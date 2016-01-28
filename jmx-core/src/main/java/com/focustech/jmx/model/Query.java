package com.focustech.jmx.model;

import static com.focustech.jmx.model.PropertyResolver.resolveList;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * JMX查询
 * 
 * @author wangwei-ww
 */
@ThreadSafe
@Immutable
public class Query {

    private final Integer id;
    private final String obj;
    private final ImmutableList<String> keys;
    private final ImmutableList<String> attr;
    private final ImmutableSet<String> typeNames;
    private final String resultAlias;
    private final boolean allowDottedKeys;
    private final ImmutableList<OutputWriter> outputWriters;

    public Query(Integer id, String obj, List<String> keys, List<String> attr, List<String> typeNames,
            String resultAlias, boolean allowDottedKeys, List<OutputWriter> outputWriters) {
        this.id = id;
        this.obj = obj;
        this.attr = resolveList(firstNonNull(attr, Collections.<String> emptyList()));
        this.resultAlias = resultAlias;
        this.keys = resolveList(firstNonNull(keys, Collections.<String> emptyList()));
        this.allowDottedKeys = allowDottedKeys;
        this.outputWriters = ImmutableList.copyOf(firstNonNull(outputWriters, Collections.<OutputWriter> emptyList()));
        this.typeNames = ImmutableSet.copyOf(firstNonNull(typeNames, Collections.<String> emptySet()));
    }

    public String getObj() {
        return obj;
    }

    public Integer getId() {
        return id;
    }

    public String getResultAlias() {
        return resultAlias;
    }

    public ImmutableSet<String> getTypeNames() {
        return typeNames;
    }

    @Nonnull
    public ImmutableList<String> getAttr() {
        return attr;
    }

    @Nonnull
    public ImmutableList<String> getKeys() {
        return keys;
    }

    public boolean isAllowDottedKeys() {
        return allowDottedKeys;
    }

    @Nonnull
    public ImmutableList<OutputWriter> getOutputWriters() {
        return outputWriters;
    }

    @Override
    public String toString() {
        return "Query [obj=" + obj + ", resultAlias=" + resultAlias + ", attr=" + attr + "]";
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

        if (!(o instanceof Query)) {
            return false;
        }

        Query other = (Query) o;

        return new EqualsBuilder().append(this.getObj(), other.getObj()).append(this.getKeys(), other.getKeys())
                .append(this.getAttr(), other.getAttr()).append(this.getResultAlias(), other.getResultAlias())
                .append(sizeOf(this.getOutputWriters()), sizeOf(other.getOutputWriters())).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 97).append(this.getObj()).append(this.getKeys()).append(this.getAttr())
                .append(this.getResultAlias()).append(sizeOf(this.getOutputWriters())).toHashCode();
    }

    private static int sizeOf(List<OutputWriter> writers) {
        if (writers == null) {
            return 0;
        }
        return writers.size();
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotThreadSafe
    public static final class Builder {

        private Integer id;
        private String obj;
        private final List<String> attr = newArrayList();
        private String resultAlias;
        private final List<String> keys = newArrayList();
        private boolean allowDottedKeys;
        private final List<OutputWriter> outputWriters = newArrayList();
        private final List<String> typeNames = newArrayList();

        private Builder() {
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setObj(String obj) {
            this.obj = obj;
            return this;
        }

        public Builder addAttr(String... attr) {
            this.attr.addAll(asList(attr));
            return this;
        }

        public Builder setResultAlias(String resultAlias) {
            this.resultAlias = resultAlias;
            return this;
        }

        public Builder addKey(String keys) {
            return addKeys(keys);
        }

        public Builder addKeys(String... keys) {
            this.keys.addAll(asList(keys));
            return this;
        }

        public Builder addTypeNames(String... typeNames) {
            this.typeNames.addAll(asList(typeNames));
            return this;
        }

        public Builder setAllowDottedKeys(boolean allowDottedKeys) {
            this.allowDottedKeys = allowDottedKeys;
            return this;
        }

        public Builder addOutputWriter(OutputWriter outputWriter) {
            return addOutputWriters(outputWriter);
        }

        public Builder addOutputWriters(OutputWriter... outputWriters) {
            this.outputWriters.addAll(asList(outputWriters));
            return this;
        }

        public Builder setTypeNames(Set<String> typeNames) {
            this.typeNames.addAll(typeNames);
            return this;
        }

        public Query build() {
            return new Query(this.id, this.obj, this.keys, this.attr, this.typeNames, this.resultAlias,
                    this.allowDottedKeys, this.outputWriters);
        }

    }
}
