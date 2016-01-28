package com.focustech.jmx.pojo;

import com.focustech.jmx.po.JmxConnectionFail;

public class JmxConnectionFailPOJO extends JmxConnectionFail {
    private String appName;
    private String hostIp;
    private Integer count;

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

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

}
