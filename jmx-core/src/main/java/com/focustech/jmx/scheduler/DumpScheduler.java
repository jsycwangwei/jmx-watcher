package com.focustech.jmx.scheduler;

import javax.management.MBeanServerConnection;

import com.focustech.jmx.po.JmxThreadDumpInfo;

public interface DumpScheduler {
    /**
     * dump操作
     */
    public void execute(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo);
}
