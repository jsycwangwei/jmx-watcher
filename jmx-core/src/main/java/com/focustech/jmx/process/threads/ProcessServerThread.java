package com.focustech.jmx.process.threads;

import javax.management.MBeanServerConnection;

import com.focustech.jmx.model.Server;
import com.focustech.jmx.util.JmxUtils;

/**
 * 负责直接和server对接的线程
 * 
 * @author wangwei-ww
 */
public class ProcessServerThread implements Runnable {
    private final Server server;
    private final MBeanServerConnection mbeanServer;

    public ProcessServerThread(Server server, MBeanServerConnection mbeanServer) {
        this.server = server;
        this.mbeanServer = mbeanServer;
    }

    public void run() {
        try {
            JmxUtils.processServer(this.server, this.mbeanServer);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
