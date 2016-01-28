package com.focustech.jmx.log;

public enum LogCategory {

    JOBS("jobs"), SERVICE("service"),DUMP("dump"), CONTROLLER("controller");

    private String name;

    private LogCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
