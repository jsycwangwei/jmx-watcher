package com.focustech.jmx.po;

import java.util.Date;

public class JmxMemory {
    private Integer recId;
    private Integer hostId;
    private Integer appId;
    private int heapMemorySize = -1;
    private int noHeapMemorySize = -1;
    private int edenSize = -1;
    private int oldSize = -1;
    private int permSize = -1;
    private int survivorSize = -1;
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

    public int getHeapMemorySize() {
        return heapMemorySize;
    }

    public void setHeapMemorySize(int heapMemorySize) {
        this.heapMemorySize = heapMemorySize;
    }

    public int getNoHeapMemorySize() {
        return noHeapMemorySize;
    }

    public void setNoHeapMemorySize(int noHeapMemorySize) {
        this.noHeapMemorySize = noHeapMemorySize;
    }

    public int getEdenSize() {
        return edenSize;
    }

    public void setEdenSize(int edenSize) {
        this.edenSize = edenSize;
    }

    public int getOldSize() {
        return oldSize;
    }

    public void setOldSize(int oldSize) {
        this.oldSize = oldSize;
    }

    public int getPermSize() {
        return permSize;
    }

    public int getSurvivorSize() {
        return survivorSize;
    }

    public void setSurvivorSize(int survivorSize) {
        this.survivorSize = survivorSize;
    }

    public void setPermSize(int permSize) {
        this.permSize = permSize;
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
