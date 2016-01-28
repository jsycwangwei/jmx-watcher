package com.focustech.jmx.monitor;

import java.util.Collections;

import com.focustech.jmx.JmxWatcher;
import com.focustech.jmx.client.JmxWatcherConfiguration;
import com.focustech.jmx.guice.JmxWatcherModule;
import com.focustech.jmx.model.JmxProcess;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.model.output.InterfaceOutputWriter;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Set the monitor bean by hand
 * 
 * @author wangwei-ww
 */
public class TestMonitor {

    /** */
    public static void main(String[] args) throws Exception {

        Server.Builder serverBuilder = Server.builder().setHost("192.168.10.25").setPort("1099").setNumQueryThreads(2);
        Query q =
                Query.builder()
                        .setObj("resin:type=ConnectionPool,name=jdbc/mlanDatabase")
                        .addOutputWriters(
                                new InterfaceOutputWriter(ImmutableList.<String> of(), Collections
                                        .<String, Object> emptyMap())).build();
        serverBuilder.addQuery(q);

        Query q2 =
                Query.builder()
                        .setObj("resin:type=ThreadPool")
                        .addOutputWriters(
                                new InterfaceOutputWriter(ImmutableList.<String> of(), Collections
                                        .<String, Object> emptyMap())).build();
        serverBuilder.addQuery(q2);

        JmxProcess process = new JmxProcess(serverBuilder.build());
        // printer.prettyPrint(process);

        JmxWatcherConfiguration jmxTransConfig = new JmxWatcherConfiguration();
        jmxTransConfig.setRunPeriod(50);
        Injector injector = Guice.createInjector(new JmxWatcherModule(jmxTransConfig));
        JmxWatcher jmxMonitor = injector.getInstance(JmxWatcher.class);

        jmxMonitor.executeStandalone(process);

    }

}
