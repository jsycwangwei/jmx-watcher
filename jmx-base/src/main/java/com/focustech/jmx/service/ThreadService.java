package com.focustech.jmx.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.focustech.jmx.DAO.JmxThreadDAO;
import com.focustech.jmx.po.JmxThread;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.po.Pager;
import com.focustech.jmx.pojo.JmxThreadPOJO;

@Service("threadService")
public class ThreadService extends AbstractService {
    @Autowired
    JmxThreadDAO threadDao;

    private static Map<String, String> reasonMap = new HashMap<String, String>();

    static {
        reasonMap.put(JmxThreadDumpInfo.THREAD_NUM_HIGH, JmxThreadDumpInfo.THREAD_NUM_HIGH_STR);
        reasonMap.put(JmxThreadDumpInfo.THREAD_DEAD_LOCK, JmxThreadDumpInfo.THREAD_DEAD_LOCK_STR);
        reasonMap.put(JmxThreadDumpInfo.CPU_USAGE_HIGH, JmxThreadDumpInfo.CPU_USAGE_HIGH_STR);
        reasonMap.put(JmxThreadDumpInfo.NORMAL_DUMP, JmxThreadDumpInfo.NORMAL_DUMP_STR);
        reasonMap.put(JmxThreadDumpInfo.DB_CONN_HIGH, JmxThreadDumpInfo.DB_CONN_HIGH_STR);
        reasonMap.put(JmxThreadDumpInfo.JVM_CMS_ABNORMAL, JmxThreadDumpInfo.JVM_CMS_ABNORMAL_STR);
        reasonMap.put(JmxThreadDumpInfo.JVM_MEMORY_ABNORMAL, JmxThreadDumpInfo.JVM_MEMORY_ABNORMAL_STR);
        reasonMap.put(JmxThreadDumpInfo.HAND_DUMP, JmxThreadDumpInfo.HAND_DUMP_STR);
    }

    /**
     * 保存线程的统计信息
     * 
     * @param statisticsInfo
     */
    public Integer saveInfo(JmxThread statisticsInfo) {
        return threadDao.saveInfo(statisticsInfo);
    }

    /**
     * 获取对线程信息进行dump操作时,连续两次dump的时间间隔,单位为秒
     * 
     * @return
     */

    public int getRepeatInterval(Integer appId) {
        // return NumberUtils.toInt(threadDao.getRepeatInterval(appId), 30);
        return NumberUtils.toInt(threadDao.getRepeatInterval(appId), 20);
    }

    /**
     * 对线程信息进行dump的次数
     * 
     * @return
     */
    public int getRepeatCount(Integer appId) {
        return NumberUtils.toInt(threadDao.getRepeatCount(appId), 2);
    }

    /**
     * 保存线程dump的相关信息
     * 
     * @param dumpInfo
     */
    public void saveDumpInfo(JmxThreadDumpInfo dumpInfo) {
        threadDao.saveDumpInfo(dumpInfo);
    }

    public List<JmxThread> getThreadStatisticsInfoByDate(Integer appId, Integer serverId, Date from, Date to) {
        return threadDao.getThreadStatisticsInfoByDate(appId, serverId, from, to);
    }

    public JmxThread getLastestThreadInfo(Integer appId, Integer serverId) {
        return threadDao.getLastestThreadInfo(appId, serverId);
    }

    /**
     * 获取所有的线程dump的信息
     * 
     * @return
     */
    public List<JmxThreadDumpInfo> getAllDumpInfos() {
        List<JmxThreadDumpInfo> dumpInfos = threadDao.getAllDumpInfos();
        if (CollectionUtils.isEmpty(dumpInfos)) {
            return null;
        }
        setDumpReason(dumpInfos);
        return dumpInfos;
    }

    /**
     * 分页获取线程dump信息
     * 
     * @return
     */
    public Pager<JmxThreadDumpInfo> getDumpInfos(int currentPage, int pageSize, String dumpType, Date startDate,
            Date endDate, String server, String app) {
        Integer hostId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        Pager<JmxThreadDumpInfo> pager = new Pager<JmxThreadDumpInfo>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(pageSize);
        pager.setTotalCount(threadDao.getDumpInfosCount(dumpType, startDate, endDate, hostId, appId));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pager.getStart());
        map.put("max", pager.getPageSize());
        map.put("dumpType", dumpType);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("hostId", hostId);
        map.put("appId", appId);
        List<JmxThreadDumpInfo> dumpInfos = threadDao.getDumpInfos(map);
        setDumpReason(dumpInfos);
        pager.setItems(dumpInfos);
        return pager;
    }

    public List<JmxThreadDumpInfo> getAppAbnormalDumpInfos(Integer appId, Integer hosId, Date startDate, Date endDate,
            int startNum, int endNum) {
        List<JmxThreadDumpInfo> dumpInfos =
                threadDao.getAppAbnormalDumpInfos(appId, hosId, startDate, endDate, startNum, endNum);
        setDumpReason(dumpInfos);
        return dumpInfos;
    }

    public List<JmxThreadPOJO> getAllServerNowThreadActiveCount() {
        return threadDao.getAllServerNowThreadActiveCount();
    }

    public Map<String, Long> getLastest7dayDumpCount(Integer appId, Integer serverId) {
        String defaultFormat = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(defaultFormat);
        Date toDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.add(Calendar.DATE, -6);
        Date fromDate = cal.getTime();
        String fromStr = format.format(fromDate);
        String toStr = format.format(toDate);
        try {
            fromDate = format.parse(fromStr);
            toDate = format.parse(toStr);
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Map<String, String>> dumpcount = threadDao.getLastestdayDumpCount(appId, serverId, fromDate, toDate);
        Map<String, Long> dayMap = new TreeMap<String, Long>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (Map<String, String> map : dumpcount) {
            String key = map.get("day").toString();
            String value = String.valueOf(map.get("num"));
            dayMap.put(key, Long.parseLong(value));
        }
        for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
            if (!dayMap.containsKey(format.format(cal.getTime()))) {
                dayMap.put(format.format(cal.getTime()), 0l);
            }

        }

        return dayMap;

    }

    private void setDumpReason(List<JmxThreadDumpInfo> dumpInfos) {
        for (JmxThreadDumpInfo dumpInfo : dumpInfos) {
            dumpInfo.setReason(reasonMap.get(dumpInfo.getReasonType()));

        }
    }
}
