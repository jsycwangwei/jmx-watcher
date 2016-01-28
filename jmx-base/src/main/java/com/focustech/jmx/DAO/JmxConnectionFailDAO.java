package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxConnectionFail;
import com.focustech.jmx.pojo.JmxConnectionFailPOJO;

public interface JmxConnectionFailDAO {
    int addFailRecord(JmxConnectionFail po);

    /***
     * 统计所有应用失败链接数
     * 
     * @param start
     * @param max
     * @return
     */
    List<JmxConnectionFailPOJO> selectStatisFailRecord(@Param("project") String project, @Param("appId") Integer appid,
            @Param("start") Integer start, @Param("max") Integer max);

    int selectStatisFailRecordCount(@Param("project") String project, @Param("appId") Integer appid);

    /***
     * 查询失败jmx链接到监控应用 次数
     * 
     * @param hostId
     * @param appid
     * @param from
     * @param to
     * @param start
     * @param max
     * @return
     */
    List<JmxConnectionFail> selectFailRecord(@Param("appId") Integer appid, @Param("hostId") Integer hostId,
            @Param("startDate") Date from, @Param("endDate") Date to, @Param("start") Integer start,
            @Param("max") Integer max);

    int selectFailRecordCount(@Param("appId") Integer appid, @Param("hostId") Integer hostId,
            @Param("startDate") Date from, @Param("endDate") Date to);

}
