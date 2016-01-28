package com.focustech.jmx.po;

import java.util.Date;

public class JmxCms {
    private Integer recId;
    private Integer hostId;
    private Integer appId;
    private Long startTime;
    private Date addTime;
    private int collectionSize=-1;
    private int duration=-1;
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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public int getCollectionSize() {
        return collectionSize;
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAbnormalFlg() {
        return abnormalFlg;
    }

    public void setAbnormalFlg(int abnormalFlg) {
        this.abnormalFlg = abnormalFlg;
    }
}
