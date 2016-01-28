package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxCmsDAO;
import com.focustech.jmx.po.JmxCms;

@Service
public class JmxCmsService extends AbstractService {

    @Autowired
    JmxCmsDAO jmxCmsDAO;

    public List<JmxCms> selectCmsInfoByDate(Integer appId, Integer hostId, Date from, Date to) {
        return jmxCmsDAO.selectCmsInfoByDate(appId, hostId, from, to);
    }

    public JmxCms selectLastestCmsInfo(Integer appId, Integer hostId) {
        return jmxCmsDAO.selectLastestCmsInfo(appId, hostId);
    }

}
