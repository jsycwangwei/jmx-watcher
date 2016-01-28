package com.focustech.jmx;

// $Id: MemoryMonitor.java,v 1.9 2009/03/11 18:45:39 cpress Exp $

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @see <pre>
 * 		http://java.sun.com/javase/technologies/hotspot/gc/gc_tuning_6.html
 * 		http://java.sun.com/javase/6/docs/api/javax/management/Notification.html
 * 		http://java.sun.com/javase/6/docs/api/javax/management/NotificationListener.html
 * 		http://java.sun.com/javase/6/docs/api/java/lang/management/MemoryPoolMXBean.html
 * 		http://www.theserverside.com/news/thread.tss?thread_id=27481
 * </pre>
 */
/** notify listeners when percentage of available memory is exceeded. */
public class MemoryMonitor implements Monitor {

    private static MemoryMonitor memoryMonitor;
    private final NotificationListener memoryListenerNotificationListener = new NotificationListener() {
        public void handleNotification(Notification notification, Object handback) {
            if (notification.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                thresholdNotificationLogger(notification);
                thresholdNotificationWatch(notification);
            }
        }
    };
    private final Map<String, MemoryPoolMXBean> memoryPoolMap = new HashMap<String, MemoryPoolMXBean>();
    private Timer watchThresholdtimer = null;
    private final static Logger log = Logger.getLogger(MemoryMonitor.class);
    private boolean verbose = false;
    private boolean enabled = false;
    private long thresholdPollPeriod = 600000L; // 600000msec = 10min

    public static MemoryMonitor getMonitor() {
        if (memoryMonitor == null) {
            memoryMonitor = new MemoryMonitor();
        }
        return memoryMonitor;
    }

    private MemoryMonitor() {
        for (MemoryPoolMXBean memPool : ManagementFactory.getMemoryPoolMXBeans()) {
            memoryPoolMap.put(memPool.getName(), memPool);
        }
    }

    private class MemoryMonitorTimerTask extends TimerTask {
        public void run() {
            if (getUsagePercentMax() > getThreshold()) {
                log.info("memory usage is above " + getThresholdFormat() + " threshold");
                return;
            }
            else {
                log.error("memory usage is below threshold");
                watchThresholdtimer.cancel();
                watchThresholdtimer = null;
            }
        }
    }

    private void thresholdNotificationWatch(Notification notification) {
        if (watchThresholdtimer == null && thresholdPollPeriod > 0) {
            watchThresholdtimer = new Timer();
            watchThresholdtimer.schedule(new MemoryMonitorTimerTask(), new Date(), thresholdPollPeriod);
        }
    }

    private void thresholdNotificationLogger(Notification notification) {
        CompositeData cd = (CompositeData) notification.getUserData();
        MemoryNotificationInfo info = MemoryNotificationInfo.from(cd);
        MemoryPoolMXBean memPool = memoryPoolMap.get(info.getPoolName());
        log.error("memory threshold " + getPercentFormat(getThresholdPercent(memPool)) + " exceeded on memory pool "
                + info.getPoolName() + ", count:" + info.getCount() + ", " + getUsagePercent(memPool) + "["
                + info.getUsage() + "]");
        if (verbose) {
            logStats(memoryPoolMap.get(info.getPoolName()), Level.WARN);
        }

    }

    private void addNotificationListener(NotificationListener notificationListener) {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        ((NotificationBroadcaster) mem).addNotificationListener(memoryListenerNotificationListener, null, null);
    }

    private void removeNotificationListener(NotificationListener notificationListener) {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        try {
            ((NotificationBroadcaster) mem).removeNotificationListener(notificationListener);
        }
        catch (ListenerNotFoundException lnfe) {
            log.error("error in removeNotificationListener");
            log.error(lnfe);
        }
    }

    public void setThresholdPollPeriod(long _thresholdPollPeriod) {
        thresholdPollPeriod = _thresholdPollPeriod;
        log.info("thresholdPollPeriod set to " + thresholdPollPeriod);
    }

    public long getThresholdPollPeriod() {
        return thresholdPollPeriod;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean _verbose) {
        verbose = _verbose;
        log.info("verbose set to " + verbose);
    }

    public void setEnabled(boolean _enabled) {
        if (enabled == _enabled) {
            return;
        }

        if (_enabled) {
            addNotificationListener(memoryListenerNotificationListener);
        }
        else {
            removeNotificationListener(memoryListenerNotificationListener);
        }
        log.info("enabled set to " + enabled);
        enabled = _enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getThreshold() {
        float f = 0;
        for (MemoryPoolMXBean memPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memPool.isUsageThresholdSupported()) {
                f = getThresholdPercent(memPool);
                break;
            }
        }
        return f;
    }

    private String getThresholdFormat() {
        String s = "n/a";
        for (MemoryPoolMXBean memPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memPool.isUsageThresholdSupported()) {
                s = getPercentFormat(getThresholdPercent(memPool));
                break;
            }
        }
        return s;
    }

    public void setThreshold(float fltThresholdPercent) {
        if (fltThresholdPercent < 0.0 || fltThresholdPercent > 1.0) {
            throw new IllegalArgumentException("Percentage not in range");
        }
        for (MemoryPoolMXBean memPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memPool.isUsageThresholdSupported()) {
                setThreshold(fltThresholdPercent, memPool);
            }
        }
        log.info("threshold set to " + fltThresholdPercent);
    }

    private void setThreshold(float fltThresholdPercent, MemoryPoolMXBean memPool) {
        MemoryUsage memUsage = memPool.getUsage();
        long max = memUsage.getMax();
        long threshold = (long) (max * fltThresholdPercent);
        String strThresholdPercent = Integer.toString(Math.round(fltThresholdPercent * 100)) + "%";
        log.info("setting threshold on pool " + memPool.getName() + " to " + strThresholdPercent + " of " + max + "("
                + threshold + ")");
        memPool.setUsageThreshold(threshold);
    }
    private boolean loadCancelled = false;
    private boolean loadRunning = false;

    public boolean isLoadRunning() {
        return loadRunning;
    }

    public void setLoadRunning(boolean _setLoadRunning) {
        if (_setLoadRunning == loadRunning) {
            return;
        }
        if (_setLoadRunning == false) {
            loadCancelled = true;
            log.info("stop Load requested");
            return;
        }

        loadRunning = true;

        log.info("start Load requested");
        final Collection<byte[]> data = new ArrayList<byte[]>();

        new Thread() {
            public void run() {
                while (!loadCancelled && getThreshold() > getUsagePercentMax()) {
                    data.add(new byte[1024 * 1024]);
                    memoryMonitor.logStats(false);
                    try {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException exc) {
                        exc.printStackTrace();
                    }
                }

                log.info("stop Load completed");
                loadCancelled = false;
                loadRunning = false;
            }
        }.start();
    }

    public void logStats(boolean _verbose) {
        logStats(_verbose, "");
    }

    public void logStats(boolean _verbose, String logMesg) {
        boolean orgVerbose = isVerbose();
        verbose = _verbose;
        logStats(logMesg);
        verbose = orgVerbose;
    }

    public void logStats() {
        logStats("");
    }

    public void logStats(String _preString) {
        log.log(Level.INFO, _preString + "\n\n" + getHeapComposite());
        if (verbose) {
            for (MemoryPoolMXBean memPool : memoryPoolMap.values()) {
                logStats(memPool, Level.INFO);
            }
        }
    }

    private void logStats(MemoryPoolMXBean memPool, Level level) {
        boolean supported = memPool.isUsageThresholdSupported();
        String n = "\n";
        log.log(level, n + "MemoryPool name      : " + memPool.getName() + n + "MemoryPool usage     : "
                + niceFormat(memPool) + n + "MemoryPool peak      : " + getPeakFormat(memPool) + n
                + "MemoryPool threshold : " + getThresholdFormat(memPool) + n + "MemoryPool count     : "
                + ((supported) ? memPool.getUsageThresholdCount() : "n/a") + n + "MemoryPool exceeded  : "
                + ((supported) ? memPool.isUsageThresholdExceeded() : "n/a") + n + "MemoryPool isHeap?   : "
                + ((memPool.getType() == MemoryType.HEAP) ? "HEAP" : "NOT HEAP") + n + "---");
    }

    public String getStats() {
        String s = "";
        s = getHeapComposite() + "<br><br>";
        if (verbose) {
            for (MemoryPoolMXBean memPool : memoryPoolMap.values()) {
                s = s + getStats(memPool);
            }
        }
        return s;
    }

    private String getStats(MemoryPoolMXBean memPool) {
        boolean supported = memPool.isUsageThresholdSupported();
        String br = "<br>";
        String s = "";
        s = s + br + "MemoryPool name      : " + memPool.getName();
        s = s + br + "MemoryPool usage     : " + niceFormat(memPool);
        s = s + br + "MemoryPool peak      : " + getPeakFormat(memPool);
        s = s + br + "MemoryPool threshold : " + getThresholdFormat(memPool);
        s = s + br + "MemoryPool count     : " + ((supported) ? memPool.getUsageThresholdCount() : "n/a");
        s = s + br + "MemoryPool exceeded  : " + ((supported) ? memPool.isUsageThresholdExceeded() : "n/a");
        s = s + br + "MemoryPool type      : " + ((memPool.getType() == MemoryType.HEAP) ? "HEAP" : "NOT HEAP");
        s = s + br + "---";
        return s;
    }

    private long getPeak(MemoryPoolMXBean memPool) {
        return memPool.getPeakUsage().getUsed();
    }

    private String getPeakFormat(MemoryPoolMXBean memPool) {
        String s;
        String pattern = "{###,###,###}";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        s = getPercentFormat(getPeakPercent(memPool)) + myFormatter.format(getPeak(memPool));
        return s;
    }

    private String getThresholdFormat(MemoryPoolMXBean memPool) {
        String s;
        String pattern = "{###,###,###}";
        DecimalFormat myFormatter = new DecimalFormat(pattern);

        if (memPool.isUsageThresholdSupported()) {
            s = getPercentFormat(getThresholdPercent(memPool)) + myFormatter.format(memPool.getUsageThreshold());
        }
        else {
            s = "n/a";
        }
        return s;
    }

    private String getMByteFormat(long bytes) {
        long mbytes = Math.round(1000 * (float) bytes / 1048576);
        BigDecimal bd = new BigDecimal(Long.toString(mbytes)).movePointLeft(3);
        String strPercent = "(" + bd + "M)";
        return strPercent;
    }

    private class PoolData {
        int inta;
        float fltP;
        int intP;
        String strP;
    }

    private String niceFormat(MemoryPoolMXBean memPool) {
        String strUsage = memPool.getUsage().toString();
        Pattern pattern2 = Pattern.compile(" \\d*\\(");
        Matcher matcher2 = pattern2.matcher(strUsage);
        String strMatch;
        String s = new String();
        String pattern = "{###,###,###}";
        DecimalFormat format = new DecimalFormat(pattern);

        PoolData d[] = new PoolData[4];

        try {
            for (int i = 0; i < 4; i++) {
                matcher2.find();
                d[i] = new PoolData();

                strMatch = matcher2.group();
                strMatch = strMatch.substring(1, strMatch.length() - 1);
                d[i].inta = Integer.parseInt(strMatch);
            }

            for (int i = 0; i < 4; i++) {
                d[i].fltP = 100 * (float) d[i].inta / d[3].inta;
                d[i].intP = Math.round(d[i].fltP);
                if (d[i].intP >= 10)
                    d[i].strP = "" + d[i].intP;
                if (d[i].intP < 10)
                    d[i].strP = "0" + d[i].intP;
            }

            s = s + "init = " + d[0].strP + "%" + format.format(d[0].inta) + getMByteFormat(d[0].inta) + " ";
            s = s + "used = " + d[1].strP + "%" + format.format(d[1].inta) + getMByteFormat(d[1].inta) + " ";
            s = s + "comm = " + d[2].strP + "%" + format.format(d[2].inta) + getMByteFormat(d[2].inta) + " ";
            s = s + "max  = " + d[3].strP + "%" + format.format(d[3].inta) + getMByteFormat(d[3].inta) + " ";

        }
        catch (IllegalStateException ise) {
            System.out.println("bad matcher input");
        }

        return s;
    }

    private float getUsagePercentMax() {
        float maxUsage = 0, itemUsage = 0;
        // MemoryPoolMXBean memPoolUsagePercentMax ;
        for (MemoryPoolMXBean memPool : memoryPoolMap.values()) {
            if (memPool.isUsageThresholdSupported()) {
                itemUsage = (float) memPool.getUsage().getUsed() / memPool.getUsage().getMax();
                if (itemUsage > maxUsage) {
                    maxUsage = itemUsage;
                }
            }
        }
        return maxUsage;
    }

    private float getUsagePercent(MemoryPoolMXBean memPool) {
        float fltPercent = (float) memPool.getUsage().getUsed() / memPool.getUsage().getMax();
        return fltPercent;
    }

    private float getPeakPercent(MemoryPoolMXBean memPool) {
        float fltPercent = (float) memPool.getPeakUsage().getUsed() / memPool.getUsage().getMax();
        return fltPercent;
    }

    private float getThresholdPercent(MemoryPoolMXBean memPool) {
        float fltPercent = (float) memPool.getUsageThreshold() / memPool.getUsage().getMax();
        return fltPercent;
    }

    private String getPercentFormat(float fltPercent) {
        String strPercent = Integer.toString(Math.round(fltPercent * 100)) + "%";
        return strPercent;
    }

    private String getHeapComposite() {
        int intSum[] = new int[4];
        int intOne[] = new int[4];
        String strPer[] = new String[4];
        DecimalFormat format = new DecimalFormat("{###,###,###}");
        String s = "HEAP SUMMARY: ";

        for (MemoryPoolMXBean memPool : memoryPoolMap.values()) {
            if (memPool.getType() == MemoryType.HEAP) {
                intOne = getHeap(memPool);
                intSum[0] += intOne[0];
                intSum[1] += intOne[1];
                intSum[2] += intOne[2];
                intSum[3] += intOne[3];
            }
        }

        strPer[0] = Integer.toString(Math.round((float) intSum[0] / intSum[3] * 100));
        strPer[1] = Integer.toString(Math.round((float) intSum[1] / intSum[3] * 100));
        strPer[2] = Integer.toString(Math.round((float) intSum[2] / intSum[3] * 100));
        strPer[3] = Integer.toString(Math.round((float) intSum[3] / intSum[3] * 100));

        s = s + "init = " + strPer[0] + "%" + format.format(intSum[0]) + getMByteFormat(intSum[0]) + " ";
        s = s + "used = " + strPer[1] + "%" + format.format(intSum[1]) + getMByteFormat(intSum[1]) + " ";
        s = s + "comm = " + strPer[2] + "%" + format.format(intSum[2]) + getMByteFormat(intSum[2]) + " ";
        s = s + "max  = " + strPer[3] + "%" + format.format(intSum[3]) + getMByteFormat(intSum[3]) + " ";

        return s;
    }

    private int[] getHeap(MemoryPoolMXBean memPool) {
        Pattern pattern2 = Pattern.compile(" \\d*\\(");
        Matcher matcher2 = pattern2.matcher(memPool.getUsage().toString());
        String strMatch;
        int ints[] = new int[4];

        try {
            for (int i = 0; i < 4; i++) {
                matcher2.find();
                ints[i] = 0;

                strMatch = matcher2.group();
                strMatch = strMatch.substring(1, strMatch.length() - 1);
                ints[i] = Integer.parseInt(strMatch);
            }

        }
        catch (IllegalStateException ise) {
            System.out.println("bad matcher input");
        }

        return ints;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            log.info("% arg missing");
            java.lang.System.exit(1);
        }
        log.info("MemoryMonitor says hello");

        MemoryMonitor memoryMonitor = MemoryMonitor.getMonitor();
        memoryMonitor.setVerbose(true);
        if (args.length > 0)
            memoryMonitor.setThreshold(Float.parseFloat(args[0]));
        if (args.length > 1)
            memoryMonitor.setThresholdPollPeriod(Long.parseLong(args[1]));
        memoryMonitor.setEnabled(true);

        memoryMonitor.logStats();
        System.out.println("---");
        System.out.println(memoryMonitor.getStats());
        memoryMonitor.setLoadRunning(true);
        // System.exit(0);
    }

}
