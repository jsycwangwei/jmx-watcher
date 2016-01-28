package com.focustech.jmx.jobs;

import javax.management.MBeanServerConnection;

import org.springframework.stereotype.Component;

import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;

/**
 * 正常的线程的dump job,即正常情况的线程定时dump操作
 *
 * @author wuyafeng
 */
@Component
public class ThreadNormalDumpJob extends AbstractBaseJob {

    @Override
    protected void doExecute(Server server, MBeanServerConnection mbeanServer) {
        try {
            new Thread(new Task(mbeanServer, buildDumpInfo(server))).start();
        }
        catch (Exception e) {
            throw new RuntimeException("ThreadNormalDumpJob::doExecute server:" + server, e);
        }
    }

    class Task implements Runnable {
        MBeanServerConnection mbeanServer;
        JmxThreadDumpInfo dumpInfo;

        Task(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo) {
            this.mbeanServer = mbeanServer;
            this.dumpInfo = dumpInfo;
            this.dumpInfo.setReasonType(JmxThreadDumpInfo.NORMAL_DUMP);
        }

        @Override
        public void run() {
            new ThreadDumpScheduler().execute(mbeanServer, dumpInfo);
        }

    }

    @Override
    protected Server buildQueries(JmxServer jmxServer, Server server) {
        // TODO Auto-generated method stub
        return server;
    }

}
