package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxNormalDAO;
import com.focustech.jmx.po.JmxNormal;

@Service
public class JmxNormalService {
    @Autowired
    JmxNormalDAO jmxNormalDAO;

    public List<JmxNormal> selectNormalInfo(String objName, int hostId, int size, Date from, Date to) {
        return jmxNormalDAO.selectNormalInfo(objName, hostId, size, from, to);
    }

    public List<String> selectObjsByhostId(int hostId) {
        return jmxNormalDAO.selectObjsByhostId(hostId);
    }
}
