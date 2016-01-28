package com.focustech.jmx.guice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

import javax.management.remote.JMXConnector;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.client.JmxWatcherConfiguration;
import com.focustech.jmx.connections.JMXConnectionParams;
import com.focustech.jmx.connections.JmxConnectionFactory;
import com.focustech.jmx.management.ManagedGenericKeyedObjectPool;
import com.google.common.io.Closer;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class JmxWatcherModule extends AbstractModule {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JmxWatcherConfiguration configuration;

    public JmxWatcherModule(JmxWatcherConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<GenericKeyedObjectPool<JMXConnectionParams, JMXConnector>>() {}).toInstance(
                getObjectPool(new JmxConnectionFactory(), JmxConnectionFactory.class.getSimpleName()));
    }

    @Provides
    JmxWatcherConfiguration jmxWatcherConfiguration() {
        return configuration;
    }

    @Provides
    @Inject
    Scheduler scheduler(JmxWatcherConfiguration configuration, GuiceJobFactory jobFactory) throws SchedulerException,
            IOException {
        StdSchedulerFactory serverSchedFact = new StdSchedulerFactory();
        Closer closer = Closer.create();
        try {
            InputStream stream;
            if (configuration == null || configuration.getQuartPropertiesFile() == null) {
                // stream =
                // closer.register(JmxWatcherModule.class
                // .getResourceAsStream("src/main/resources/quartz.properties"));
                stream = closer.register(new FileInputStream("src/main/resources/quartz.properties"));
            }
            else {
                stream = closer.register(new FileInputStream(configuration.getQuartPropertiesFile()));
            }
            serverSchedFact.initialize(stream);
        }
        catch (Throwable t) {
            throw closer.rethrow(t);
        }
        finally {
            closer.close();
        }
        Scheduler scheduler = serverSchedFact.getScheduler();
        scheduler.setJobFactory(jobFactory);
        return scheduler;
    }

    private <K, V> GenericKeyedObjectPool getObjectPool(KeyedPoolableObjectFactory<K, V> factory, String poolName) {
        GenericKeyedObjectPool<K, V> pool = new GenericKeyedObjectPool<K, V>(factory);
        pool.setTestOnBorrow(true);
        pool.setMaxActive(-1);
        pool.setMaxIdle(-1);
        pool.setTimeBetweenEvictionRunsMillis(1000 * 60 * 5);
        pool.setMinEvictableIdleTimeMillis(1000 * 60 * 5);

        try {
            ManagedGenericKeyedObjectPool mbean = new ManagedGenericKeyedObjectPool(pool, poolName);
            ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, mbean.getObjectName());
        }
        catch (Exception e) {
            log.error("Could not register mbean for pool [{}]", poolName, e);
        }

        return pool;
    }

}
