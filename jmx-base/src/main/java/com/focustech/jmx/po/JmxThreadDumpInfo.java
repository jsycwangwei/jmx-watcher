package com.focustech.jmx.po;

import java.util.Date;

public class JmxThreadDumpInfo {
    public static final String THREAD_NUM_HIGH = "jvm_thread_count";
    public static final String THREAD_DEAD_LOCK = "dead_lock";
    public static final String CPU_USAGE_HIGH = "cpu_usage";
    public static final String NORMAL_DUMP = "normal_dump";
    public static final String HAND_DUMP = "hand_dump";
    public static final String THREAD_NUM_HIGH_STR = "线程数超过阀值";
    public static final String THREAD_DEAD_LOCK_STR = "线程死锁";
    public static final String CPU_USAGE_HIGH_STR = "cpu负载超过阀值";
    public static final String NORMAL_DUMP_STR = "正常dump";
    public static final String HAND_DUMP_STR = "手动实时dump";
    public static final String DB_CONN_HIGH = "db_conn_count";
    public static final String DB_CONN_HIGH_STR = "数据库连接数过高";
    public static final String JVM_CMS_ABNORMAL = "jvm_cms_abnormal";
    public static final String JVM_CMS_ABNORMAL_STR = "JVMCMS异常";
    public static final String JVM_MEMORY_ABNORMAL = "jvm_memory_abnormal";
    public static final String JVM_MEMORY_ABNORMAL_STR = "内存异常";
    private Long recId;
    private Integer hostId;
    private String filePath;
    private String reasonType;
    private Date addTime;
    private String reason;
    private Integer appId;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
