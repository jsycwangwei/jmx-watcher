package com.focustech.jmx.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.focustech.jmx.po.JmxTaskScheduleJob;

/**
 * 计划任务执行处 [无状态]
 * 
 * @author wangwei-ww
 */
public class QuartzJobFactory implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JmxTaskScheduleJob scheduleJob = (JmxTaskScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        TaskUtils.invokMethod(scheduleJob);
    }
}
