package com.focustech.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.management.HotSpotDiagnosticMXBean;

public class TestMXBean {

    public static void main(String[] args) {
        try {
            JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://192.168.10.25:1099/jmxrmi");
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxUrl);
            MBeanServerConnection mbs = jmxConnector.getMBeanServerConnection();
            // while (true) {
            HotSpotDiagnosticMXBean hsMXBean =
                    ManagementFactory.newPlatformMXBeanProxy(mbs, "com.sun.management:type=HotSpotDiagnostic",
                            HotSpotDiagnosticMXBean.class);
            // ConnectionPoolMXBean connectionPoolBean =
            // ManagementFactory.newPlatformMXBeanProxy(mbs, "resin:type=ConnectionPool",
            // ConnectionPoolMXBean.class);
            // System.out.println(connectionPoolBean.getConnectionCount());
            hsMXBean.dumpHeap("1.out", true);
            // Thread.currentThread().sleep(1000);
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
