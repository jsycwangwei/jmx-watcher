package com.focustech.jmx.process;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 死锁检测
 * 
 * @author wuyafeng
 */
public class JmxThreadDeadLockDetection implements DeadLockDetection {
    private final Logger log = LoggerFactory.getLogger(JmxThreadDeadLockDetection.class);

    private JmxThreadDeadLockDetection() {
    }

    public boolean detectDeadLock(MBeanServerConnection mbServer) {
        try {
            long[] ids = findDeadlockedThreads(mbServer);
            if (ids != null && ids.length > 0)
                return true;
        }
        catch (Exception e) {
            log.error("JmxThreadDeadLockCheck::checkDeadLock", e);
        }
        return false;
    }

    private long[] findDeadlockedThreads(MBeanServerConnection mbServer) throws IOException {
        ThreadMXBean threadMXBean = newPlatformMXBeanProxy(mbServer, THREAD_MXBEAN_NAME, ThreadMXBean.class);
        if (supportsLockUsage(mbServer) && threadMXBean.isSynchronizerUsageSupported()) {
            return threadMXBean.findDeadlockedThreads();
        }
        else {
            return threadMXBean.findMonitorDeadlockedThreads();
        }
    }

    private boolean supportsLockUsage(MBeanServerConnection mbServer) {
        try {
            ObjectName objName = new ObjectName(THREAD_MXBEAN_NAME);
            if (hasPlatformMXBeans(mbServer)) {
                MBeanOperationInfo[] mopis = mbServer.getMBeanInfo(objName).getOperations();
                // 查找是否有检查死锁的操作
                for (MBeanOperationInfo op : mopis) {
                    if (op.getName().equals("findDeadlockedThreads")) {
                        return true;
                    }
                }
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
        catch (IntrospectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (ReflectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (IOException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        return false;
    }

    private boolean hasPlatformMXBeans(MBeanServerConnection mbServer) {
        try {
            return mbServer.isRegistered(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME));
        }
        catch (MalformedObjectNameException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (IOException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }
    // lazy initialization holder class模式实现单例
    private static class SingletonHolder {
        private static JmxThreadDeadLockDetection instance = new JmxThreadDeadLockDetection();
    }

    public static JmxThreadDeadLockDetection getInstance() {
        return SingletonHolder.instance;
    }
}
