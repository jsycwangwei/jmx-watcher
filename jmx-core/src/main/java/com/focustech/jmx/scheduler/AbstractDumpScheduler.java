package com.focustech.jmx.scheduler;

import javax.management.MBeanServerConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.po.JmxThreadDumpInfo;

public abstract class AbstractDumpScheduler implements DumpScheduler {
    protected Log log = LogFactory.getLog(LogCategory.JOBS.toString());

    public void execute(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo) {
        try {
            Scheduler scheduler = createScheduler();
            startSchedluer(scheduler);
            schedulerJob(scheduler, mbeanServer, dumpInfo);
        }
        catch (SchedulerException e) {
            log.error("AbstractDumpScheduler::execute", e);
        }
    }

    protected Scheduler createScheduler() throws SchedulerException {
        return StdSchedulerFactory.getDefaultScheduler();

    }

    protected void startSchedluer(Scheduler scheduler) throws SchedulerException {
        createScheduler().start();
    }

    protected abstract void schedulerJob(Scheduler scheduler, MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo)
            throws SchedulerException;
}
