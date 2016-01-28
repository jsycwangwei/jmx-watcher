package com.focustech.jmx.jobs;

import javax.management.MBeanServerConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxServerDAO;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.process.JmxThreadDeadLockDetection;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.util.LogUtils;

@Component
public class ThreadDeadLockDetectionJob extends AbstractBaseJob {

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
            throw new RuntimeException("ThreadDeadLockDetectionJob::doExecute server:" + server, e);
        }
    }

    class Task implements Runnable {
        private MBeanServerConnection mbeanServer;
        private JmxThreadDumpInfo dumpInfo;

        Task(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo) {
            this.mbeanServer = mbeanServer;
            this.dumpInfo = dumpInfo;
            this.dumpInfo.setReasonType(JmxThreadDumpInfo.THREAD_DEAD_LOCK);
        }

        @Override
        public void run() {
            // 如果检测到线程发生死锁,则进行线程dump操作
            if (JmxThreadDeadLockDetection.getInstance().detectDeadLock(mbeanServer)) {
                JmxApplication app = jmxApplicationDAO.selectAppByAppId(dumpInfo.getAppId());
                JmxServer server = jmxServerDAO.selectDetailInfo(dumpInfo.getHostId(), app.getAppId());
                LogUtils.logDumpErrorInfo("App:" + app.getAppName() + ";Host:" + server.getHostIp() + "[jmx检测到死锁]");
                new ThreadDumpScheduler().execute(mbeanServer, dumpInfo);
            }
        }
    }

    @Override
    protected Server buildQueries(JmxServer jmxServer, Server server) {
        // TODO Auto-generated method stub
        return server;
    }

}
