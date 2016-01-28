package com.focustech.jmx.po;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class JmxConnectionFail {
    private Long recId;
    private Integer hostId;
    private Date addTime;
    private String reason;
    private Integer appId;
    private static int maxLength = 200;

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    /***
     * 截取reason长度
     * 
     * @return
     */
    public String getCutReason() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(reason) && reason.length() > maxLength) {
            sb.append(reason.substring(0, maxLength)).append("...");
            return sb.toString();
        }
        return reason;
    }

}
