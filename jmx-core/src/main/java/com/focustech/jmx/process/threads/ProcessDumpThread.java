package com.focustech.jmx.process.threads;

import javax.management.MBeanServerConnection;

import com.focustech.jmx.model.Server;

/**
 * 远程dump线程
 * 
 * @author wangwei-ww
 */
public class ProcessDumpThread implements Runnable {

    private final MBeanServerConnection mbeanServer;
    private final Server server;

    public ProcessDumpThread(MBeanServerConnection mbeanServer, Server server) {
        this.mbeanServer = mbeanServer;
        this.server = server;
    }

    public void run() {

    }

}
