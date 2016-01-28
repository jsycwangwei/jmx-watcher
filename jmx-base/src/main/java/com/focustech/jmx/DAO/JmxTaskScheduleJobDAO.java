package com.focustech.jmx.DAO;

import java.util.List;

import com.focustech.jmx.po.JmxTaskScheduleJob;

public interface JmxTaskScheduleJobDAO {
    int deleteByPrimaryKey(Long jobId);

    int insert(JmxTaskScheduleJob record);

    int insertSelective(JmxTaskScheduleJob record);

    JmxTaskScheduleJob selectByPrimaryKey(Long jobId);

    int updateByPrimaryKeySelective(JmxTaskScheduleJob record);

    int updateByPrimaryKey(JmxTaskScheduleJob record);

    List<JmxTaskScheduleJob> getAll();
}
