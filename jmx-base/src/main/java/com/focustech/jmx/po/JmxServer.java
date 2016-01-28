package com.focustech.jmx.po;

import java.util.Date;

/**
 * jmx监控的server
 * 
 * @author wangwei-ww
 */
public class JmxServer {
    private Long recId;
    private Integer appId;
    private Integer hostId;
    private int jmxPort;
    private int jmxStatus;
    private String hostIp;
    private String dataSource;
    private Date addTime;

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public int getJmxStatus() {
        return jmxStatus;
    }

    public void setJmxStatus(int jmxStatus) {
        this.jmxStatus = jmxStatus;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

}
