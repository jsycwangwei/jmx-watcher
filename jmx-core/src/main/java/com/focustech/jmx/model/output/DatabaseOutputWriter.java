package com.focustech.jmx.model.output;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxDatabaseDAO;
import com.focustech.jmx.DAO.JmxSampleDAO;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Result;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxDatabase;
import com.focustech.jmx.po.JmxSample;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.service.ThreadDumpService;
import com.focustech.jmx.util.LogUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class DatabaseOutputWriter extends BaseOutputWriter {

    private JmxSampleDAO jmxSampleDAO;
    private JmxDatabaseDAO jmxDatabaseDAO;
    private JmxApplicationDAO jmxApplicationDAO;

    public DatabaseOutputWriter(ImmutableList<String> typeNames, Map<String, Object> settings) {
        super(typeNames, false, false, settings);
        jmxSampleDAO = SpringUtils.getBean(JmxSampleDAO.class);
        jmxDatabaseDAO = SpringUtils.getBean(JmxDatabaseDAO.class);
        jmxApplicationDAO = SpringUtils.getBean(JmxApplicationDAO.class);
    }

    public void validateSetup(Server server, Query query) throws ValidationException {

    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {
        JmxDatabase jmxDatabase = new JmxDatabase();
        jmxDatabase.setAppId(server.getAppId());
        jmxDatabase.setHostId(server.getHostId());
        jmxDatabase.setAbnormalFlg(abnormalFlg);
        jmxDatabase.setAddTime(new Date());

        if (query.getObj().equals("com.alibaba.druid:type=DruidDataSourceStat")) { // 临时支持druid数据源
            jmxDatabase.setActiveCount(NumberUtils.toInt(resultMap.get("ActiveCount")));
            jmxDatabase.setIdleCount(NumberUtils.toInt(resultMap.get("PoolingCount")));
            jmxDatabase.setFailConnCount(NumberUtils.toInt(resultMap.get("ErrorCount")));
            jmxDatabaseDAO.insertDatabaseInfo(jmxDatabase);

            return;
        }

        List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(query.getObj());
        if (CollectionUtils.isEmpty(list))
            return;
        for (JmxSample s : list) {
            if (!resultMap.containsKey(s.getAlias()))
                continue;
            if (s.getAlias().contains("active")) {
                jmxDatabase.setActiveCount(NumberUtils.toInt(resultMap.get(s.getAlias())));
            }
            if (s.getAlias().contains("idle")) {
                jmxDatabase.setIdleCount(NumberUtils.toInt(resultMap.get(s.getAlias())));
            }
            if (s.getAlias().contains("fail")) {
                jmxDatabase.setFailConnCount(NumberUtils.toInt(resultMap.get(s.getAlias())));
            }
        }
        jmxDatabaseDAO.insertDatabaseInfo(jmxDatabase);

    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        return SpringUtils.getBean(ThreadDumpService.class).getParamtersByTypeAndName(appId, "threshold", "database");
    }

    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {
        JmxDatabaseDAO jmxDatabaseDAO = SpringUtils.getBean(JmxDatabaseDAO.class);
        JmxDatabase dbInfo = jmxDatabaseDAO.getDBInfoByPrimaryKey(query.getId());
        if (null == dbInfo || dbInfo.getAbnormalFlg() != 1) {
            JmxApplication app = jmxApplicationDAO.selectAppByAppId(server.getAppId());
            LogUtils.logDumpErrorInfo("App:" + app.getAppName() + ";Host:" + server.getHost() + "[jmx监测到数据库连接异常]");
            new ThreadDumpScheduler().execute(mbeanServer,
                    buildJmxDumpInfo(server, JmxThreadDumpInfo.DB_CONN_HIGH, JmxThreadDumpInfo.DB_CONN_HIGH_STR));
        }
    }

    @Override
    protected Map<String, String> convertResultsToMap(ImmutableList<Result> results, Query query) throws Exception {
        if (query.getObj().equals("com.alibaba.druid:type=DruidDataSourceStat")) { // 临时支持druid数据源
            Map<String, String> hash = Maps.newHashMap();
            for (final Result result : results) {
                ImmutableMap<String, Object> map = result.getValues();
                if (map.keySet().size() > 5) {
                    hash.put("ActiveCount", map.get("ActiveCount").toString());
                    hash.put("PoolingCount", map.get("PoolingCount").toString());
                    hash.put("ErrorCount", map.get("ErrorCount").toString());
                    return hash;
                }
            }
        }

        return super.convertResultsToMap(results, query);
    }

}
