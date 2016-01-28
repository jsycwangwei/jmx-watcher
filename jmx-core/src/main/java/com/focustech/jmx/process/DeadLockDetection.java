package com.focustech.jmx.process;

import javax.management.MBeanServerConnection;

public interface DeadLockDetection {
    public boolean detectDeadLock(MBeanServerConnection mbServer);
}
