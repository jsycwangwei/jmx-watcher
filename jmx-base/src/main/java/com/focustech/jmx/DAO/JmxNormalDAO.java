package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxNormal;

public interface JmxNormalDAO {
    void insertNormal(JmxNormal jmxNormal);

    List<JmxNormal> selectNormalInfo(@Param("objName") String objName, @Param("hostId") int hostId,
            @Param("size") int size, @Param("from") Date from, @Param("to") Date to);

    List<String> selectObjsByhostId(@Param("hostId") int hostId);
}
