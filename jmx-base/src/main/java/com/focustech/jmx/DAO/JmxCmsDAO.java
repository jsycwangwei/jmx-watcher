package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxCms;

public interface JmxCmsDAO {
    int selectLastGcTime(@Param("appId") Integer appId, @Param("hostId") Integer hostId,
            @Param("startTime") Long startTime);

    int insertLastGcInfo(JmxCms cms);

    List<JmxCms> selectCmsInfoByDate(@Param("appId") Integer appId, @Param("hostId") Integer hostId,
            @Param("from") Date from, @Param("to") Date to);

    JmxCms selectLastestCmsInfo(@Param("appId") Integer appId, @Param("hostId") Integer hostId);

    int updateLastGcInfo(JmxCms cms);

    int delSameInfo(@Param("recId") Integer recId);

    JmxCms selectByPrimary(@Param("recId") Integer recId);
}
