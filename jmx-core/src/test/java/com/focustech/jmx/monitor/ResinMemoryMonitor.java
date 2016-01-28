package com.focustech.jmx.monitor;

import java.io.File;

import com.focustech.jmx.JmxWatcher;
import com.focustech.jmx.client.JmxWatcherConfiguration;
import com.focustech.jmx.guice.JmxWatcherModule;
import com.focustech.jmx.model.JmxProcess;
import com.focustech.jmx.util.JsonPrinter;
import com.focustech.jmx.util.JsonUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ResinMemoryMonitor {
    public static void main(String[] args) {
        try {
            JmxProcess process = JsonUtils.getJmxProcess(new File("resin.json"));
            new JsonPrinter(System.out).print(process);
            JmxWatcherConfiguration jmxTransConfig = new JmxWatcherConfiguration();
            jmxTransConfig.setRunPeriod(10);
            Injector injector = Guice.createInjector(new JmxWatcherModule(jmxTransConfig));
            JmxWatcher transformer = injector.getInstance(JmxWatcher.class);
            transformer.executeStandalone(process);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("done!");
    }
}
