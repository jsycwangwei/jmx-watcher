package com.focustech.jmx.process;

import java.io.IOException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.druid.util.StringUtils;
import com.focustech.jmx.model.OutputWriter;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Result;
import com.focustech.jmx.model.Server;
import com.google.common.collect.ImmutableList;

public class JmxQueryProcessor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 执行远程query
     * 
     * @param mbeanServer
     * @param server
     * @param query
     * @throws Exception
     */
    public void processQuery(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {  
        ObjectName oName = new ObjectName(query.getObj());   
        for (ObjectName queryName : mbeanServer.queryNames(oName, null)) {
            ImmutableList<Result> results = fetchResults(mbeanServer, query, queryName);
            if (CollectionUtils.isEmpty(results)){
            	 continue;
            }               
            runOutputWritersForQuery(server, query, results, mbeanServer);
        }
    }

    private ImmutableList<Result> fetchResults(MBeanServerConnection mbeanServer, Query query, ObjectName queryName)
            throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        MBeanInfo info = mbeanServer.getMBeanInfo(queryName);
        ObjectInstance oi = mbeanServer.getObjectInstance(queryName);

        List<String> attributes;

        if (query.getAttr().isEmpty() || (query.getAttr().size() == 1 && StringUtils.isEmpty(query.getAttr().get(0)))) {
            attributes = new ArrayList<String>();
            for (MBeanAttributeInfo attrInfo : info.getAttributes()) {
                attributes.add(attrInfo.getName());
            }
        }
        else {
            attributes = query.getAttr();
        }

        ImmutableList<Result> results = ImmutableList.of();
        try {
            if (attributes.size() > 0) {
                log.debug("Executing queryName [{}] from query [{}]", queryName.getCanonicalName(), query);

                AttributeList al =
                        mbeanServer.getAttributes(queryName, attributes.toArray(new String[attributes.size()]));

                results = new JmxResultProcessor(query, oi, al.asList(), info.getClassName()).getResults();
            }
        }
        catch (UnmarshalException ue) {
            if ((ue.getCause() != null) && (ue.getCause() instanceof ClassNotFoundException)) {
                log.debug("Error in fetchQuerys " + ue.getMessage());
            }
        }
        return results;
    }

    private void runOutputWritersForQuery(Server server, Query query, ImmutableList<Result> results,
            MBeanServerConnection mbeanServer) throws Exception {
        for (OutputWriter writer : query.getOutputWriters()) {
            writer.doWrite(server, query, results, mbeanServer);
        }
        log.debug("Finished running outputWriters for query: {}", query);
    }

}
