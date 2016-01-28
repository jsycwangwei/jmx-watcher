package com.focustech.jmx.model.output;

import static com.focustech.jmx.model.PropertyResolver.resolveMap;
import static com.focustech.jmx.model.output.Settings.getBooleanSetting;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.management.MBeanServerConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.exceptions.LifecycleException;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.OutputWriter;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Result;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.model.naming.KeyUtils;
import com.focustech.jmx.model.result.BooleanAsNumberValueTransformer;
import com.focustech.jmx.model.result.IdentityValueTransformer;
import com.focustech.jmx.model.result.ValueTransformer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@NotThreadSafe
public abstract class BaseOutputWriter implements OutputWriter {

    protected final Log log = LogFactory.getLog(LogCategory.SERVICE.toString());

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String OUTPUT_FILE = "outputFile";
    public static final String TEMPLATE_FILE = "templateFile";
    public static final String BINARY_PATH = "binaryPath";
    public static final String DEBUG = "debug";
    public static final String TYPE_NAMES = "typeNames";

    private ImmutableList<String> typeNames;
    private boolean debugEnabled;
    private Map<String, Object> settings;
    private final ValueTransformer valueTransformer;

    public BaseOutputWriter(ImmutableList<String> typeNames, boolean booleanAsNumber, Boolean debugEnabled,
            Map<String, Object> settings) {
        // resolve and initialize settings first, so we cean refer to them to initialize other fields
        this.settings = resolveMap(MoreObjects.firstNonNull(settings, Collections.<String, Object> emptyMap()));

        this.typeNames = copyOf(firstNonNull(typeNames, (List<String>) this.settings.get(TYPE_NAMES),
                Collections.<String> emptyList()));
        this.debugEnabled = firstNonNull(debugEnabled, getBooleanSetting(this.settings, DEBUG), false);

        if (booleanAsNumber) {
            this.valueTransformer = new BooleanAsNumberValueTransformer(0, 1);
        }
        else {
            this.valueTransformer = new IdentityValueTransformer();
        }
    }

    protected <T> T firstNonNull(@Nullable T first, @Nullable T second, @Nullable T third) {
        return first != null ? first : (second != null ? second : checkNotNull(third));
    }

    /**
     * @deprecated Don't use the settings Map, please extract necessary bits at construction time.
     */
    @Deprecated
    public Map<String, Object> getSettings() {
        return settings;
    }

    /**
     * @deprecated Initialize settings in constructor only please.
     */
    @Deprecated
    public void setSettings(Map<String, Object> settings) {
        this.settings = resolveMap(settings);
        if (settings.containsKey(DEBUG)) {
            this.debugEnabled = getBooleanSetting(settings, DEBUG);
        }
        if (settings.containsKey(TYPE_NAMES)) {
            this.typeNames = copyOf((List<String>) settings.get(TYPE_NAMES));
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    /**
     * Given a typeName string, get the first match from the typeNames setting. In other words, suppose you have:
     * <p/>
     * typeName=name=PS Eden Space,type=MemoryPool
     * <p/>
     * If you addTypeName("name"), then it'll retrieve 'PS Eden Space' from the string
     */
    protected String getConcatedTypeNameValues(String typeNameStr) {
        return KeyUtils.getConcatedTypeNameValues(this.getTypeNames(), typeNameStr);
    }

    /**
     * A do nothing method.
     */

    public void start() throws LifecycleException {
        // Do nothing.
    }

    public void stop() throws LifecycleException {
        // Do nothing.
    }

    public final void doWrite(Server server, Query query, ImmutableList<Result> results,
            MBeanServerConnection mbeanServer) {
        if (CollectionUtils.isEmpty(results))
            return;
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            resultMap = convertResultsToMap(results, query);
        }
        catch (Exception e) {
            log.error("BaseoutputWriter:doWrite()convertResultsToMap error", e);
            e.printStackTrace();
        }
        // 检查采样信息是否超过阀值
        int abnormalFlg = checkNeedDumpInfo(server, resultMap);
        // dump线程信息
        if (abnormalFlg == 1) {
            try {
                dumpThreadInfo(mbeanServer, server, query);
            }
            catch (Exception e) {
                log.error("baseoutputwriter:dumpThreadInfo error", e);
                e.printStackTrace();
            }
        }
        log.error("BaseoutputWriter:doWrite() hostId="+server.getHostId()+",appId="+server.getAppId());
        internalWrite(abnormalFlg, server, query, resultMap, mbeanServer);
    }

    protected abstract void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer);

    private static final class ResultValuesTransformer implements Function<Result, Result> {

        private final ValueTransformer valueTransformer;

        private ResultValuesTransformer(ValueTransformer valueTransformer) {
            this.valueTransformer = valueTransformer;
        }

        @Nullable
        public Result apply(@Nullable Result input) {
            if (input == null) {
                return null;
            }
            return new Result(input.getEpoch(), input.getAttributeName(), input.getClassName(),
                    input.getClassNameAlias(), input.getTypeName(),
                    Maps.transformValues(input.getValues(), valueTransformer));
        }
    }

    protected abstract Map<String, String> getThresholdParamList(Integer appId) throws Exception;

    protected abstract void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query)
            throws Exception;

    protected Map<String, String> convertResultsToMap(ImmutableList<Result> results, Query query) throws Exception {
        ImmutableList<Result> resultList =
                from(results).transform(new ResultValuesTransformer(valueTransformer)).toList();
        Map<String, String> map = new HashMap<String, String>();
        Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>(resultList.size());

        for (final Result result : resultList) {
            resultMap.put(result.getAttributeName(), result.getValues());
        }

        int i = 0;
        ImmutableList<String> attrs = query.getAttr();
        for (String attr : attrs) {
            final Map<String, Object> resultValues = resultMap.get(attr);
            if (resultValues != null) {
                for (final Entry<String, Object> values : resultValues.entrySet()) {
                    String key = values.getKey();
                    if (StringUtils.equals(query.getKeys().get(i), key)) {
                        String val = values.getValue() + "";
                        map.put(query.getTypeNames().toArray()[i].toString(), val);
                    }
                }
            }
            i++;
        }

        return map;
    }

    protected int checkNeedDumpInfo(Server server, Map<String, String> resultMap) {
        int abnormalFlg = 0;// 默认正常值
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            paramMap = getThresholdParamList(server.getAppId());
        }
        catch (Exception e) {
            log.error("BaseOutputWriter::checkNeedDumpInfo()  ", e);
            e.printStackTrace();
        } // 获取数据库阀值
        if (CollectionUtils.isEmpty(paramMap))
            return abnormalFlg;
        // 判断采样数据是否异常
        Iterator<String> it = paramMap.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = it.next();
            if (resultMap.containsKey(key)) {
                if (org.apache.commons.lang.math.NumberUtils.toLong(
                        resultMap.get(key)) > org.apache.commons.lang.math.NumberUtils.toLong(paramMap.get(key))) {
                    if (StringUtils.isNotEmpty(paramMap.get(key))) {
                        abnormalFlg = 1;
                    }
                    return abnormalFlg;
                }
            }

        }
        return abnormalFlg;
    }

    protected JmxThreadDumpInfo buildJmxDumpInfo(Server server, String reasonType, String reason) {
        JmxThreadDumpInfo dumpInfo = new JmxThreadDumpInfo();
        dumpInfo.setHostId(server.getHostId());
        dumpInfo.setAppId(server.getAppId());
        dumpInfo.setReasonType(reasonType);
        dumpInfo.setReason(reason);
        dumpInfo.setAddTime(new Date());
        return dumpInfo;
    }

}
