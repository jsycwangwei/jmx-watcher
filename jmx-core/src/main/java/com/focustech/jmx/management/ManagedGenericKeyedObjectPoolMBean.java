package com.focustech.jmx.management;

/**
 * copy of {@link org.apache.commons.pool.impl.GenericKeyedObjectPool}.
 */
public interface ManagedGenericKeyedObjectPoolMBean {

    /**
     * Gets the max active.
     * 
     * @return the max active
     */
    int getMaxActive();

    int getMaxIdle();

    long getMaxWait();

    int getMinIdle();

    int getNumActive();

    int getNumIdle();

    void setMaxActive(int maxActive);

    void setMaxIdle(int maxIdle);

    void setMinIdle(int maxIdle);

    void setMaxWait(long maxWait);
}
