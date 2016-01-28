package com.focustech.jmx.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxApplicationDAO;
import com.focustech.jmx.DAO.JmxServerDAO;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.pojo.JmxServerPOJO;

@Service
public class JmxServerService {
    @Autowired
    JmxServerDAO jmxServerDAO;
    @Autowired
    JmxApplicationDAO jmxAppDAO;

    public List<JmxServer> selectServersByAppId(Integer appId) {
        return jmxServerDAO.selectServersByAppId(appId);
    }

    public JmxServer selectServerByHostId(Integer hostId) {
        return jmxServerDAO.selectServerByHostId(hostId);
    }

    public List<JmxServer> selectServersByApps(List<JmxApplication> apps) {
        List<Integer> appids = new ArrayList<Integer>();
        for (JmxApplication app : apps) {
            appids.add(app.getAppId());
        }
        if (CollectionUtils.isEmpty(appids)) {
            return new ArrayList<JmxServer>();
        }
        return jmxServerDAO.selectServersByAppIds(appids);
    }

    public JmxServer selectDetailInfo(Integer serverid, Integer appid) {
        return jmxServerDAO.selectDetailInfo(serverid, appid);
    }

    public JmxServerPOJO selectServerExtraInfo(Integer hostid, Integer appid) {
        return jmxServerDAO.selectServerExtraInfo(hostid, appid);
    }

    /***
     * @param serverid
     * @param appid
     * @param isMonitor 是否监控，true 监控，false 不监控
     */
    public void updateServerMonitor(Integer serverid, Integer appid, boolean isMonitor) {
        int jmxStatus = isMonitor ? 1 : 0;
        jmxServerDAO.updateServerMonitor(serverid, appid, jmxStatus);
    }

}
