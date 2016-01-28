package com.focustech.jmx.model;

import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import com.focustech.jmx.exceptions.LifecycleException;
import com.google.common.collect.ImmutableList;

/**
 * 查询输出接口
 * 
 * @author wangwei-ww
 */
public interface OutputWriter {

    void start() throws LifecycleException;

    void stop() throws LifecycleException;

    void doWrite(Server server, Query query, ImmutableList<Result> results, MBeanServerConnection mbeanServer)
            throws Exception;

    Map<String, Object> getSettings();

    void setSettings(Map<String, Object> settings);

    void validateSetup(Server server, Query query) throws ValidationException;

}
