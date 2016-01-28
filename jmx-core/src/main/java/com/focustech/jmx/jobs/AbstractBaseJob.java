package com.focustech.jmx.jobs;

import java.util.Date;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxConnectionFailDAO;
import com.focustech.jmx.connections.JMXConnectionParams;
import com.focustech.jmx.connections.JmxConnectionPool;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxConnectionFail;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.service.JmxServerService;

public abstract class AbstractBaseJob implements IJob {
    protected Log log = LogFactory.getLog(LogCategory.JOBS.toString());
    @Autowired
    JmxConnectionPool jmxConnectionPool;

    @Autowired
    JmxServerService jmxServerService;

    @Autowired
    JmxConnectionFailDAO jmxConnectionFailDAO;

    // 模板方法各自子类只要实现doExecute()和transforming()buildQueries() 三个抽象方法既可
    public void execute(Object object) {
        Integer appId = (Integer) object;
        List<JmxServer> list = jmxServerService.selectServersByAppId(appId);
        if (CollectionUtils.isEmpty(list)){
        	return;
        }
            
        for (JmxServer jmxServer : list) {
            Server server = null;
            JMXConnector conn = null;
            JMXConnectionParams connectionParams = null;
            MBeanServerConnection mbeanServer = null;
            try {
                server = transforming(jmxServer);
                if (null == server){
                	continue;
                	
                }                   
                connectionParams = new JMXConnectionParams(server.getJmxServiceURL(), server.getEnvironment());
                if (server.isLocal()) {
                    mbeanServer = server.getLocalMBeanServer();
                }
                else {
                    conn = jmxConnectionPool.getJmxPool().borrowObject(connectionParams);
                    mbeanServer = conn.getMBeanServerConnection();
                }
                // 该方法交由子类实现,负责具体的操作
                server = buildQueries(jmxServer, server);
                doExecute(server, mbeanServer);
            }
            catch (Exception e) {
                log.error("Error in job server: " + server, e);
                JmxConnectionFail po = new JmxConnectionFail();
                po.setAddTime(new Date());
                po.setAppId(jmxServer.getAppId());
                po.setHostId(server.getHostId());
                po.setReason(StringUtils.substring(e.getMessage(), 0, 100));
                jmxConnectionFailDAO.addFailRecord(po);
            }
            finally {
                try {
                    jmxConnectionPool.getJmxPool().returnObject(connectionParams, conn);
                }
                catch (Exception e) {
                    log.error("Error returning object to pool for server: " + server, e);
                }
            }
        }
    }

    protected abstract void doExecute(Server server, MBeanServerConnection mbeanServer);

    protected Server transforming(JmxServer jmxServer) {
        Server server =
                Server.builder().setHost(jmxServer.getHostIp()).setPort(jmxServer.getJmxPort() + "")
                        .setAlias(jmxServer.getHostIp()).setNumQueryThreads(1).setHostId(jmxServer.getHostId())
                        .setAppId(jmxServer.getAppId()).build();
        return server;
    }

    protected abstract Server buildQueries(JmxServer jmxServer, Server server);

    protected JmxThreadDumpInfo buildDumpInfo(Server server) {
        JmxThreadDumpInfo dumpInfo = new JmxThreadDumpInfo();
        dumpInfo.setAppId(server.getAppId());
        dumpInfo.setHostId(server.getHostId());
        dumpInfo.setAddTime(new Date());
        return dumpInfo;
    }
}
