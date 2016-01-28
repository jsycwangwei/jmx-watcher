package com.focustech.jmx.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxParameterDAO;
import com.focustech.jmx.po.JmxParameter;

@Service
public class ThreadDumpService {
    @Autowired
    JmxParameterDAO systemThresholdDao;

    public Map<String,String> getParamtersByTypeAndName(Integer appId, String paramType, String paramName){
        Map<String,String> map = new HashMap<String, String>();
        List<JmxParameter> list= systemThresholdDao.getParamtersByTypeAndName(appId, paramType, paramName);
        if(null!=list && list.size()>0 ){
            for(JmxParameter po: list){
                map.put(po.getParamKey(), po.getParamValue());
            }
        }
        return map;
    }
}
