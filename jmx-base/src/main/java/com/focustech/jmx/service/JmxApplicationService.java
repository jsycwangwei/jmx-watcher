package com.focustech.jmx.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.po.JmxApplication;

/***
 * AppService
 * 
 * @author liulin
 */
@Service
public class JmxApplicationService {
    @Autowired
    JmxApplicationDAO appDAO;

    public List<String> selectAllMonitorProjects() {
        return appDAO.selectAllMonitorProjects();

    }

    public List<String> selectAllProjects() {
        return appDAO.selectAllProjects();
    }

    public List<JmxApplication> selectMonitorAppByProject(String projectName) {
        return appDAO.selectMonitorAppsByProject(projectName);
    }

    public List<JmxApplication> selectAppByProject(String projectName) {
        return appDAO.selectAppsByProject(projectName);
    }

    public JmxApplication selectAppByAppId(int appid) {
        return appDAO.selectAppByAppId(appid);
    }

}
