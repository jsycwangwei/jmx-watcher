package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxDatabaseDAO;
import com.focustech.jmx.po.JmxDatabase;
import com.focustech.jmx.pojo.JmxDatabasePOJO;

@Service
public class JmxDatabaseService extends AbstractService {
    @Autowired
    JmxDatabaseDAO jmxDBDAO;

    public List<JmxDatabase> selectDatabaseInfoByDate(Integer appId, Integer serverId, Date from, Date to) {
        return jmxDBDAO.selectDatabaseInfoByDate(appId, serverId, from, to);
    }

    public JmxDatabase selectLastestDatabaseInfo(Integer appId, Integer serverId) {
        return jmxDBDAO.selectLastestDatabaseInfo(appId, serverId);
    }

    public int insertDatabaseInfo(JmxDatabase database) {
        return jmxDBDAO.insertDatabaseInfo(database);
    }

    /***
     * 获取所有应用的数据库Active连接数
     * 
     * @return
     */
    public List<JmxDatabasePOJO> selectDBActiveConn() {
        return jmxDBDAO.selectDBActiveConn();

    }

}
