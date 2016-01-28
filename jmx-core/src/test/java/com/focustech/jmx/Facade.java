package com.focustech.jmx;

// package edu.uic.java.monitor.memory.web;

// import MemoryMonitor;

/** jsp helper */
public class Facade {
    /** "s" stands for SINGLETON */
    MemoryMonitor s;

    /**
     * 0 argument constructor constructor
     */
    public Facade() {
        this.s = MemoryMonitor.getMonitor();
    }

    /**
     * Wrapper methods
     */
    public float getThreshold() {
        return this.s.getThreshold();
    }

    public void setThreshold(float f) {
        if (f != this.s.getThreshold()) {
            this.s.setThreshold(f);
        }
    }

    public long getThresholdPollPeriod() {
        return this.s.getThresholdPollPeriod();
    }

    public void setThresholdPollPeriod(long f) {
        if (f != this.s.getThresholdPollPeriod()) {
            this.s.setThresholdPollPeriod(f);
        }
    }

    public boolean isVerbose() {
        return this.s.isVerbose();
    }

    public void setVerbose(boolean b) {
        if (b != this.s.isVerbose()) {
            this.s.setVerbose(b);
        }
    }

    public boolean isEnabled() {
        return this.s.isEnabled();
    }

    public void setEnabled(boolean b) {
        if (b != this.s.isEnabled()) {
            this.s.setEnabled(b);
        }
    }

    public boolean isLoadRunning() {
        return s.isLoadRunning();
    }

    public void setLoadRunning(boolean b) {
        if (b != s.isLoadRunning()) {
            this.s.setLoadRunning(b);
        }
    }

    public String getStats() {
        return this.s.getStats();
    }

}
