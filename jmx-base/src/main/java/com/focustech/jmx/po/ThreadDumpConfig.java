package com.focustech.jmx.po;

import java.util.Date;

public class ThreadDumpConfig {

    private Long recId;
    private Integer hostId;
    private Integer appId;
    private Date addTime;
    private Date updateTime;
    /*
     * 计算cpu使用率,数据采样的间隔时间
     */
    private Long cupSampleInterval;
    /*
     * 线程dump操作的时间间隔
     */
    private Long dumpRepeatInterval;

    /*
     * 线程dump操作的次数
     */
    private long dumpRepeatCount;

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public Integer getServerId() {
        return hostId;
    }

    public void setServerId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCupSampleInterval() {
        return cupSampleInterval;
    }

    public void setCupSampleInterval(Long cupSampleInterval) {
        this.cupSampleInterval = cupSampleInterval;
    }

    public Long getDumpRepeatInterval() {
        return dumpRepeatInterval;
    }

    public void setDumpRepeatInterval(Long dumpRepeatInterval) {
        this.dumpRepeatInterval = dumpRepeatInterval;
    }

    public long getDumpRepeatCount() {
        return dumpRepeatCount;
    }

    public void setDumpRepeatCount(long dumpRepeatCount) {
        this.dumpRepeatCount = dumpRepeatCount;
    }

}
