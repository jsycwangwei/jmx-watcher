package com.focustech.jmx.model;

import java.io.Serializable;
import java.util.List;

public class MBean implements Serializable {

    private static final long serialVersionUID = 8709735183966454874L;
    private String beanName;
    private List<Result> properties;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public List<Result> getProperties() {
        return properties;
    }

    public void setProperties(List<Result> properties) {
        this.properties = properties;
    }

}
