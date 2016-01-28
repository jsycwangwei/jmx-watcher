package com.focustech.jmx.po;

import java.util.Date;

/**
 * 通用采样信息
 * 
 * @author wangwei-ww
 */
public class JmxNormal {
    private Integer recId;
    private Integer hostId;
    private Integer appId;
    private String objName;
    private Date addTime;
    private Date updateTime;
    private String normalVal;

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

    public String getNormalVal() {
        return normalVal;
    }

    public void setNormalVal(String normalVal) {
        this.normalVal = normalVal;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

}
