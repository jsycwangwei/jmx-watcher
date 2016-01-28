package com.focustech.jmx.po;

import java.util.Date;

public class JmxThread {
    private Integer recId;
    /*
     * server id
     */
    private Integer hostId;
    /*
     * 活动线程数
     */
    private Integer activeCount;
    /*
     * 空闲线程数
     */
    private Integer idleCount;
    /*
     * 当前线程的总数
     */
    private Integer curTotalCount;
    /*
     * 异常标志
     */
    private int abnormalFlg;
    /*
     * 记录增加时间
     */
    private Date addTime;

    /*
     * 应用id
     */
    private Integer appId;

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAppId() {
        return this.appId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }

    public Integer getIdleCount() {
        return idleCount;
    }

    public void setIdleCount(Integer idleCount) {
        this.idleCount = idleCount;
    }

    public Integer getCurTotalCount() {
        return curTotalCount;
    }

    public void setCurTotalCount(Integer curTotalCount) {
        this.curTotalCount = curTotalCount;
    }

    public int getAbnormalFlg() {
        return abnormalFlg;
    }

    public void setAbnormalFlg(int abnormalFlg) {
        this.abnormalFlg = abnormalFlg;
    }

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

}
