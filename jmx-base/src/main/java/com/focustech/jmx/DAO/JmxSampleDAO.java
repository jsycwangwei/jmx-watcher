package com.focustech.jmx.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxSample;

public interface JmxSampleDAO {

    List<JmxSample> getSampleListByObjName(@Param("obj") String obj);

    List<String> getObjNameListByAlias(@Param("alias") String alias);

    List<JmxSample> getSampleListByIds(@Param("ids") List<Integer> ids);

}
