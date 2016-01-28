package com.focustech.jmx.model.output;

import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.google.common.collect.ImmutableList;

/**
 * A writer for log4J
 * 
 * @author wangwei-ww
 */
public class Log4JWriter extends BaseOutputWriter {
    private final Logger log;
    private final String logger;

    @JsonCreator
    public Log4JWriter(@JsonProperty("typeNames") ImmutableList<String> typeNames,
            @JsonProperty("booleanAsNumber") boolean booleanAsNumber, @JsonProperty("debug") Boolean debugEnabled,
            @JsonProperty("logger") String logger, @JsonProperty("settings") Map<String, Object> settings) {
        super(typeNames, booleanAsNumber, debugEnabled, settings);
        this.logger = firstNonNull(logger, (String) getSettings().get("logger"), "Log4JWriter");
        this.log = Logger.getLogger("Log4JWriter." + this.logger);
    }

    public String getLogger() {
        return logger;
    }

    public void validateSetup(Server server, Query query) throws ValidationException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {
        final List<String> typeNames = getTypeNames();

        // for (final Result result : results) {
        // final Map<String, Object> resultValues = result.getValues();
        // if (resultValues != null) {
        // for (final Entry<String, Object> values : resultValues.entrySet()) {
        // if (NumberUtils.isNumeric(values.getValue())) {
        // String alias;
        // if (server.getAlias() != null) {
        // alias = server.getAlias();
        // }
        // else {
        // alias = server.getHost() + "_" + server.getPort();
        // alias = StringUtils.cleanupStr(alias);
        // }
        //
        // MDC.put("server", alias);
        // MDC.put("metric", KeyUtils.getKeyString(server, query, result, values, typeNames, null));
        // MDC.put("value", values.getValue());
        // if (result.getClassNameAlias() != null) {
        // MDC.put("resultAlias", result.getClassNameAlias());
        // }
        // MDC.put("attributeName", result.getAttributeName());
        // MDC.put("key", values.getKey());
        // MDC.put("Epoch", String.valueOf(result.getEpoch()));
        //
        // // log.error(result.getAttributeName());
        // }
        // }
        // }
        // }

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

}
