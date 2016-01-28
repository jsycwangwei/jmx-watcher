package com.focustech.jmx.pojo;

import com.focustech.jmx.po.JmxDatabase;

public class JmxDatabasePOJO extends JmxDatabase {
    private String appName;
    private String hostIp;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

}
