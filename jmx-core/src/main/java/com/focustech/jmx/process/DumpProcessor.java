package com.focustech.jmx.process;

import javax.management.MBeanServerConnection;

import com.focustech.jmx.po.JmxThreadDumpInfo;

/**
 * 线程dump操作
 * 
 * @author wuyafeng
 */
public interface DumpProcessor {
    /**
     * dump所有线程,信息保存到指定的文件路径
     * 
     * @param mbeanServer
     * @param filePath,线程dump信息保存的文件路径
     */
    public void dump(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo);

    /**
     * dump单个线程,信息以string的形式返回
     * 
     * @param mbeanServer
     * @return
     */
    public String dump(MBeanServerConnection mbeanServer, long threadId);
}
