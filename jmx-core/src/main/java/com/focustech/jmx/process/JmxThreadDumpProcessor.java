package com.focustech.jmx.process;

import static java.lang.management.ManagementFactory.RUNTIME_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServerConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.model.IPrintWriter;
import com.focustech.jmx.model.output.FilePrintWriter;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.service.ThreadService;

/**
 * 负责dump线程信息,只支持JDK1.6以及以上的版本
 * 
 * @author wuyafeng
 */

public class JmxThreadDumpProcessor implements DumpProcessor {

    private final Logger log = LoggerFactory.getLogger(JmxThreadDeadLockDetection.class);
    private final IPrintWriter writer = new FilePrintWriter();

    @Override
    public void dump(MBeanServerConnection mbeanServer, JmxThreadDumpInfo dumpInfo) {
        try {
            ThreadMXBean threadMXBean = newPlatformMXBeanProxy(mbeanServer, THREAD_MXBEAN_NAME, ThreadMXBean.class);
            long[] ids = threadMXBean.getAllThreadIds();
            if (null == ids || ids.length <= 0) {
                return;
            }
            StringBuilder sb = new StringBuilder(4096);
            printStartTime(sb);
            printJVM(sb, mbeanServer);
            ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
            threadInfos = sortThreadInfos(threadInfos);
            for (ThreadInfo threadInfo : threadInfos) {
                if (null != threadInfo) {
                    printThread(sb, threadMXBean, threadInfo);
                }
            }
            // 保存dump文件
            writer.write(sb.toString(), System.getProperty("dump_path") + dumpInfo.getFilePath());
            // 保存dump信息
            SpringUtils.getBean(ThreadService.class).saveDumpInfo(dumpInfo);
        }
        catch (Exception e) {
            log.error("JmxThreadDumpProcessor::dump.dump所有线程", e);
        }
    }

    /**
     * 按照线程状态排序，RUNNABLE->WAITTING->TIMED_WATTING
     *
     * @param tinfos
     */
    private ThreadInfo[] sortThreadInfos(ThreadInfo[] tinfos) {
        List<ThreadInfo> activeList = new ArrayList<ThreadInfo>();
        List<ThreadInfo> otherList = new ArrayList<ThreadInfo>();
        for (ThreadInfo t : tinfos) {
            if (t.getThreadState() == State.RUNNABLE) {
                activeList.add(t);
                continue;
            }
            otherList.add(t);
        }
        activeList.addAll(otherList);
        tinfos = activeList.toArray(new ThreadInfo[tinfos.length]);

        return tinfos;
    }

    /**
     * 打印dump开始的时间
     * 
     * @param sb
     */
    private void printStartTime(final StringBuilder sb) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(df.format(new Date()) + "\n");
    }

    /**
     * 打印JVM的信息
     * 
     * @param sb
     * @param mbeanServer
     */
    private void printJVM(final StringBuilder sb, MBeanServerConnection mbeanServer) {
        sb.append("Full thread dump ");
        Properties prop = getSysProperties(mbeanServer);
        if (null != prop) {
            sb.append(prop.getProperty("java.vm.name") + "(" + prop.getProperty("java.vm.version") + " "
                    + prop.getProperty("java.vm.info") + "):");
            sb.append("\n");
        }
    }

    private void printThread(final StringBuilder sb, final ThreadMXBean threadMXBean, final ThreadInfo threadInfo) {
        MonitorInfo[] monitors = null;
        if (threadMXBean.isObjectMonitorUsageSupported()) {
            monitors = threadInfo.getLockedMonitors();
        }
        sb.append("\n\"" + threadInfo.getThreadName() + "\" - Thread tid=" + threadInfo.getThreadId() + "\n");
        sb.append("  java.lang.Thread.State: " + threadInfo.getThreadState());
        // if (threadInfo.getThreadState() == State.RUNNABLE) {
        // sb.append(" cpuTime:").append(threadMXBean.getThreadCpuTime(threadInfo.getThreadId()));
        // }
        sb.append("\n");
        int index = 0;
        for (StackTraceElement st : threadInfo.getStackTrace()) {
            LockInfo lockInfo = threadInfo.getLockInfo();
            String lockOwner = threadInfo.getLockOwnerName();
            sb.append("\tat " + st.toString() + "\n");
            if (index == 0) {
                if ("java.lang.Object".equals(st.getClassName()) && "wait".equals(st.getMethodName())) {
                    if (lockInfo != null) {
                        sb.append("\t - waiting on");
                        printLock(sb, lockInfo);
                        sb.append("\n");
                    }
                }
                else if (lockInfo != null) {
                    if (lockOwner == null) {
                        sb.append("\t- parking to wait for ");
                        printLock(sb, lockInfo);
                        sb.append("\n");
                    }
                    else {
                        sb.append("\t- waiting to lock ");
                        printLock(sb, lockInfo);
                        sb.append(" owned by \"" + lockOwner + "\"+ t@" + threadInfo.getLockOwnerId());
                        sb.append("\n");
                    }
                }
            }
            printMonitors(sb, monitors, index);
            index++;
        }
        StringBuilder jnisb = new StringBuilder();
        printMonitors(jnisb, monitors, -1);
        if (jnisb.length() > 0) {
            sb.append("  JNI locked monitors:\n");
            sb.append(jnisb);
        }
        if (threadMXBean.isSynchronizerUsageSupported()) {
            sb.append("\n Locked ownable synchronizers:");
            LockInfo[] synchronizers = threadInfo.getLockedSynchronizers();
            if (synchronizers == null || synchronizers.length == 0) {
                sb.append("\n\t- None\n");
            }
            else {
                for (LockInfo li : synchronizers) {
                    sb.append("\n\t- locked ");
                    printLock(sb, li);
                    sb.append("\n");
                }
            }
        }
    }

    /**
     * 打印锁信息
     * 
     * @param sb
     * @param lockInfo
     */
    private void printLock(StringBuilder sb, LockInfo lockInfo) {
        String id = Integer.toHexString(lockInfo.getIdentityHashCode());
        String className = lockInfo.getClassName();
        sb.append("<" + id + "> (a " + className + ")");
    }

    /**
     * 打印监视器信息
     * 
     * @param sb
     * @param monitors
     * @param index
     */
    private void printMonitors(final StringBuilder sb, final MonitorInfo[] monitors, final int index) {
        if (monitors != null) {
            for (MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == index) {
                    sb.append("\t- locked");
                    printLock(sb, mi);
                    sb.append("\n");
                }
            }
        }
    }

    /**
     * 获取jvm的系统信息
     * 
     * @param mbeanServer
     * @return
     */
    private Properties getSysProperties(MBeanServerConnection mbeanServer) {
        try {
            RuntimeMXBean runMxBean = newPlatformMXBeanProxy(mbeanServer, RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
            if (null != runMxBean) {
                Properties prop = new Properties();
                prop.putAll(runMxBean.getSystemProperties());
                return prop;
            }
        }
        catch (Exception e) {
            log.error("JmxThreadDumpProcessor::getSysProperties", e);
        }
        return null;
    }

    /**
     * dump单个线程的信息
     */
    @Override
    public String dump(MBeanServerConnection mbeanServer, long threadId) {
        try {
            ThreadMXBean threadMXBean = newPlatformMXBeanProxy(mbeanServer, THREAD_MXBEAN_NAME, ThreadMXBean.class);
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, Integer.MAX_VALUE);
            if (threadInfo != null) {
                StringBuilder sb = new StringBuilder(128);
                printStartTime(sb);
                printJVM(sb, mbeanServer);
                printThread(sb, threadMXBean, threadInfo);
                return sb.toString();
            }
        }
        catch (IOException e) {
            log.error("JmxThreadDumpProcessor::dump dump单个线程信息 threadId=" + threadId, e);
        }
        return null;
    }

}
