package com.focustech.jmx.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * 管理实体对象
 * 
 * @author wangwei-ww
 */
public interface ManagedObject {

    /**
     * Gets the object name.
     * 
     * @return the object name
     * @throws MalformedObjectNameException if the object name is not valid
     */
    ObjectName getObjectName() throws MalformedObjectNameException;

    /**
     * Sets the object name.
     * 
     * @param objectName the object name
     * @throws MalformedObjectNameException if the object name is not valid
     */
    void setObjectName(ObjectName objectName) throws MalformedObjectNameException;

    /**
     * Sets the object name.
     * 
     * @param objectName the object name
     * @throws MalformedObjectNameException if the object name is not valid
     */
    void setObjectName(String objectName) throws MalformedObjectNameException;
}
