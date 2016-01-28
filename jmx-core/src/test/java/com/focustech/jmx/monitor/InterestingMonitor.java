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

public class InterestingMonitor {

    // private static final JsonPrinter printer = new JsonPrinter(System.out);

    public static void main(String[] args) throws Exception {

        Server.Builder serverBuilder = Server.builder().setHost("192.168.10.25").setPort("1099").setNumQueryThreads(2);

        InterfaceOutputWriter interOutWriter =
                new InterfaceOutputWriter(ImmutableList.<String> of(), Collections.<String, Object> emptyMap());

        Query q =
                Query.builder().setObj("resin:type=ConnectionPool,name=jdbc/mlanDatabase").addAttr("ConnectionCount")
                        .addAttr("ConnectionWaitTime").addOutputWriters(interOutWriter).build();
        serverBuilder.addQuery(q);

        Query q2 =
                Query.builder().setObj("resin:type=ThreadPool").addAttr("ThreadActiveCount").addAttr("ThreadIdleCount")
                        .addAttr("ThreadCount").addOutputWriters(interOutWriter).build();
        serverBuilder.addQuery(q2);

        JmxProcess process = new JmxProcess(serverBuilder.build());
        // printer.prettyPrint(process);
        JmxWatcherConfiguration configure = new JmxWatcherConfiguration();
        configure.setRunPeriod(30);
        Injector injector = Guice.createInjector(new JmxWatcherModule(configure));
        JmxWatcher transformer = injector.getInstance(JmxWatcher.class);

        transformer.executeStandalone(process);
    }

}
