package com.focustech.jmx.process.threads;

import javax.management.MBeanServerConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.process.JmxQueryProcessor;

/**
 * 查询线程
 * 
 * @author wangwei-ww
 */
public class ProcessQueryThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(LogCategory.JOBS.toString());

    private final MBeanServerConnection mbeanServer;
    private final Server server;
    private final Query query;

    public ProcessQueryThread(MBeanServerConnection mbeanServer, Server server, Query query) {
        this.mbeanServer = mbeanServer;
        this.server = server;
        this.query = query;
    }

    public void run() {
        try {
            new JmxQueryProcessor().processQuery(this.mbeanServer, this.server, this.query);
        }
        catch (Exception e) {
            log.error("Error executing query: " + query, e);
            throw new RuntimeException(e);
        }
    }
}
