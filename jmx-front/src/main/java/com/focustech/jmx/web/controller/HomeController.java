package com.focustech.jmx.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.focustech.jmx.po.JmxTaskScheduleJob;
import com.focustech.jmx.service.JmxApplicationService;
import com.focustech.jmx.service.JobTaskService;

@Controller
@RequestMapping("/")
public class HomeController {
    // 日志记录器
    public final Logger log = Logger.getLogger(this.getClass());
    @Autowired
    private JobTaskService taskService;
    @Autowired
    JmxApplicationService appService;

    @RequestMapping(value = {"/"})
    public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
        Object o = request.getSession().getAttribute("SIGN");
        if (null == o) {
            return "redirect:/login.html";
        }
        else {
            Boolean isLogin = (Boolean) o;
            if (!isLogin)
                return "redirect:/login.html";
        }

        return "redirect:/jmx/overview.html";
    }

    @RequestMapping(value = {"/jmx/overview.html"})
    public String overview(Model model) {
        setCommonServerSelect(model, true, true);
        model.addAttribute("ignore", true);// 主要表示是否需要隐藏server区的下拉列表

        return "/dashboard";
    }

    @RequestMapping(value = "/jmx/serverlist.html")
    public String serverlist(Model model) {
        setCommonServerSelect(model, true, true);
        return "/serverlist";
    }

    @RequestMapping(value = "/jmx/appSetting.html")
    public String appSetting(Model model) {
        setCommonServerSelect(model, false, true);

        return "/appsetting";
    }

    @RequestMapping(value = "/jmx/serverSetting.html")
    public String serverSetting(Model model) {
        setCommonServerSelect(model, false, false);
        return "/serversetting";
    }

    @RequestMapping(value = "/jmx/jobSetting.html")
    public String jobSetting(Model model) {
        setCommonServerSelect(model, false, true);
        List<JmxTaskScheduleJob> taskList = taskService.getAllTask();
        List<JmxTaskScheduleJob> commontask = new ArrayList<JmxTaskScheduleJob>();
        List<JmxTaskScheduleJob> normaltask = new ArrayList<JmxTaskScheduleJob>();
        for (JmxTaskScheduleJob job : taskList) {
            if (JmxTaskScheduleJob.COMMONJOBGROUP.equalsIgnoreCase(job.getJobGroup())) {
                commontask.add(job);
            }
            else {
                normaltask.add(job);
            }
        }
        model.addAttribute("commontask", commontask);
        model.addAttribute("normaltask", normaltask);
        return "/jobsetting";
    }

    @RequestMapping(value = "/jmx/dbsourceSetting.html")
    public String dbSourceSetting(Model model) {
        setCommonServerSelect(model, false, true);
        List<JmxTaskScheduleJob> taskList = taskService.getAllTask();
        List<JmxTaskScheduleJob> commontask = new ArrayList<JmxTaskScheduleJob>();
        List<JmxTaskScheduleJob> normaltask = new ArrayList<JmxTaskScheduleJob>();
        for (JmxTaskScheduleJob job : taskList) {
            if (JmxTaskScheduleJob.COMMONJOBGROUP.equalsIgnoreCase(job.getJobGroup())) {
                commontask.add(job);
            }
            else {
                normaltask.add(job);
            }
        }
        model.addAttribute("commontask", commontask);
        model.addAttribute("normaltask", normaltask);
        return "/dbsourcesetting";
    }

    /***
     * 项目下拉框数据
     * 
     * @param model
     * @param isneedAll 是否需要第一行的 所有项目的提示
     * @param isOnlyMonitor 是否应用于只展现监控的服务
     * @return
     */
    private List<String> setCommonServerSelect(Model model, boolean isneedAll, boolean isOnlyMonitor) {
        List<String> projects = new ArrayList<String>();
        if (isOnlyMonitor) {
            projects.addAll(appService.selectAllMonitorProjects());
        }
        else {
            projects.addAll(appService.selectAllProjects());
        }
        model.addAttribute("projects", projects);
        model.addAttribute("isneedAll", isneedAll);
        return projects;
    }
}
