package com.focustech.jmx.process;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.focustech.jmx.JmxWatcher;
import com.focustech.jmx.client.JmxWatcherConfiguration;
import com.focustech.jmx.exceptions.LifecycleException;
import com.focustech.jmx.management.ManagedJmxWatcherProcessMXBean;
import com.focustech.jmx.management.ManagedObject;

/**
 * JMX监控管理类处理
 * 
 * @author wangwei-ww
 */
public class ManagedJmxWatcherProcess implements ManagedJmxWatcherProcessMXBean, ManagedObject {

    /**
     * The object name.
     */
    private ObjectName objectName;

    /**
     * The proc.
     */
    private JmxWatcher proc;

    private final JmxWatcherConfiguration configuration;

    /**
     * The Constructor.
     * 
     * @param proc the proc
     * @param configuration
     */
    public ManagedJmxWatcherProcess(JmxWatcher proc, JmxWatcherConfiguration configuration) {
        this.proc = proc;
        this.configuration = configuration;
    }

    public void start() throws LifecycleException {
        this.proc.start();
    }

    public void stop() throws LifecycleException {
        this.proc.stop();
    }

    public String getQuartPropertiesFile() {
        return configuration.getQuartPropertiesFile();
    }

    public void setQuartPropertiesFile(String quartPropertiesFile) {
        configuration.setQuartPropertiesFile(quartPropertiesFile);
    }

    public int getRunPeriod() {
        return configuration.getRunPeriod();
    }

    public void setRunPeriod(int runPeriod) {
        configuration.setRunPeriod(runPeriod);
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            objectName = new ObjectName("com.focustech.jmx:Type=JmxWatcherProcess,Name=JmxWatcherProcess");
        }
        return objectName;
    }

    public void setObjectName(ObjectName objectName) throws MalformedObjectNameException {
        this.objectName = objectName;
    }

    public void setObjectName(String objectName) throws MalformedObjectNameException {
        this.objectName = ObjectName.getInstance(objectName);
    }
}
