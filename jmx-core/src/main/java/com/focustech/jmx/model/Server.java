package com.focustech.jmx.model;

import static com.focustech.jmx.model.PropertyResolver.resolveProps;
import static com.google.common.collect.ImmutableSet.copyOf;
import static java.util.Arrays.asList;
import static javax.management.remote.JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 监控的remote jmx server
 * 
 * @author wangwei-ww
 */
@Immutable
@ThreadSafe
public class Server {

    private static final String FRONT = "service:jmx:rmi:///jndi/rmi://";
    private static final String BACK = "/jmxrmi";

    private Integer hostId;
    private String alias;
    private String host;
    private String port;
    private String username;
    private String password;
    private String protocolProviderPackages;
    private String url;
    private String cronExpression;
    private Integer numQueryThreads;
    private Integer appId;
    private String dataSource;

    // query the local MBeanServer
    private boolean local;

    private ImmutableSet<Query> queries;

    private List<Integer> sampIds;

    public Server(String alias, String host, String port, String username, String password,
            String protocolProviderPackages, String url, String cronExpression, Integer numQueryThreads, boolean local,
            List<Query> queries, Integer hostId, Integer appId, String dataSource, List<Integer> sampIds) {
        this.alias = resolveProps(alias);
        this.host = resolveProps(host);
        this.port = port;
        this.username = resolveProps(username);
        this.password = resolveProps(password);
        this.protocolProviderPackages = protocolProviderPackages;
        this.url = resolveProps(url);
        this.cronExpression = cronExpression;
        this.numQueryThreads = numQueryThreads;
        this.local = local;
        this.queries = copyOf(queries);
        this.hostId = hostId;
        this.appId = appId;
        this.dataSource = dataSource;
        this.sampIds = sampIds;
    }

    public Server(String alias, String host, String port) {
        this.alias = alias;
        this.host = host;
        this.port = port;
    }

    public Integer getHostId() {
        return hostId;
    }

    public Integer getAppId() {
        return appId;
    }

    /**
     * Generates the proper username/password environment for JMX connections.
     */
    public ImmutableMap<String, ?> getEnvironment() {
        if (getProtocolProviderPackages() != null && getProtocolProviderPackages().contains("weblogic")) {
            ImmutableMap.Builder<String, String> environment = ImmutableMap.builder();
            if ((username != null) && (password != null)) {
                environment.put(PROTOCOL_PROVIDER_PACKAGES, getProtocolProviderPackages());
                environment.put(SECURITY_PRINCIPAL, username);
                environment.put(SECURITY_CREDENTIALS, password);
            }
            return environment.build();
        }

        ImmutableMap.Builder<String, String[]> environment = ImmutableMap.builder();
        if ((username != null) && (password != null)) {
            String[] credentials = new String[]{username, password};
            environment.put(JMXConnector.CREDENTIALS, credentials);
        }

        return environment.build();
    }

    /**
     * Helper method for connecting to a Server. You need to close the resulting connection.
     */
    public JMXConnector getServerConnection() throws Exception {
        JMXServiceURL url = new JMXServiceURL(getUrl());
        return JMXConnectorFactory.connect(url, this.getEnvironment());
    }

    public MBeanServer getLocalMBeanServer() {
        // Getting the platform MBean server is cheap (expect for th first call) no need to cache it.
        return ManagementFactory.getPlatformMBeanServer();
    }

    public String getAlias() {
        return this.alias;
    }

    public String getHost() {
        if (host == null && url == null) {
            throw new IllegalStateException("host is null and url is null. Cannot construct host dynamically.");
        }

        if (host != null) {
            return host;
        }

        return url.substring(url.lastIndexOf("//") + 2, url.lastIndexOf(':'));
    }

    public String getPort() {
        if (port == null && url == null) {
            throw new IllegalStateException("port is null and url is null.  Cannot construct port dynamically.");
        }
        if (this.port != null) {
            return port;
        }

        return extractPortFromUrl(url);
    }

    private static String extractPortFromUrl(String url) {
        String computedPort = url.substring(url.lastIndexOf(':') + 1);
        if (computedPort.contains("/")) {
            computedPort = computedPort.substring(0, computedPort.indexOf('/'));
        }
        return computedPort;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    /**
     * 是否提供对外的jmx监控
     * 
     * @return
     */
    public boolean isLocal() {
        return local;
    }

    public ImmutableSet<Query> getQueries() {
        return this.queries;
    }

    /**
     * 获取Jmx监控url
     * 
     * @return
     */
    public String getUrl() {
        if (this.url == null) {
            if ((this.host == null) || (this.port == null)) {
                throw new RuntimeException("url is null and host or port is null. cannot construct url dynamically.");
            }
            return FRONT + this.host + ":" + this.port + BACK;
        }
        return this.url;
    }

    public JMXServiceURL getJmxServiceURL() throws MalformedURLException {
        return new JMXServiceURL(getUrl());
    }

    public boolean isQueriesMultiThreaded() {
        return (this.numQueryThreads != null) && (this.numQueryThreads > 0);
    }

    public Integer getNumQueryThreads() {
        return this.numQueryThreads;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    @Override
    public String toString() {
        return "Server [host=" + this.host + ", port=" + this.port + ", url=" + this.url + ", cronExpression="
                + this.cronExpression + ", numQueryThreads=" + this.numQueryThreads + "]";
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

        if (!(o instanceof Server)) {
            return false;
        }

        Server other = (Server) o;

        return new EqualsBuilder().append(this.getHost(), other.getHost()).append(this.getPort(), other.getPort())
                .append(this.getNumQueryThreads(), other.getNumQueryThreads())
                .append(this.getCronExpression(), other.getCronExpression()).append(this.getAlias(), other.getAlias())
                .append(this.getUsername(), other.getUsername()).append(this.getPassword(), other.getPassword())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 21).append(this.getHost()).append(this.getPort())
                .append(this.getNumQueryThreads()).append(this.getCronExpression()).append(this.getAlias())
                .append(this.getUsername()).append(this.getPassword()).toHashCode();
    }

    public String getProtocolProviderPackages() {
        return protocolProviderPackages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Server server) {
        return new Builder(server);
    }

    @NotThreadSafe
    public static final class Builder {
        private String alias;
        private String host;
        private String port;
        private String username;
        private String password;
        private String protocolProviderPackages;
        private String url;
        private String cronExpression;
        private Integer numQueryThreads;
        private boolean local;
        private Integer hostId;
        private Integer appId;
        private String dataSource;
        private final List<Query> queries = new ArrayList<Query>();
        private List<Integer> sampIds;

        private Builder() {
        }

        private Builder(Server server) {
            this.alias = server.alias;
            this.host = server.host;
            this.port = server.port;
            this.username = server.username;
            this.password = server.password;
            this.protocolProviderPackages = server.protocolProviderPackages;
            this.url = server.url;
            this.cronExpression = server.cronExpression;
            this.numQueryThreads = server.numQueryThreads;
            this.local = server.local;
            this.queries.addAll(server.queries);
            this.appId = server.appId;
            this.dataSource = server.dataSource;
            this.hostId = server.hostId;
            this.sampIds = server.sampIds;
        }

        public Builder setAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setProtocolProviderPackages(String protocolProviderPackages) {
            this.protocolProviderPackages = protocolProviderPackages;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public Builder setNumQueryThreads(Integer numQueryThreads) {
            this.numQueryThreads = numQueryThreads;
            return this;
        }

        public Builder setLocal(boolean local) {
            this.local = local;
            return this;
        }

        public Builder addQuery(Query query) {
            this.queries.add(query);
            return this;
        }

        public Builder addQueries(Query... queries) {
            this.queries.addAll(asList(queries));
            return this;
        }

        public Builder addQueries(Set<Query> queries) {
            this.queries.addAll(queries);
            return this;
        }

        public Builder setHostId(Integer hostId) {
            this.hostId = hostId;
            return this;
        }

        public Builder setAppId(Integer appId) {
            this.appId = appId;
            return this;
        }

        public Builder addQueries(List<Query> queries) {
            if (CollectionUtils.isNotEmpty(queries))
                this.queries.addAll(queries);
            return this;
        }

        public Builder setDataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder setSampIds(List<Integer> sampIds) {
            this.sampIds = sampIds;
            return this;
        }

        public Server build() {
            Server server = new Server(alias, host, port, username, password, protocolProviderPackages, url,
                    cronExpression, numQueryThreads, local, queries, hostId, appId, dataSource, sampIds);
            return server;

        }
    }

    @CheckReturnValue
    public static List<Server> mergeServerLists(List<Server> firstList, List<Server> secondList) {
        ImmutableList.Builder<Server> results = ImmutableList.builder();
        List<Server> toProcess = new ArrayList<Server>(secondList);
        for (Server firstServer : firstList) {
            if (toProcess.contains(firstServer)) {
                Server found = toProcess.get(secondList.indexOf(firstServer));
                results.add(merge(firstServer, found));
                // remove server as it is already merged
                toProcess.remove(found);
            }
            else {
                results.add(firstServer);
            }
        }
        // add servers from the second list that are not in the first one
        results.addAll(toProcess);
        return results.build();
    }

    private static Server merge(Server firstServer, Server secondServer) {
        return builder(firstServer).addQueries(secondServer.getQueries()).build();
    }

    public List<Integer> getSampIds() {
        return sampIds;
    }

}
