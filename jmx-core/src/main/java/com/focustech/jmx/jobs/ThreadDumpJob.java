package com.focustech.jmx.jobs;

import java.io.File;
import java.util.Calendar;

import javax.management.MBeanServerConnection;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.process.JmxThreadDumpProcessor;

/**
 * 负责线程dump的操作,在需要的dump的时候由ThreadDumpScheduler进行调用,比如发现线程数过高,死锁......
 * 
 * @author wuyafeng
 */
public class ThreadDumpJob implements Job {
    @Override
    public void execute(JobExecutionContext executionContext) throws JobExecutionException {
        MBeanServerConnection mbeanServer =
                (MBeanServerConnection) executionContext.getJobDetail().getJobDataMap().get("mbeanServer");
        JmxThreadDumpInfo dumpInfo =
                (JmxThreadDumpInfo) executionContext.getJobDetail().getJobDataMap().get("dumpInfo");
        // 设置dump文件的存放路径
        dumpInfo.setFilePath(getThreadDumpFilePath());

        new JmxThreadDumpProcessor().dump(mbeanServer, dumpInfo);
    }

    /**
     * 获取线程dump文件的路径，thread_dump_file加上当前时间的年-月-日.时分秒毫秒组成
     * 
     * @return
     */
    private String getThreadDumpFilePath() {
        StringBuilder sb = new StringBuilder("thread_dump_file");
        sb.append(File.separator);
        Calendar calendar = Calendar.getInstance();
        sb.append("thread-dump.").append(calendar.get(Calendar.YEAR)).append("-")
                .append(calendar.get(Calendar.MONTH) + 1).append("-").append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(".").append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.MINUTE))
                .append(calendar.get(Calendar.MILLISECOND));
        return sb.toString();
    }
}
