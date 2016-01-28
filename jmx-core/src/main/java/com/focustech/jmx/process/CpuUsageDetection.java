package com.focustech.jmx.process;

import javax.management.MBeanServerConnection;

public interface CpuUsageDetection {
    public float calcuateCpuUsage(MBeanServerConnection mbServer);
}
