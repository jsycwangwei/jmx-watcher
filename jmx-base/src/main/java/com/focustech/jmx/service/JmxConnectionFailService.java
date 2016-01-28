package com.focustech.jmx.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focustech.jmx.DAO.JmxConnectionFailDAO;
import com.focustech.jmx.po.JmxConnectionFail;
import com.focustech.jmx.po.Pager;

@Service
public class JmxConnectionFailService {
    @Autowired
    JmxConnectionFailDAO connectionFailDAO;

    public Pager<JmxConnectionFail> selectFailRecord(Integer appid, Integer hostId, Date from, Date to,
            int currentPage, int pageSize) {
        Pager<JmxConnectionFail> pager = new Pager<JmxConnectionFail>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(pageSize);
        pager.setTotalCount(connectionFailDAO.selectFailRecordCount(appid, hostId, from, to));
        List<JmxConnectionFail> records =
                connectionFailDAO.selectFailRecord(appid, hostId, from, to, pager.getStart(), pager.getPageSize());
        pager.setItems(records);
        return pager;
    }

    public int selectFailRecordCount(Integer appid, Integer hostId, Date from, Date to) {
        return connectionFailDAO.selectFailRecordCount(appid, hostId, from, to);
    }
}
