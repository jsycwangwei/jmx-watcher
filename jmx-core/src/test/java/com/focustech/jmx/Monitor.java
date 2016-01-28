// $Id: Monitor.java,v 1.5 2009/03/10 19:58:35 cpress Exp $
package com.focustech.jmx;

/** Monitoring interface **/
public interface Monitor {
    String getStats();

    void logStats();

    void logStats(boolean tempVerbose);

    void logStats(boolean tempVerbose, String logMesg);

    void logStats(String _preString);

    void setThreshold(float fltThresholdPercent);

    float getThreshold();

    void setThresholdPollPeriod(long _thresholdPollPeriod);

    long getThresholdPollPeriod();

    void setVerbose(boolean b);

    boolean isVerbose();

    void setEnabled(boolean enabled);

    boolean isEnabled();

}
