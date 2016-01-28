package com.focustech.jmx.model.output;

import java.util.Date;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import net.sf.json.JSONObject;

import com.focustech.jmx.DAO.JmxNormalDAO;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxNormal;
import com.focustech.jmx.quartz.SpringUtils;
import com.google.common.collect.ImmutableList;

public class NormalOutputWriter extends BaseOutputWriter {

    private JmxNormalDAO jmxNormalDAO;

    public NormalOutputWriter(ImmutableList<String> typeNames, boolean booleanAsNumber, Boolean debugEnabled,
            Map<String, Object> settings) {
        super(typeNames, booleanAsNumber, debugEnabled, settings);
        jmxNormalDAO = SpringUtils.getBean(JmxNormalDAO.class);
    }

    @Override
    public void validateSetup(Server server, Query query) throws ValidationException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {
        JmxNormal jmxNormal = new JmxNormal();
        jmxNormal.setAppId(server.getAppId());
        jmxNormal.setHostId(server.getHostId());
        jmxNormal.setObjName(query.getObj());
        JSONObject obj = new JSONObject();
        for (String k : resultMap.keySet()) {
            obj.put(k, resultMap.get(k));
        }
        jmxNormal.setNormalVal(obj.toString());
        jmxNormal.setAddTime(new Date());
        jmxNormal.setUpdateTime(new Date());

        jmxNormalDAO.insertNormal(jmxNormal);

        return;
    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        return null;
    }

    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {

    }

}
