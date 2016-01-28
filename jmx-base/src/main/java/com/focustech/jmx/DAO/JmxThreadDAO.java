package com.focustech.jmx.DAO;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.focustech.jmx.po.JmxThread;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.pojo.JmxThreadPOJO;

public interface JmxThreadDAO {
    /**
     * 保存线程的统计信息
     * 
     * @param statisticsInfo
     */
    public Integer saveInfo(JmxThread jmxThread);

    /**
     * 获取对线程信息进行dump操作时,连续两次dump的时间间隔,单位为秒
     * 
     * @return
     */
    public String getRepeatInterval(Integer appId);

    /**
     * 对线程信息进行dump的次数
     * 
     * @return
     */
    public String getRepeatCount(Integer appId);

    /**
     * 保存线程dump的信息
     */
    public void saveDumpInfo(JmxThreadDumpInfo dumpInfo);

    /***
     * 获取一段时间内某个server的线程信息数据
     * 
     * @return
     */
    public List<JmxThread> getThreadStatisticsInfoByDate(@Param("appId") Integer appId,
            @Param("hostId") Integer serverId, @Param("from") Date from, @Param("to") Date to);

    /***
     * 获取最近一条线程信息数据
     * 
     * @param serverId
     * @return
     */
    public JmxThread getLastestThreadInfo(@Param("appId") Integer appId, @Param("hostId") Integer hostId);

    /**
     * 获取所有线程dump的信息
     * 
     * @return
     */
    public List<JmxThreadDumpInfo> getAllDumpInfos();

    /**
     * 分页获取线程的dump信息
     */
    public List<JmxThreadDumpInfo> getDumpInfos(Map<String, Object> map);

    /**
     * 获得线程dump信息的总记录数
     */

    public int getDumpInfosCount(@Param("dumpType") String dumpType, @Param("startDate") Date startDate,
            @Param("endDate") Date endDate, @Param("hostId") Integer hosId, @Param("appId") Integer appId);

    /***
     * @param appId 应用id 如果为null或者0表示不限制
     * @param hostId server id 如果为null或者0表示不限制
     * @param startDate 开始日期 如果为null 表示不限制
     * @param endDate 结束日期 如果为null 表示不限制
     * @param startNum 从前多少条开始 必须填写
     * @param endNum 截止到多少条 必须填写
     * @return
     */
    public List<JmxThreadDumpInfo> getAppAbnormalDumpInfos(@Param("appId") Integer appId,
            @Param("hostId") Integer hosId, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
            @Param("start") Integer startNum, @Param("end") Integer endNum);

    /***
     * 获取最近每天的dump信息记录数量
     * 
     * @param serverId 服务id
     * @param from 开始日期
     * @param to 结束日期
     * @return
     */
    List<Map<String, String>> getLastestdayDumpCount(@Param("appId") Integer appId, @Param("hostId") Integer serverId,
            @Param("from") Date from, @Param("to") Date to);

    /***
     * 获取当前所有应用服务的活跃线程数量
     * 
     * @return
     */
    List<JmxThreadPOJO> getAllServerNowThreadActiveCount();

    int countSizeByReasonAndRange(@Param("appId") int appId, @Param("hostId") int hostId,
            @Param("reason") String reason, @Param("hours") int hours);

    int countSizeByAbnormalReasonAndRange(@Param("appId") Integer appId, @Param("hostId") Integer hostId,
            @Param("hours") int hours);

    void updateThreadInfo(JmxThread statisticsInfo);

    JmxThread getThreadInfoByPrimaryKeyAndDomain(@Param("recId") Integer recId);
}
