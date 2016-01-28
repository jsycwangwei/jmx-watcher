package com.focustech.jmx.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxCmsDAO;
import com.focustech.jmx.DAO.JmxModelDAO;
import com.focustech.jmx.DAO.JmxSampleDAO;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.OutputWriter;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Query.Builder;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.model.output.CmsOutputWriter;
import com.focustech.jmx.model.output.DatabaseOutputWriter;
import com.focustech.jmx.model.output.MemoryOutputWriter;
import com.focustech.jmx.model.output.NormalOutputWriter;
import com.focustech.jmx.model.output.ThreadOutputWriter;
import com.focustech.jmx.po.JmxMemory;
import com.focustech.jmx.po.JmxParameter;
import com.focustech.jmx.po.JmxSample;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.service.JmxDatabaseService;
import com.focustech.jmx.service.JmxMemoryService;
import com.focustech.jmx.service.ThreadService;
import com.focustech.jmx.util.JmxUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * 监控server的job,负责数据的采样
 * 
 * @author wangwei-ww
 */
@Component
public class ServerJob extends AbstractBaseJob {
    private final Log log = LogFactory.getLog(LogCategory.JOBS.toString());

    @Autowired
    JmxDatabaseService jmxDatabaseService;

    @Autowired
    JmxMemoryService jmxMemoryService;

    @Autowired
    ThreadService threadService;

    @Autowired
    JmxSampleDAO jmxSampleDAO;

    @Autowired
    JmxCmsDAO jmxCmsDAO;

    @Autowired
    JmxModelDAO jmxModelDAO;

    @Override
    protected void doExecute(Server server, MBeanServerConnection mbeanServer) {
        new Thread(new Task(server, mbeanServer)).start();
    }

    class Task implements Runnable {
        private Server server;
        private MBeanServerConnection mbeanServer;

        public Task(Server server, MBeanServerConnection mbeanServer) {
            this.server = server;
            this.mbeanServer = mbeanServer;
        }

        @Override
        public void run() {
            try {
                JmxUtils.processServer(server, mbeanServer);
            }
            catch (Exception e) {
                log.error("ServerJob error ", e);
                throw new RuntimeException("ServerJob::doExecute server:" + server, e);
            }
        }

    }

    /**
     * @param jmxServer
     * @return
     */
    protected Server transforming(JmxServer jmxServer) {
        List<Integer> sampIds = jmxModelDAO.selectSampIdsByAppId(jmxServer.getAppId());
        if (CollectionUtils.isEmpty(sampIds)){
        	return null;
        }        
        Server.Builder serverBuilder =
                Server.builder().setHost(jmxServer.getHostIp()).setPort(jmxServer.getJmxPort() + "")
                        .setAlias(jmxServer.getHostIp()).setNumQueryThreads(1).setHostId(jmxServer.getHostId())
                        .setAppId(jmxServer.getAppId()).setDataSource(jmxServer.getDataSource()).setSampIds(sampIds);

        return serverBuilder.build();
    }

    protected Server buildQueries(JmxServer jmxServer, Server server) {
        List<Integer> sampIds = server.getSampIds();
        List<JmxSample> sampList = jmxSampleDAO.getSampleListByIds(sampIds);

        // 在这里组装Query条件.
        com.focustech.jmx.model.Server.Builder builder = server.builder(server);
        Map<String, String> map = new HashMap<String, String>();
        List<String> excludeObjs = Lists.newArrayList();

        for (JmxSample jmxSample : sampList) {

            if (org.apache.commons.lang.StringUtils.startsWith(jmxSample.getAlias(), JmxParameter.MEMORY_PARAM_NAME)) {
                if (map.containsKey(JmxParameter.MEMORY_PARAM_NAME))
                    continue;
                map.put(JmxParameter.MEMORY_PARAM_NAME, JmxParameter.MEMORY_PARAM_NAME);
                builder.addQueries(buildMemoryQuery(jmxServer));
                continue;
            }

            if (org.apache.commons.lang.StringUtils.startsWith(jmxSample.getAlias(), JmxParameter.THREAD_PARAM_NAME)) {
                if (map.containsKey(JmxParameter.THREAD_PARAM_NAME))
                    continue;
                map.put(JmxParameter.THREAD_PARAM_NAME, JmxParameter.THREAD_PARAM_NAME);
                builder.addQueries(buildThreadQuery(jmxServer));
                continue;
            }

            if (org.apache.commons.lang.StringUtils.startsWith(jmxSample.getAlias(), JmxParameter.CMS_PARAM_NAME)) {
                if (map.containsKey(JmxParameter.CMS_PARAM_NAME))
                    continue;
                map.put(JmxParameter.CMS_PARAM_NAME, JmxParameter.CMS_PARAM_NAME);
                builder.addQueries(buildCmsQuery(jmxServer));
                continue;
            }

            if (!excludeObjs.contains(jmxSample.getObjName())) {
                excludeObjs.add(jmxSample.getObjName());
            }
        }

        builder.addQueries(buildDatabaseQuery(jmxServer, excludeObjs));

        builder.addQueries(buildNormalQuery(jmxServer, excludeObjs)); // 定制的Mbean监控

        server = builder.build();

        return server;
    }

    /**
     * 构建线程查询条件
     * 
     * @return
     */

    private List<Query> buildThreadQuery(JmxServer jmxServer) {

        List<String> list = jmxSampleDAO.getObjNameListByAlias(JmxParameter.THREAD_PARAM_NAME);

        return buildCommonQueryList(-1, list,
                new ThreadOutputWriter(ImmutableList.<String> of(), Collections.<String, Object> emptyMap()));

    }

    /**
     * 构建内存的query条件
     * 
     * @return
     */
    private List<Query> buildMemoryQuery(JmxServer jmxServer) {
        // 登记采样数据数据
        JmxMemory m = new JmxMemory();
        m.setHostId(jmxServer.getHostId());
        m.setAddTime(new Date());
        m.setAppId(jmxServer.getAppId());
        jmxMemoryService.insertMemory(m);

        Integer id = m.getRecId(); // 查询标记

        List<String> list = jmxSampleDAO.getObjNameListByAlias(JmxParameter.MEMORY_PARAM_NAME);

        return buildCommonQueryList(id, list,
                new MemoryOutputWriter(ImmutableList.<String> of(), Collections.<String, Object> emptyMap()));
    }

    /**
     * 1. 识别当前应用数据源,对druid的mbean特殊处理 <br>
     * 2. 获取当前主机需要采集的mbean
     * 
     * @param jmxServer
     * @param mbeanNames
     * @return
     */
    private List<Query> buildDatabaseQuery(JmxServer jmxServer, List<String> mbeanNames) {
        List<String> objs = jmxSampleDAO.getObjNameListByAlias(JmxParameter.DB_PARAM_NAME);

        objs.retainAll(mbeanNames);

        if (CollectionUtils.isEmpty(objs)) { // 数据源为druid
            objs = jmxSampleDAO.getObjNameListByAlias("druid");
            objs.retainAll(mbeanNames);
            if (CollectionUtils.isEmpty(objs))
                return null;
        }

        List<Query> queryList = new ArrayList<Query>(objs.size());
        for (String obj : objs) {
            Builder builder = Query.builder().setId(-1).setObj(obj).addOutputWriter(
                    new DatabaseOutputWriter(ImmutableList.<String> of(), Collections.<String, Object> emptyMap()));
            List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(obj);
            Query q = buildCommonQuery(builder, list);

            queryList.add(q);
        }
        return queryList;
    }

    private List<Query> buildCmsQuery(JmxServer jmxServer) {
        List<String> list = jmxSampleDAO.getObjNameListByAlias(JmxParameter.CMS_PARAM_NAME);

        return buildCommonQueryList(-1, list,
                new CmsOutputWriter(ImmutableList.<String> of(), Collections.<String, Object> emptyMap()));
    }

    /**
     * 构建通用Query
     * 
     * @param jmxServer
     * @return
     */
    private List<Query> buildNormalQuery(JmxServer jmxServer, List<String> objs) {
        return buildCommonQueryList(-1, objs, new NormalOutputWriter(ImmutableList.<String> of(), false, false,
                Collections.<String, Object> emptyMap()));
    }

    /**
     * 构建查询条件
     * 
     * @param id
     * @param list
     * @param output
     * @return
     */
    private List<Query> buildCommonQueryList(int id, List<String> objs, OutputWriter output) {
        if (CollectionUtils.isEmpty(objs))
            return null;

        List<Query> queryList = new ArrayList<Query>(objs.size());
        for (String obj : objs) {
            List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(obj);
            Builder builder = Query.builder().setId(id).setObj(obj).addOutputWriter(output);
            Query query = buildCommonQuery(builder, list);

            queryList.add(query);
        }

        return queryList;
    }

    private Query buildCommonQuery(Builder builder, List<JmxSample> list) {
        if (!CollectionUtils.isEmpty(list)) {
            int size = list.size();
            String[] attrs = new String[size];
            String[] keys = new String[size];
            String[] typeNames = new String[size];
            int i = 0;
            for (JmxSample sample : list) {
                attrs[i] = sample.getAttrName();
                keys[i] = sample.getKeyName();
                typeNames[i] = sample.getAlias();
                i++;
            }
            builder.addAttr(attrs).addKeys(keys).addTypeNames(typeNames);
        }
        return builder.build();
    }

}
