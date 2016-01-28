package com.focustech.jmx.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.pojo.JmxServerPOJO;

public interface JmxServerDAO {
    List<JmxServer> selectServersByAppId(@Param("appId") Integer appId);

    List<JmxServer> selectServersByAppIds(@Param("appIds") List<Integer> appIds);

    JmxServer selectDetailInfo(@Param("hostid") Integer hostid, @Param("appid") Integer appid);

    JmxServerPOJO selectServerExtraInfo(@Param("hostid") Integer hostid, @Param("appid") Integer appid);

    void updateServerMonitor(@Param("hostid") Integer hostid, @Param("appid") Integer appid,
            @Param("jmxstatus") Integer jmxStatus);

    JmxServer selectServerByHostId(@Param("hostId") Integer hostId);

}
