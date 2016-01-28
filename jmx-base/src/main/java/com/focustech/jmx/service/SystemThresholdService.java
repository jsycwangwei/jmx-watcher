package com.focustech.jmx.service;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxParameterDAO;
import com.focustech.jmx.common.ThresholdFields;
import com.focustech.jmx.po.JmxParameter;

@Service
public class SystemThresholdService {
    @Autowired
    JmxParameterDAO systemThresholdDao;

    /**
     * 获取JVM总线数的阀值
     * 
     * @return
     */
    public int getJVMThreadThreshold(Integer appId) {
        return NumberUtils.toInt(systemThresholdDao.getThreadThreshold(appId, ThresholdFields.JVM_THREAD_COUNT),
                Integer.MAX_VALUE);
    }

    /**
     * 获取Resin总线数的阀值
     * 
     * @return
     */
    public int getResinThreadThreshold(Integer appId) {
        return NumberUtils.toInt(systemThresholdDao.getThreadThreshold(appId, ThresholdFields.RESIN_THREAD_COUNT),
                Integer.MAX_VALUE);
    }

    /**
     * 获取cpu使用率的阀值
     * 
     * @return
     */
    public float getCpuUsageThreshold(Integer appId) {
        return NumberUtils.toFloat(systemThresholdDao.getCpuUsageThreshold(appId, ThresholdFields.CPU_USAGE), 1);
    }

    public void updateParamValue(long recid, String value) {
        systemThresholdDao.updateParamValue(recid, value);
    }

    /**
     * 获取dump文件存放的路径的目录
     * 
     * @return
     */
    public String getWebAppRoot() {
        return System.getProperty("webapp.root");
    }

    public List<JmxParameter> selectParamtersByAppid(Integer appId) {
        return systemThresholdDao.selectParamtersByAppid(appId);
    }

    public List<JmxParameter> selectParamtersByType(Integer appId, String paramType) {
        return systemThresholdDao.selectParamtersByType(appId, paramType);
    }

    public void updateParamValueByKey(Integer appid, String paramType, String paramname, String paramkey, String value) {
        systemThresholdDao.updateParamValueByKey(appid, paramType, paramname, paramkey, value);
    }

    public void insertParam(Integer appid, String paramType, String paramname, String paramkey, String value) {
        systemThresholdDao.insertParam(appid, paramType, paramname, paramkey, value);
    }

    public void saveParam(Integer appid, String paramType, String paramname, String paramkey, String value) {
        if (isParamExsit(appid, paramType, paramname, paramkey)) {
            systemThresholdDao.updateParamValueByKey(appid, paramType, paramname, paramkey, value);
        }
        else {
            systemThresholdDao.insertParam(appid, paramType, paramname, paramkey, value);
        }
    }

    public boolean isParamExsit(Integer appid, String paramType, String paramname, String paramkey) {
        if (systemThresholdDao.getParamCount(appid, paramType, paramname, paramkey) > 0) {
            return true;
        }
        return false;
    }

    public JmxParameter selectParamter(Integer appId, String paramType, String paramkey, String paramname) {
        return systemThresholdDao.selectParamter(appId, paramType, paramkey, paramname);
    }

    public String selectParamterValue(Integer appId, String paramType, String paramkey, String paramname) {
        JmxParameter param = systemThresholdDao.selectParamter(appId, paramType, paramkey, paramname);
        return param == null ? "" : param.getParamValue();
    }
}
