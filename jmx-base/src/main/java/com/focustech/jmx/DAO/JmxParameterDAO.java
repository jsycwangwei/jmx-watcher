package com.focustech.jmx.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxParameter;

/**
 * 获取系统各自参数设置的阀值
 *
 * @author wuyafeng
 */
public interface JmxParameterDAO {
    /**
     * 获取线程信息的阀值
     *
     * @return
     */
    public String getThreadThreshold(@Param("appId") Integer appId, @Param("key") String key);

    /**
     * 获取cpu使用率的阀值
     *
     * @return
     */
    public String getCpuUsageThreshold(@Param("appId") Integer appId, @Param("key") String key);

    void updateParamValue(@Param("recid") long recid, @Param("paramvalue") String value);

    void updateParamValueByKey(@Param("appid") long appid, @Param("paramtype") String paramType,
            @Param("paramname") String paramname, @Param("paramkey") String paramkey, @Param("paramvalue") String value);

    void insertParam(@Param("appid") Integer appid, @Param("paramtype") String paramType,
            @Param("paramname") String paramname, @Param("paramkey") String paramkey, @Param("paramvalue") String value);

    int getParamCount(@Param("appid") Integer appid, @Param("paramtype") String paramType,
            @Param("paramname") String paramname, @Param("paramkey") String paramkey);

    /**
     * 获取dump文件存放的路径的目录
     *
     * @return
     */
    public String getDumpFileDir();

    List<JmxParameter> selectParamtersByAppid(@Param("appId") Integer appId);

    List<JmxParameter> selectParamtersByType(@Param("appId") Integer appId, @Param("paramType") String paramType);

    List<String> selectKeysByName(@Param("type") String type, @Param("name") String name);
    // int countByTypeAndTimeRange(@Param("type") String type, @Param("hours") int hours);

    /***
     * 查询参数，如果参数为null表示，不限制此条件
     *
     * @param appId
     * @param paramType
     * @param paramkey
     * @param paramname
     * @return
     */
    JmxParameter selectParamter(@Param("appId") Integer appId, @Param("paramType") String paramType,
            @Param("paramkey") String paramkey, @Param("paramname") String paramname);

    List<JmxParameter> getParamtersByTypeAndName(@Param("appId") Integer appId, @Param("paramType") String paramType, @Param("paramName") String paramName);
}
