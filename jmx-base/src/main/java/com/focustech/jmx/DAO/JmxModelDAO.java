package com.focustech.jmx.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface JmxModelDAO {

    /**
     * 根据appId获取采样字段
     * 
     * @param appId
     * @return
     */
    public List<Integer> selectSampIdsByAppId(@Param("appId") Integer appId);

}
