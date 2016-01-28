package com.focustech.jmx.model.output;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxSampleDAO;
import com.focustech.jmx.DAO.JmxThreadDAO;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxSample;
import com.focustech.jmx.po.JmxThread;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.service.ThreadDumpService;
import com.focustech.jmx.util.LogUtils;
import com.google.common.collect.ImmutableList;

public class ThreadOutputWriter extends BaseOutputWriter {

    private JmxSampleDAO jmxSampleDAO;

    private JmxThreadDAO jmxThreadDAO;

    private JmxApplicationDAO jmxApplicationDAO;

    public ThreadOutputWriter(ImmutableList<String> typeNames, Map<String, Object> settings) {
        super(typeNames, false, false, settings);
        jmxSampleDAO = SpringUtils.getBean(JmxSampleDAO.class);
        jmxThreadDAO = SpringUtils.getBean(JmxThreadDAO.class);
        jmxApplicationDAO = SpringUtils.getBean(JmxApplicationDAO.class);
    }

    @Override
    public Map<String, Object> getSettings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validateSetup(Server server, Query query) throws ValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {

        // 登记采样数据数据
        JmxThread jmxThread = new JmxThread();
        jmxThread.setHostId(server.getHostId());
        jmxThread.setAppId(server.getAppId());
        jmxThread.setAddTime(new Date());

        List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(query.getObj());
        if (CollectionUtils.isEmpty(list))
            return;

        for (JmxSample s : list) {
            if (!resultMap.containsKey(s.getAlias()))
                continue;
            int size = NumberUtils.toInt(resultMap.get(s.getAlias()));
            if (abnormalFlg == 1) {
                jmxThread.setAbnormalFlg(abnormalFlg);
            }
            if (s.getAlias().contains("active")) {
                jmxThread.setActiveCount(size);
            }
            if (s.getAlias().contains("idle")) {
                jmxThread.setIdleCount(size);
            }
            if (s.getAlias().contains("total")) {
                jmxThread.setCurTotalCount(size);
            }
        }

        jmxThreadDAO.saveInfo(jmxThread);

    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        return SpringUtils.getBean(ThreadDumpService.class).getParamtersByTypeAndName(appId, "threshold", "thread");
    }

    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {
        JmxThread thread = jmxThreadDAO.getThreadInfoByPrimaryKeyAndDomain(query.getId());
        if (null == thread || thread.getAbnormalFlg() != 1) {
            JmxApplication app = jmxApplicationDAO.selectAppByAppId(server.getAppId());
            LogUtils.logDumpErrorInfo("App:" + app.getAppName() + ";Host:" + server.getHost() + "[jmx监测到线程异常]");
            new ThreadDumpScheduler().execute(mbeanServer,
                    buildJmxDumpInfo(server, JmxThreadDumpInfo.THREAD_NUM_HIGH, JmxThreadDumpInfo.THREAD_NUM_HIGH_STR));
        }
    }

}
