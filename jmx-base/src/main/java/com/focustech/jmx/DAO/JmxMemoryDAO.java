package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxMemory;
import com.focustech.jmx.pojo.JmxMemoryPOJO;

public interface JmxMemoryDAO {

    List<JmxMemory> selectMemoryInfo(@Param("appId") Integer appId, @Param("hostId") Integer hostId,
            @Param("from") Date from, @Param("to") Date to);

    JmxMemory selectLastestMemoryInfo(@Param("appId") Integer appId, @Param("hostId") Integer serverId);

    Integer insertMemory(JmxMemory memory);

    int updateJavaMemory(@Param("heapMemory") int heapMemory, @Param("noHeapMemory") int noHeapMemory,
            @Param("recId") int recId);

    int updateOld(@Param("old") int old, @Param("recId") int recId);

    int updateEden(@Param("eden") int eden, @Param("recId") int recId);

    int updateSurivor(@Param("surivor") int surivor, @Param("recId") int recId);

    List<JmxMemoryPOJO> getAllServerNowMemoryHeap();

    int updateAbnormalFlgByPrimaryKey(@Param("recId") int recId);

    JmxMemory getMemoryInfoByPrimaryKey(@Param("recId") int recId);

    void updateMemoryInfoByPrimaryKey(JmxMemory memory);
}
