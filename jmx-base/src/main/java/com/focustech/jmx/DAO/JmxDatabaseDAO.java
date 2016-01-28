package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxDatabase;
import com.focustech.jmx.pojo.JmxDatabasePOJO;

public interface JmxDatabaseDAO {
    int insertDatabaseInfo(JmxDatabase database);

    int updateDatabaseInfo(@Param("po") JmxDatabase database);

    List<JmxDatabase> selectDatabaseInfoByDate(@Param("appId") Integer appId, @Param("hostId") Integer serverId,
            @Param("from") Date from, @Param("to") Date to);

    JmxDatabase selectLastestDatabaseInfo(@Param("appId") Integer appId, @Param("hostId") Integer serverId);

    /***
     * 获取所有应用的当前数据库Active的连接数量
     *
     * @return
     */
    List<JmxDatabasePOJO> selectDBActiveConn();

    JmxDatabase getDBInfoByPrimaryKey(@Param("recId") Integer recId);

}
