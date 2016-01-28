package com.focustech.jmx.management;

import com.focustech.jmx.exceptions.LifecycleException;

public interface ManagedJmxWatcherProcessMXBean {

    void start() throws LifecycleException;

    void stop() throws LifecycleException;

    String getQuartPropertiesFile();

    void setQuartPropertiesFile(String quartPropertiesFile);

    int getRunPeriod();

    void setRunPeriod(int runPeriod);

}
