package com.focustech.jmx.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxConnectionFailDAO;
import com.focustech.jmx.DAO.JmxParameterDAO;
import com.focustech.jmx.DAO.JmxThreadDAO;
import com.focustech.jmx.po.Pager;
import com.focustech.jmx.pojo.JmxConnectionFailPOJO;

/**
 * jmx统计信息接口
 * 
 * @author wangwei-ww
 */
@Service
public class JmxStaitisService {
    @Autowired
    JmxParameterDAO systemThresholdDao;

    @Autowired
    JmxThreadDAO threadDao;

    @Autowired
    JmxConnectionFailDAO connectionFailDAO;

    /**
     * 统计应用再某种类型下相关时间范围内异常的数量
     * 
     * @param appId
     * @param type
     * @param hours
     * @return
     */
    public int statisExceptions(int appId, int hostId, String type, int hours) {
        int count = 0;
        List<String> exceps = systemThresholdDao.selectKeysByName("exception", type);
        if (CollectionUtils.isEmpty(exceps))
            return count;
        for (String ex : exceps) {
            count += threadDao.countSizeByReasonAndRange(appId, hostId, ex, hours);
        }

        return count;
    }

    /***
     * 统计应用下server在一段时间内非正常的dump数量
     * 
     * @param appId null或者0表示不限制
     * @param hostId null或者0表示不限制
     * @param hours
     * @return
     */
    public int statisServerAbnormalExceptions(Integer appId, Integer hostId, int hours) {

        return threadDao.countSizeByAbnormalReasonAndRange(appId, hostId, hours);
    }

    public Pager<JmxConnectionFailPOJO> selectStatisFailRecord(String project, Integer appid, int currentPage,
            int pageSize) {
        Pager<JmxConnectionFailPOJO> pager = new Pager<JmxConnectionFailPOJO>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(pageSize);
        pager.setTotalCount(connectionFailDAO.selectStatisFailRecordCount(project, appid));
        List<JmxConnectionFailPOJO> records =
                connectionFailDAO.selectStatisFailRecord(project, appid, pager.getStart(), pager.getPageSize());
        pager.setItems(records);
        return pager;
    }

}
