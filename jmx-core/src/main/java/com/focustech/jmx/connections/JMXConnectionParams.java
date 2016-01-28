package com.focustech.jmx.connections;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.lang.reflect.Array.getLength;

import java.lang.reflect.Array;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * JMX Connection参数
 * 
 * @author wangwei-ww
 */
public class JMXConnectionParams {
    private final JMXServiceURL url;
    private final ImmutableMap<String, ?> environment;

    public JMXConnectionParams(JMXServiceURL url, Map<String, ?> environment) {
        this.url = url;
        this.environment = ImmutableMap.copyOf(environment);
    }

    public JMXServiceURL getUrl() {
        return url;
    }

    public ImmutableMap<String, ?> getEnvironment() {
        return environment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }

        if (!(o instanceof JMXConnectionParams)) {
            return false;
        }

        JMXConnectionParams that = (JMXConnectionParams) o;

        return new EqualsBuilder()
                .append(convertArraysToLists(this.environment), convertArraysToLists(that.environment))
                .append(this.url, that.url).isEquals();
    }

    private ImmutableMap<String, ?> convertArraysToLists(ImmutableMap<String, ?> map) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getValue().getClass().isArray()) {
                builder.put(entry.getKey(), asList(entry.getValue()));
            }
            else {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private ImmutableList<?> asList(Object array) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (int i = 0; i < getLength(array); i++) {
            builder.add(Array.get(array, i));
        }
        return builder.build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(135, 211).append(convertArraysToLists(this.environment)).append(this.url)
                .toHashCode();
    }

    @Override
    public String toString() {
        return toStringHelper(getClass()).add("url", url).add("environment", environment).toString();
    }

}
