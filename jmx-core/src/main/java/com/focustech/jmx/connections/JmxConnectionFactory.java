package com.focustech.jmx.connections;

import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

/**
 * 建立和各个server时间连接的pool
 * 
 * @author wangwei-ww
 */
public class JmxConnectionFactory extends BaseKeyedPoolableObjectFactory<JMXConnectionParams, JMXConnector> {

    @Override
    public JMXConnector makeObject(JMXConnectionParams params) throws Exception {
        return JMXConnectorFactory.connect(params.getUrl(), params.getEnvironment());
    }

    @Override
    public void destroyObject(JMXConnectionParams params, JMXConnector connector) throws Exception {
        connector.close();
    }

    @Override
    public boolean validateObject(JMXConnectionParams params, JMXConnector connector) {
        boolean result = false;
        try {
            connector.getConnectionId();
            connector.getMBeanServerConnection().getMBeanCount();
            result = true;
        }
        catch (IOException ex) {
        }
        return result;
    }
}
