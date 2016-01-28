package com.focustech.jmx.po;

import java.util.Date;

public class JmxCpu {
    private Integer recId;
    private Integer hostId;
    private Integer appId;
    private float cpuUsage;
    private Date addTime;
    private int abnormalFlg;

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public int getAbnormalFlg() {
        return abnormalFlg;
    }

    public void setAbnormalFlg(int abnormalFlg) {
        this.abnormalFlg = abnormalFlg;
    }

}
