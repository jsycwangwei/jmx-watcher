package com.focustech.jmx.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import com.google.common.base.MoreObjects;

/**
 * Bean缓冲池
 */
public class ManagedGenericKeyedObjectPool implements ManagedGenericKeyedObjectPoolMBean, ManagedObject {

    /** The object name. */
    private ObjectName objectName;

    /** The default pool name. */
    private final String poolName;

    /** The pool. */
    private final GenericKeyedObjectPool pool;

    /**
     * The Constructor.
     * 
     * @param pool the pool
     * @param poolName the pool name
     */
    public ManagedGenericKeyedObjectPool(GenericKeyedObjectPool pool, String poolName) {
        this.poolName = MoreObjects.firstNonNull(poolName, "Noname");
        this.pool = pool;
    }

    /**
     * Gets the pool name.
     * 
     * @return the pool name
     */
    public String getPoolName() {
        return poolName;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            objectName =
                    new ObjectName("com.focustech.jmx:Type=GenericKeyedObjectPool,PoolName=" + this.poolName + ",Name="
                            + this.getClass().getSimpleName() + "@" + this.hashCode());
        }
        return objectName;
    }

    public void setObjectName(ObjectName objectName) throws MalformedObjectNameException {
        this.objectName = objectName;
    }

    public void setObjectName(String objectName) throws MalformedObjectNameException {
        this.objectName = ObjectName.getInstance(objectName);
    }

    public int getMaxActive() {
        return pool.getMaxActive();
    }

    public int getMaxIdle() {
        return pool.getMaxIdle();
    }

    public long getMaxWait() {
        return pool.getMaxWait();
    }

    public int getMinIdle() {
        return pool.getMinIdle();
    }

    public int getNumActive() {
        return pool.getNumActive();
    }

    public int getNumIdle() {
        return pool.getNumIdle();
    }

    public void setMaxActive(int maxActive) {
        this.pool.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        this.pool.setMaxIdle(maxIdle);
    }

    public void setMinIdle(int maxIdle) {
        this.pool.setMinIdle(maxIdle);
    }

    public void setMaxWait(long maxWait) {
        this.pool.setMaxWait(maxWait);
    }
}
