package com.focustech.jmx.po;

import java.util.Date;

public class JmxDatabase {
    private Integer recId;
    private Integer hostId;
    private Integer appId;
    private int activeCount = -1;
    private int idleCount = -1;
    private int failConnCount = -1;
    private Date addTime;
    private int abnormalFlg=-1;

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

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getIdleCount() {
        return idleCount;
    }

    public void setIdleCount(int idleCount) {
        this.idleCount = idleCount;
    }

    public int getFailConnCount() {
        return failConnCount;
    }

    public void setFailConnCount(int failConnCount) {
        this.failConnCount = failConnCount;
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

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAppId() {
        return appId;
    }

}
