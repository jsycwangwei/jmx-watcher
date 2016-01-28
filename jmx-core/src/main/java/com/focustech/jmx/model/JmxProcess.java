package com.focustech.jmx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 待监控server列表
 * 
 * @author wangwei-ww
 */
public class JmxProcess {

    private String name;
    private List<Server> servers = new ArrayList<Server>();
    private Integer numMultiThreadedServers;

    public JmxProcess() {
    }

    public JmxProcess(Server server) {
        this.servers.add(server);
    }

    public JmxProcess(List<Server> servers) {
        this.setServers(servers);
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public List<Server> getServers() {
        return servers;
    }

    public boolean isServersMultiThreaded() {
        return this.numMultiThreadedServers != null && this.numMultiThreadedServers > 0;
    }

    public void setNumMultiThreadedServers(Integer numMultiThreadedServers) {
        this.numMultiThreadedServers = numMultiThreadedServers;
    }

    public Integer getNumMultiThreadedServers() {
        return numMultiThreadedServers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
