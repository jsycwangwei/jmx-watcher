package com.focustech.jmx.process;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
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

import com.focustech.jmx.model.IPrintWriter;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.model.output.ConsolePrintWriter;

/**
 * 远程dump处理
 * 
 * @author wangwei-ww
 */
public class JmxDumpProcessor {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private MBeanServerConnection serverConn;
    private ThreadMXBean tmbean;
    private ObjectName objname;
    private IPrintWriter writer = new ConsolePrintWriter();// default console print
    private String findDeadlocksMethodName = "findDeadlockedThreads";
    private boolean canDumpLocks = true;

    /**
     * 执行远程dump
     * 
     * @param mbeanServer
     * @param server
     */
    public void processDump(MBeanServerConnection mbeanServer, Server server) {
        try {
            serverConn = mbeanServer;
            tmbean = newPlatformMXBeanProxy(serverConn, THREAD_MXBEAN_NAME, ThreadMXBean.class);
            try {
                objname = new ObjectName(THREAD_MXBEAN_NAME);
            }
            catch (MalformedObjectNameException e) {
                InternalError ie = new InternalError(e.getMessage());
                ie.initCause(e);
                throw ie;
            }
            parseMBeanInfo();
        }
        catch (Exception e) {
            log.error("processDump error " + server.getHost());
        }
    }

    public void threadDump() {
        if (canDumpLocks) {
            if (tmbean.isObjectMonitorUsageSupported() && tmbean.isSynchronizerUsageSupported()) {
                dumpThreadInfoWithLocks();
            }
        }
        else {
            dumpThreadInfo();
        }
    }

    private void dumpThreadInfo() {
        writer.write("Full Java thread dump");
        long[] tids = tmbean.getAllThreadIds();
        ThreadInfo[] tinfos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
        for (ThreadInfo ti : tinfos) {
            printThreadInfo(ti);
        }
    }

    /**
     * Prints the thread dump information with locks info to System.out.
     */
    private void dumpThreadInfoWithLocks() {
        writer.write("Full Java thread dump with locks info");

        ThreadInfo[] tinfos = tmbean.dumpAllThreads(true, true);
        for (ThreadInfo ti : tinfos) {
            printThreadInfo(ti);
            LockInfo[] syncs = ti.getLockedSynchronizers();
            printLockInfo(syncs);
        }
        writer.write("");
    }

    private static String INDENT = "    ";

    private void printThreadInfo(ThreadInfo ti) {
        // print thread information
        printThread(ti);

        // print stack trace with locks
        StackTraceElement[] stacktrace = ti.getStackTrace();
        MonitorInfo[] monitors = ti.getLockedMonitors();
        for (int i = 0; i < stacktrace.length; i++) {
            StackTraceElement ste = stacktrace[i];
            writer.write(INDENT + "at " + ste.toString());
            for (MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == i) {
                    writer.write(INDENT + "  - locked " + mi);
                }
            }
        }
        writer.write("");// just for changing next row
    }

    private void printThread(ThreadInfo ti) {
        StringBuilder sb =
                new StringBuilder("\"" + ti.getThreadName() + "\"" + " tid=" + ti.getThreadId() + " in "
                        + ti.getThreadState());
        if (ti.getLockName() != null) {
            sb.append(" on lock=" + ti.getLockName());
        }
        if (ti.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (ti.isInNative()) {
            sb.append(" (running in native)");
        }

        writer.write(sb.toString());
        if (ti.getLockOwnerName() != null) {
            writer.write(INDENT + " owned by " + ti.getLockOwnerName() + " tid=" + ti.getLockOwnerId());
        }
    }

    private void printLockInfo(LockInfo[] locks) {
        writer.write(INDENT + "Locked synchronizers: count = " + locks.length);
        for (LockInfo li : locks) {
            writer.write(INDENT + "  - " + li);
        }
        writer.write("");
    }

    /**
     * Checks if any threads are deadlocked. If any, print the thread dump information.
     */
    public boolean findDeadlock() {
        long[] tids;
        if (findDeadlocksMethodName.equals("findDeadlockedThreads") && tmbean.isSynchronizerUsageSupported()) {
            tids = tmbean.findDeadlockedThreads();
            if (tids == null) {
                return false;
            }

            writer.write("Deadlock found :-");
            ThreadInfo[] infos = tmbean.getThreadInfo(tids, true, true);
            for (ThreadInfo ti : infos) {
                printThreadInfo(ti);
                printLockInfo(ti.getLockedSynchronizers());
                writer.write("");
            }
        }
        else {
            tids = tmbean.findMonitorDeadlockedThreads();
            if (tids == null) {
                return false;
            }
            ThreadInfo[] infos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
            for (ThreadInfo ti : infos) {
                // print thread information
                printThreadInfo(ti);
            }
        }

        return true;
    }

    private void parseMBeanInfo() throws IOException {
        try {
            MBeanOperationInfo[] mopis = serverConn.getMBeanInfo(objname).getOperations();

            boolean found = false;
            for (MBeanOperationInfo op : mopis) {
                if (op.getName().equals(findDeadlocksMethodName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                findDeadlocksMethodName = "findMonitorDeadlockedThreads";
                canDumpLocks = false;
            }
        }
        catch (IntrospectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (InstanceNotFoundException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (ReflectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }

    public void generateWriter(IPrintWriter writer) {
        this.writer = writer;
    }

    public void closeWriter() {
        if (writer != null) {
            writer.endwrite();
        }
    }

}
