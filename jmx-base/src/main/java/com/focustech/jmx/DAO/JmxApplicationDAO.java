package com.focustech.jmx.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxApplication;

public interface JmxApplicationDAO {

    List<String> selectAllMonitorProjects();

    /***
     * 查询所有的项目（不论是否被监控）
     * 
     * @return
     */
    List<String> selectAllProjects();

    /***
     * 查询项目下被监控的应用
     * 
     * @param projectName
     * @return
     */
    List<JmxApplication> selectMonitorAppsByProject(@Param("project") String projectName);

    /***
     * 查询项目下所有存在的应用
     * 
     * @param projectName
     * @return
     */
    List<JmxApplication> selectAppsByProject(@Param("project") String projectName);

    JmxApplication selectAppByAppId(@Param("appid") Integer appid);

}
