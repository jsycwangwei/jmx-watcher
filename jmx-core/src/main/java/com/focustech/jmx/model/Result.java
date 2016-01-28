package com.focustech.jmx.model;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableMap;

/**
 * JMX Query查询结果
 * 
 * @author wangwei-ww
 */
@ThreadSafe
@Immutable
public class Result implements Serializable {
    private static final long serialVersionUID = -8897886217237104479L;
    private final String attributeName;
    private final String className;
    private final String typeName;
    private final ImmutableMap<String, Object> values;
    private final long epoch;
    private final String classNameAlias;

    public Result(long epoch, String attributeName, String className, String classNameAlias, String typeName,
            Map<String, Object> values) {
        this.className = className;
        this.typeName = typeName;
        this.values = ImmutableMap.copyOf(values);
        this.epoch = epoch;
        this.attributeName = attributeName;
        this.classNameAlias = classNameAlias;
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameAlias() {
        return classNameAlias;
    }

    public String getTypeName() {
        return typeName;
    }

    public ImmutableMap<String, Object> getValues() {
        return values;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public long getEpoch() {
        return this.epoch;
    }

    @Override
    public String toString() {
        return "Result [attributeName=" + attributeName + ", className=" + className + ", typeName=" + typeName
                + ", values=" + values + ", epoch=" + epoch + "]";
    }
}
