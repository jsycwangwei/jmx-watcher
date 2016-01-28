package com.focustech.jmx.connections;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.springframework.stereotype.Component;

import com.focustech.jmx.connections.JMXConnectionParams;
import com.focustech.jmx.connections.JmxConnectionFactory;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.management.ManagedGenericKeyedObjectPool;

@Component
public class JmxConnectionPool {

    private final Log log = LogFactory.getLog(LogCategory.JOBS.toString());

    private GenericKeyedObjectPool<JMXConnectionParams, JMXConnector> jmxPool;

    @PostConstruct
    private void init() {
        // 初始化Jmx连接线程池
        jmxPool = getObjectPool(new JmxConnectionFactory(), JmxConnectionFactory.class.getSimpleName());
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
            log.error("Could not register mbean for pool [{}] " + poolName, e);
        }

        return pool;
    }

    public GenericKeyedObjectPool<JMXConnectionParams, JMXConnector> getJmxPool() {
        return jmxPool;
    }
}
