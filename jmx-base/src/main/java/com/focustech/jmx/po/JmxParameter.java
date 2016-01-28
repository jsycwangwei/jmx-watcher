package com.focustech.jmx.po;

public class JmxParameter {

    private Long recId;
    private Integer appId;
    private String paramType;
    private String paramName;
    private String paramKey;
    private String paramValue;
    private int paramValueOrder;
    private String paramDesc;

    public final static String PARAMTYPE_THRESHOLD = "threshold";// 阀值参数类型
    public final static String PARAMTYPE_PARAM = "param";// 自定义参数配置类型
    public final static String PARAMTYPE_EXCEPTION = "exception";// 异常dump的Type定义的类型

    // database 阀值配置name 和 key
    public final static String DB_PARAM_NAME = "database";
    public final static String DB_PARAM_KEY_ACTIVECOUNT = "database_active";
    public final static String DB_PARAM_KEY_IDLECOUNT = "database_idle";
    public final static String DB_PARAM_KEY_FAILCOUNT = "database_fail";

    // memory 阀值配置name 和 key
    public final static String MEMORY_PARAM_NAME = "memory_";
    public final static String MEMORY_KEY_HEAP = "memory_heapMemory";
    public final static String MEMORY_KEY_EDEN = "memory_eden";
    public final static String MEMORY_KEY_SURVIVOR = "memory_surivor";
    public final static String MEMORY_KEY_OLD = "memory_old";

    // thread 阀值配置name 和 key
    public final static String THREAD_PARAM_NAME = "thread_";
    public final static String THREAD_KEY_ACTIVE = "thread_active_count";
    public final static String THREAD_KEY_IDLE = "thread_idle_count";
    public final static String THREAD_KEY_TOTAL = "thread_total_count";

    // gc 阀值配置name 和 key
    public final static String CMS_PARAM_NAME = "cms_";
    public final static String CMS_KEY_COLLECTION = "collection_size";
    public final static String CMS_KEY_DURATION = "duration";

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public int getParamValueOrder() {
        return paramValueOrder;
    }

    public void setParamValueOrder(int paramValueOrder) {
        this.paramValueOrder = paramValueOrder;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

}
