package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxMemoryDAO;
import com.focustech.jmx.po.JmxMemory;
import com.focustech.jmx.pojo.JmxMemoryPOJO;

@Service
public class JmxMemoryService extends AbstractService {
    @Autowired
    JmxMemoryDAO jmxMemoryDAO;

    public List<JmxMemory> selectMemoryInfoBySid(Integer appId, Integer serverId, Date from, Date to) {
        return jmxMemoryDAO.selectMemoryInfo(appId, serverId, from, to);
    }

    public JmxMemory selectLastestMemoryInfo(Integer appId, Integer serverId) {
        return jmxMemoryDAO.selectLastestMemoryInfo(appId, serverId);
    }

    public Integer insertMemory(JmxMemory memory) {
        return jmxMemoryDAO.insertMemory(memory);
    }

    public List<JmxMemoryPOJO> getAllServerNowMemoryHeap() {
        return jmxMemoryDAO.getAllServerNowMemoryHeap();
    }

}
