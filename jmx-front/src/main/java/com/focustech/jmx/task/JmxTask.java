package com.focustech.jmx.task;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.po.JmxTaskScheduleJob;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.service.JobTaskService;

@Component
public class JmxTask extends HttpServlet {

    private static final long serialVersionUID = -7321115403819288870L;
    private Log log = LogFactory.getLog(LogCategory.SERVICE.toString());

    @Override
    public void init(ServletConfig config) {
        try {     	
            JobTaskService jobTaskService = SpringUtils.getBean(JobTaskService.class);

            Scheduler scheduler = jobTaskService.getSchedulerFactoryBean().getScheduler();

            // 这里获取任务信息数据
            List<JmxTaskScheduleJob> jobList = jobTaskService.getScheduleJobMapper().getAll();

            for (JmxTaskScheduleJob job : jobList) {
                jobTaskService.addJob(job);
            }
        }
        catch (Exception e) {
            //se.printStackTrace();
            log.error("JmxTask init error",e);
        }
    }
}
