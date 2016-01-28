package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxCpu;

public interface JmxCpuDAO {
    int insertCpuInfo(JmxCpu cpu);

    List<JmxCpu> selectCpuInfoByDate(@Param("appId") Integer appId, @Param("hostId") Integer serverId,
            @Param("from") Date from, @Param("to") Date to);

    JmxCpu selectLastestCpuInfo(@Param("appId") Integer appId, @Param("hostId") Integer serverId);
}
