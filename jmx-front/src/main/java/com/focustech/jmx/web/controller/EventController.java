package com.focustech.jmx.web.controller;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.focustech.jmx.face.EventResult;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.po.JmxTaskScheduleJob;
import com.focustech.jmx.service.JmxServerService;
import com.focustech.jmx.service.JobTaskService;
import com.focustech.jmx.service.SystemThresholdService;

@Controller
@RequestMapping("/event")
public class EventController {
    protected Log log = LogFactory.getLog(LogCategory.CONTROLLER.toString());
    @Autowired
    SystemThresholdService systemThresholdService;
    @Autowired
    private JobTaskService taskService;
    @Autowired
    JmxServerService jmxServerService;

    @RequestMapping("monitor/addServer")
    @ResponseBody
    public EventResult addMonitorServer(String server, String app) {
        EventResult result = new EventResult();
        boolean monitor = true;
        try {
            Integer serverid = NumberUtils.toInt(server, 0);
            Integer appid = NumberUtils.toInt(app, 0);
            jmxServerService.updateServerMonitor(serverid, appid, monitor);
        }
        catch (Exception e) {
            result.setFailureResult("添加监控失败：" + e.getMessage());
            log.error("添加监控异常：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping("monitor/cancelServer")
    @ResponseBody
    public EventResult cancelMonitorServer(String server, String app) {
        EventResult result = new EventResult();
        boolean monitor = false;
        try {
            Integer serverid = NumberUtils.toInt(server, 0);
            Integer appid = NumberUtils.toInt(app, 0);
            jmxServerService.updateServerMonitor(serverid, appid, monitor);
        }
        catch (Exception e) {
            result.setFailureResult("取消监控失败：" + e.getMessage());
            log.error("取消监控异常：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping("params/saveParam")
    @ResponseBody
    public EventResult saveParam(Integer appid, String paramname, String paramtype, String paramkey, String value) {
        EventResult result = new EventResult();
        try {
            systemThresholdService.saveParam(appid, paramtype, paramname, paramkey, value);
        }
        catch (Exception e) {
            result.setFailureResult("保存失败：" + e.getMessage());
            log.error("参数保存异常：" + e.getMessage());
            return result;
        }
        return result;
    }

    @RequestMapping("job/save")
    @ResponseBody
    public EventResult saveJob(JmxTaskScheduleJob scheduleJob) {
        EventResult result = new EventResult();
        String cron = scheduleJob.getCronExpression();
        try {
            @SuppressWarnings("unused")
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        }
        catch (Exception e) {
            result.setFailureResult("cron表达式有误，不能被解析！");
            return result;
        }

        try {
            JmxTaskScheduleJob orginJob = taskService.getTaskById(scheduleJob.getJobId());
            if (!orginJob.getJobStatus().trim().equals(scheduleJob.getJobStatus().trim())) {// 日终状态改变
                if (JmxTaskScheduleJob.STATUS_RUNNING.equals(scheduleJob.getJobStatus().trim())) {// 日终被设置为启动
                    taskService.addJob(scheduleJob);
                }
                else {// 日终被设置为禁止
                    taskService.deleteJob(scheduleJob);
                }
            }
            else {// 日终状态没有改变
                if (JmxTaskScheduleJob.STATUS_RUNNING.equals(orginJob.getJobStatus().trim())) {// 日终为启动
                    if (taskService.isNeedReloadJob(scheduleJob, orginJob)) {// 需要重新加载
                        taskService.reloadJob(scheduleJob, orginJob);
                    }
                    else if (!orginJob.getCronExpression().trim().equals(scheduleJob.getCronExpression().trim())) {
                        taskService.updateCron(scheduleJob.getJobId(), cron);
                    }
                }
            }
        }
        catch (SchedulerException e) {
            result.setFailureResult("重新加载日终异常");
            log.error("更新job配置异常" + e.getMessage());
            return result;
        }
        try {
            taskService.updateByPrimaryKeySelective(scheduleJob);
        }
        catch (Exception e) {
            result.setFailureResult("更新保存失败");
            log.error("更新job配置异常：" + e.getMessage());
            return result;
        }

        return result;
    }
}
