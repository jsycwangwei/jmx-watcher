package com.focustech.jmx.po;

/**
 * 应用监控数据采用模型
 * 
 * @author wangwei-ww
 */
public class JmxModel {
    private Integer recId;
    private Integer appId;
    private Integer sampleId;

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

}
