package com.focustech.jmx.jobs;

import java.util.Date;

import javax.management.MBeanServerConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxCpuDAO;
import com.focustech.jmx.DAO.JmxServerDAO;
import com.focustech.jmx.common.JmxConstants;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxCpu;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.process.JmxCpuUsageDetection;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.service.SystemThresholdService;
import com.focustech.jmx.util.LogUtils;

@Component
public class CpuUsageDetectionJob extends AbstractBaseJob {
    @Autowired
    SystemThresholdService sysThresholdService;
    @Autowired
    JmxCpuDAO jmxCpuDAO;
    @Autowired
    JmxApplicationDAO jmxApplicationDAO;
    @Autowired
    JmxServerDAO jmxServerDAO;

    @Override
    protected void doExecute(Server server, MBeanServerConnection mbeanServer) {
        try {
            new Thread(new Task(mbeanServer, buildDumpInfo(server))).start();
        }
        catch (Exception e) {
            throw new RuntimeException("CpuUsageDetectionJob::doExecute server:" + server, e);
        }
    }

    class Task implements Runnable {
        private MBeanServerConnection mbeanServer;
        private JmxThreadDumpInfo dumpInfo;

        public Task(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo) {
            this.mbeanServer = mbeanServer;
            this.dumpInfo = dumpInfo;
            this.dumpInfo.setReasonType(JmxThreadDumpInfo.CPU_USAGE_HIGH);
        }

        @Override
        public void run() {
            JmxCpu cpu = new JmxCpu();
            float cpuUsage = JmxCpuUsageDetection.getInstance().calcuateCpuUsage(mbeanServer);
            float cpuThreshold = sysThresholdService.getCpuUsageThreshold(dumpInfo.getAppId());
            // 如果cpu的使用率超过了设定的阀值,则进行线程dump操作
            if (cpuUsage > cpuThreshold) {
                JmxApplication app = jmxApplicationDAO.selectAppByAppId(dumpInfo.getAppId());
                JmxServer server = jmxServerDAO.selectDetailInfo(dumpInfo.getHostId(), app.getAppId());
                StringBuffer errMsg = new StringBuffer();
                LogUtils.logDumpErrorInfo(
                		errMsg.append("App:").append(app.getAppName()).append(";Host:").append(server.getHostIp()).append("[").append("jmx检测到CPU负载过高, 当前系统CPU阀值为").append(cpuThreshold).append("]").toString());
                cpu.setAbnormalFlg(JmxConstants.ABNORMALFLAG);
                new ThreadDumpScheduler().execute(mbeanServer, dumpInfo);
            }

            cpu.setAddTime(new Date());
            cpu.setCpuUsage(cpuUsage);
            cpu.setAppId(dumpInfo.getAppId());
            cpu.setHostId(dumpInfo.getHostId());
            jmxCpuDAO.insertCpuInfo(cpu);
        }
    }

    @Override
    protected Server buildQueries(JmxServer jmxServer, Server server) {
        return server;
    }

}
