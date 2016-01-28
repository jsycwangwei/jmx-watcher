package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxCpuDAO;
import com.focustech.jmx.po.JmxCpu;

@Service
public class JmxCpuService {
    @Autowired
    JmxCpuDAO jmxCpuDAO;

    public List<JmxCpu> selectCpuInfoByDate(Integer appId, Integer serverId, Date from, Date to) {
        return jmxCpuDAO.selectCpuInfoByDate(appId, serverId, from, to);
    }

    public JmxCpu selectLastestCpuInfo(Integer appId, Integer serverId) {
        return jmxCpuDAO.selectLastestCpuInfo(appId, serverId);
    }

}
