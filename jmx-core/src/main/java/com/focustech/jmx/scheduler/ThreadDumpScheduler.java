package com.focustech.jmx.scheduler;

import java.util.Date;

import javax.management.MBeanServerConnection;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

import com.focustech.jmx.jobs.ThreadDumpJob;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.service.ThreadService;

public class ThreadDumpScheduler extends AbstractDumpScheduler {
    @Override
    protected void schedulerJob(Scheduler scheduler, MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo)
            throws SchedulerException {
        ThreadService threadService = SpringUtils.getBean("threadService");

        JobDetail jobDetail = JobBuilder.newJob(ThreadDumpJob.class).build();

        jobDetail.getJobDataMap().put("mbeanServer", mbeanServer);
        jobDetail.getJobDataMap().put("dumpInfo", dumpInfo);

        SimpleScheduleBuilder schedBuilder =
                SimpleScheduleBuilder.repeatSecondlyForTotalCount(threadService.getRepeatCount(dumpInfo.getAppId()),
                        threadService.getRepeatInterval(dumpInfo.getAppId()));

        SimpleTrigger trigger = TriggerBuilder.newTrigger().withSchedule(schedBuilder).startAt(new Date()).build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

}
