package com.focustech.jmx.model.output;

import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Result;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.util.LogUtils;
import com.google.common.collect.ImmutableList;

/**
 * 接口数据输出
 * 
 * @author wangwei-ww
 */
public class InterfaceOutputWriter extends BaseOutputWriter {

    public InterfaceOutputWriter(ImmutableList<String> typeNames, Map<String, Object> settings) {
        super(typeNames, false, false, settings);
    }

    /**
     * 定时数据获取
     */
    public void internalWrite(Server server, final Query query, ImmutableList<Result> results) {

        for (final Result result : results) {
            final Map<String, Object> resultValues = result.getValues();
            if (resultValues != null) {
                for (final Entry<String, Object> values : resultValues.entrySet()) {
                    if (NumberUtils.isNumeric(values.getValue())) {
                        // String alias = genServerKey(server);
                        // LogUtils.jmxLog.info("server:" + alias);
                        LogUtils.jmxLog.info("attributeName:" + result.getAttributeName());
                        LogUtils.jmxLog.info("key:" + values.getKey());
                        LogUtils.jmxLog.info("value:" + values.getValue());

                    }
                }
            }
        }

        // JmxDumpProcessor dump = new JmxDumpProcessor();
        // dump.processDump(mbeanServer, server);
    }

    public void validateSetup(Server server, Query query) throws ValidationException {

    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {
        // TODO Auto-generated method stub

    }

}
