package com.focustech.jmx.model.output;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxCmsDAO;
import com.focustech.jmx.DAO.JmxSampleDAO;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxCms;
import com.focustech.jmx.po.JmxSample;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.service.ThreadDumpService;
import com.focustech.jmx.util.ConvertUtils;
import com.focustech.jmx.util.LogUtils;
import com.google.common.collect.ImmutableList;

public class CmsOutputWriter extends BaseOutputWriter {

    private JmxSampleDAO jmxSampleDAO;

    private JmxCmsDAO jmxCmsDAO;

    private JmxApplicationDAO jmxApplicationDAO;

    public CmsOutputWriter(ImmutableList<String> typeNames, Map<String, Object> settings) {
        super(typeNames, false, false, settings);
        jmxSampleDAO = SpringUtils.getBean(JmxSampleDAO.class);
        jmxCmsDAO = SpringUtils.getBean(JmxCmsDAO.class);
        jmxApplicationDAO = SpringUtils.getBean(JmxApplicationDAO.class);
    }

    @Override
    public void validateSetup(Server server, Query query) throws ValidationException {

    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {

        JmxCms cms = new JmxCms();
        cms.setAppId(server.getAppId());
        cms.setHostId(server.getHostId());
        cms.setAddTime(new Date());
        cms.setAbnormalFlg(abnormalFlg);
        long before = 0l;
        long after = 0l;
        long startTime = 0l;

        List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(query.getObj());
        if (CollectionUtils.isEmpty(list))
            return;
        for (JmxSample s : list) {
            if (!resultMap.containsKey(s.getAlias()))
                continue;
            if (s.getAlias().contains("duration")) {
                cms.setDuration(NumberUtils.toInt(resultMap.get(s.getAlias())));
                continue;
            }
            if (s.getAlias().contains("start")) {
                startTime = NumberUtils.toLong(resultMap.get(s.getAlias()));
                cms.setStartTime(startTime);
                continue;
            }
            if (s.getAlias().contains("before")) {
                before = NumberUtils.toLong(resultMap.get(s.getAlias()));
                continue;
            }
            if (s.getAlias().contains("after")) {
                after = NumberUtils.toLong(resultMap.get(s.getAlias()));
            }
        }
        cms.setCollectionSize(ConvertUtils.convertByte2KB(before - after));

        int c = jmxCmsDAO.selectLastGcTime(server.getAppId(), server.getHostId(), startTime);
        if (c > 0)
            return;

        jmxCmsDAO.insertLastGcInfo(cms);
    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        return SpringUtils.getBean(ThreadDumpService.class).getParamtersByTypeAndName(appId, "threshold", "cms");
    }

    /**
     * 目前cms只建了一个Build所以直接DUmp
     */
    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {
        JmxApplication app = jmxApplicationDAO.selectAppByAppId(server.getAppId());
        LogUtils.logDumpErrorInfo("App:" + app.getAppName() + ";Host:" + server.getHost() + "[jmx监测到cms GC异常]");
        new ThreadDumpScheduler().execute(mbeanServer,
                buildJmxDumpInfo(server, JmxThreadDumpInfo.JVM_CMS_ABNORMAL, JmxThreadDumpInfo.JVM_CMS_ABNORMAL_STR));

    }
}
