package com.focustech.jmx.process;

import static java.lang.management.ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.RUNTIME_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxCpuUsageDetection implements CpuUsageDetection {
    private final Logger log = LoggerFactory.getLogger(JmxThreadDeadLockDetection.class);

    private JmxCpuUsageDetection() {
    }

    @SuppressWarnings("restriction")
    @Override
    /**
     * 计算CPU的使用率
     */
    public float calcuateCpuUsage(MBeanServerConnection mbServer) {
        try {
            RuntimeMXBean rmBean = newPlatformMXBeanProxy(mbServer, RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
            OperatingSystemMXBean osBean =
                    newPlatformMXBeanProxy(mbServer, OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
            com.sun.management.OperatingSystemMXBean sunOSMBean = getSunOperatingSystemMXBean(mbServer);
            if (null == rmBean || null == osBean || null == sunOSMBean)
                return 0;
            long prevUpTime = rmBean.getUptime();//JAva虚拟机的运行时间
            long prevProcessCpuTime = sunOSMBean.getProcessCpuTime();//cpu运行时间
            long nCpus = osBean.getAvailableProcessors();//cpu的数量
            TimeUnit.SECONDS.sleep(10);
            long cpuCountTime = sunOSMBean.getProcessCpuTime() - prevProcessCpuTime;
            long osCountTime = rmBean.getUptime() - prevUpTime;
            return Math.min(99F, cpuCountTime / (osCountTime * 1000000F * nCpus));
        }
        catch (Exception e) {
            log.error("JmxCpuUsageDetection::calcuateCpuUsage", e);
        }
        return 0;
    }

    @SuppressWarnings("restriction")
    private com.sun.management.OperatingSystemMXBean getSunOperatingSystemMXBean(MBeanServerConnection mbServer)
            throws IOException {
        try {//OPERATING_SYSTEM_MXBEAN_NAME
            ObjectName objName = new ObjectName(OPERATING_SYSTEM_MXBEAN_NAME);
            if (mbServer.isInstanceOf(objName, "com.sun.management.OperatingSystemMXBean")) {
                return newPlatformMXBeanProxy(mbServer, OPERATING_SYSTEM_MXBEAN_NAME,
                        com.sun.management.OperatingSystemMXBean.class);
            }
        }
        catch (MalformedObjectNameException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (InstanceNotFoundException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        return null;
    }

    // lazy initialization holder class模式实现单例
    private static class SingletonHolder {
        private static JmxCpuUsageDetection instance = new JmxCpuUsageDetection();
    }

    public static JmxCpuUsageDetection getInstance() {
        return SingletonHolder.instance;
    }

}
