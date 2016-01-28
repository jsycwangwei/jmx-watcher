package com.focustech.jmx.web.controller;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.focustech.jmx.po.JmxTaskScheduleJob;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.service.JobTaskService;
import com.focustech.jmx.task.RetObj;

@Controller
@RequestMapping("/task")
public class JobTaskController {
    // 日志记录器
    public final Logger log = Logger.getLogger(this.getClass());
    @Autowired
    private JobTaskService taskService;

    @RequestMapping("taskList")
    public String taskList(HttpServletRequest request) {
        List<JmxTaskScheduleJob> taskList = taskService.getAllTask();
        request.setAttribute("taskList", taskList);
        return "taskList";
    }

    @RequestMapping("updateCron")
    @ResponseBody
    public RetObj updateCron(HttpServletRequest request, Long jobId, String cron) {
        RetObj retObj = new RetObj();
        retObj.setFlag(false);
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        }
        catch (Exception e) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return retObj;
        }
        try {
            taskService.updateCron(jobId, cron);
        }
        catch (SchedulerException e) {
            retObj.setMsg("cron更新失败！");
            return retObj;
        }
        retObj.setFlag(true);
        return retObj;
    }

    @RequestMapping("add")
    @ResponseBody
    public RetObj taskList(HttpServletRequest request, JmxTaskScheduleJob scheduleJob) {
        RetObj retObj = new RetObj();
        retObj.setFlag(false);
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        }
        catch (Exception e) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return retObj;
        }
        Object obj = null;
        try {
            if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
                obj = SpringUtils.getBean(scheduleJob.getSpringId());
            }
            else {
                Class clazz = Class.forName(scheduleJob.getBeanClass());
                obj = clazz.newInstance();
            }
        }
        catch (Exception e) {
            // do nothing.........
        }
        if (obj == null) {
            retObj.setMsg("未找到目标类！");
            return retObj;
        }
        else {
            Class clazz = obj.getClass();
            Method method = null;
            try {
                method = clazz.getMethod(scheduleJob.getMethodName(), null);
            }
            catch (Exception e) {
                // do nothing.....
            }
            if (method == null) {
                retObj.setMsg("未找到目标方法！");
                return retObj;
            }
        }
        try {
            taskService.addTask(scheduleJob);
        }
        catch (Exception e) {
            e.printStackTrace();
            retObj.setFlag(false);
            retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
            return retObj;
        }

        retObj.setFlag(true);
        return retObj;
    }
}
