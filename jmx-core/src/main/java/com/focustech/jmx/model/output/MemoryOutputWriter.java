package com.focustech.jmx.model.output;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.xml.bind.ValidationException;

import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxMemoryDAO;
import com.focustech.jmx.DAO.JmxSampleDAO;
import com.focustech.jmx.exceptions.SampleException;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxMemory;
import com.focustech.jmx.po.JmxSample;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.quartz.SpringUtils;
import com.focustech.jmx.scheduler.ThreadDumpScheduler;
import com.focustech.jmx.service.ThreadDumpService;
import com.focustech.jmx.util.ConvertUtils;
import com.focustech.jmx.util.LogUtils;
import com.google.common.collect.ImmutableList;

public class MemoryOutputWriter extends BaseOutputWriter {

    private JmxSampleDAO jmxSampleDAO;

    private JmxMemoryDAO jmxMemoryDAO;

    private JmxApplicationDAO jmxApplicationDAO;
    

    public MemoryOutputWriter(ImmutableList<String> typeNames, Map<String, Object> settings) {
        super(typeNames, false, false, settings);
        jmxMemoryDAO = SpringUtils.getBean(JmxMemoryDAO.class);
        jmxSampleDAO = SpringUtils.getBean(JmxSampleDAO.class);
        jmxApplicationDAO = SpringUtils.getBean(JmxApplicationDAO.class);
    }

    @Override
    public void validateSetup(Server server, Query query) throws ValidationException {
    }
    
    @Override
    protected void internalWrite(int abnormalFlg, Server server, Query query, Map<String, String> resultMap,
            MBeanServerConnection mbeanServer) {
        // System.out.println("========= " + server.getHost() + " " + query.getObj() + ">>>>>>>> " + query.getId());
        JmxMemory jmxMemory = new JmxMemory();
        jmxMemory.setRecId(query.getId());
        jmxMemory.setAddTime(new Date());

        List<JmxSample> list = jmxSampleDAO.getSampleListByObjName(query.getObj());
        
        if (CollectionUtils.isEmpty(list))
            return;
        for (JmxSample s : list) {
            if (!resultMap.containsKey(s.getAlias()))
                continue;
            Long size = org.apache.commons.lang.math.NumberUtils.toLong(resultMap.get(s.getAlias()));
            if (abnormalFlg == 1) {
                jmxMemory.setAbnormalFlg(abnormalFlg);
            }
            if (s.getAlias().contains("heap")) {
                jmxMemory.setHeapMemorySize(ConvertUtils.convertByte2MB(size));
                continue;
            }
            if (s.getAlias().contains("noHeap")) {
                jmxMemory.setNoHeapMemorySize(ConvertUtils.convertByte2MB(size));
                continue;
            }
            if (s.getAlias().contains("old")) {
                jmxMemory.setOldSize(ConvertUtils.convertByte2MB(size));
                continue;
            }
            if (s.getAlias().contains("eden")) {
                jmxMemory.setEdenSize(ConvertUtils.convertByte2MB(size));
                continue;
            }
            if (s.getAlias().contains("surivor")) {
                jmxMemory.setSurvivorSize(ConvertUtils.convertByte2MB(size));
                continue;
            }
            if (s.getAlias().contains("perm")) {
                jmxMemory.setPermSize(ConvertUtils.convertByte2MB(size));
            }
        }
        jmxMemoryDAO.updateMemoryInfoByPrimaryKey(jmxMemory);
        return;
    }

    @Override
    protected Map<String, String> getThresholdParamList(Integer appId) throws Exception {
        return SpringUtils.getBean(ThreadDumpService.class).getParamtersByTypeAndName(appId, "threshold", "memory");
    }

    @Override
    protected void dumpThreadInfo(MBeanServerConnection mbeanServer, Server server, Query query) throws Exception {
        JmxMemoryDAO jmxMemoryDAO = SpringUtils.getBean(JmxMemoryDAO.class);
        JmxMemory memory = jmxMemoryDAO.getMemoryInfoByPrimaryKey(query.getId());
        if (null == memory || memory.getAbnormalFlg() != 1) {
            JmxApplication app = jmxApplicationDAO.selectAppByAppId(server.getAppId());
            SampleException se = new SampleException(app.getAppName(), server.getHost(), "jmx监测到内存异常");
            LogUtils.logDumpErrorInfo(se.toString());
            new ThreadDumpScheduler().execute(mbeanServer, buildJmxDumpInfo(server,
                    JmxThreadDumpInfo.JVM_MEMORY_ABNORMAL, JmxThreadDumpInfo.JVM_MEMORY_ABNORMAL_STR));
        }
    }
}
