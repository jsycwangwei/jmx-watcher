package com.focustech.jmx.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import com.focustech.jmx.DAO.JmxConnectionFailDAO;
import com.focustech.jmx.connections.JMXConnectionParams;
import com.focustech.jmx.connections.JmxConnectionPool;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxConnectionFail;
import com.focustech.jmx.po.JmxNormal;
import com.focustech.jmx.po.JmxParameter;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.po.Pager;
import com.focustech.jmx.process.JmxThreadDumpProcessor;
import com.focustech.jmx.service.JmxApplicationService;
import com.focustech.jmx.service.JmxConnectionFailService;
import com.focustech.jmx.service.JmxNormalService;
import com.focustech.jmx.service.JmxServerService;
import com.focustech.jmx.service.JmxStaitisService;
import com.focustech.jmx.service.SystemThresholdService;
import com.focustech.jmx.service.ThreadService;

@Controller
public class DetailController {
    protected Log log = LogFactory.getLog(LogCategory.CONTROLLER.toString());

    static List<String> list = new ArrayList<String>();
    @Autowired
    JmxServerService jmxServerService;
    @Autowired
    JmxApplicationService appService;
    @Autowired
    ThreadService threadService;
    @Autowired
    JmxStaitisService jmxStaitisService;
    @Autowired
    SystemThresholdService systemThresholdService;
    @Autowired
    JmxConnectionFailService connectionFailService;
    @Autowired
    JmxNormalService jmxNormalService;
    @Autowired
    JmxConnectionPool jmxConnectionPool;
    @Autowired
    JmxConnectionFailDAO jmxConnectionFailDAO;

    static {
        list.add("db");
        list.add("memory");
        list.add("thread");
        list.add("gc");
    }

    // 暂时废弃链接了
    @RequestMapping(value = "/jmx/detail/main.html")
    public String detail(Model model, String server, String app) {
        model.addAttribute("page", "detail");
        ServerConvert convert = new ServerConvert(server, app);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        return "/detail/detail";
    }

    @RequestMapping(value = "/jmx/detail/memory.html")
    public String memory(Model model, String server, String app) {
        model.addAttribute("page", "memory");
        ServerConvert convert = new ServerConvert(server, app);
        String heapparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.MEMORY_KEY_HEAP, JmxParameter.MEMORY_PARAM_NAME);

        String survivorparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.MEMORY_KEY_SURVIVOR, JmxParameter.MEMORY_PARAM_NAME);

        String edenparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.MEMORY_KEY_EDEN, JmxParameter.MEMORY_PARAM_NAME);

        String oldparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.MEMORY_KEY_OLD, JmxParameter.MEMORY_PARAM_NAME);
        model.addAttribute("heap", heapparam);
        model.addAttribute("survivor", survivorparam);
        model.addAttribute("eden", edenparam);
        model.addAttribute("old", oldparam);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        return "detail/memory";
    }

    @RequestMapping(value = "/jmx/detail/database.html")
    public String database(Model model, String server, String app) {
        model.addAttribute("page", "db");

        ServerConvert convert = new ServerConvert(server, app);
        String activeparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.DB_PARAM_KEY_ACTIVECOUNT, JmxParameter.DB_PARAM_NAME);
        String idleparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.DB_PARAM_KEY_IDLECOUNT, JmxParameter.DB_PARAM_NAME);

        String failparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.DB_PARAM_KEY_FAILCOUNT, JmxParameter.DB_PARAM_NAME);
        model.addAttribute("activecount", activeparam);
        model.addAttribute("idlecount", idleparam);
        model.addAttribute("failcount", failparam);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        return "detail/database";
    }

    @RequestMapping(value = "/jmx/detail/dump.html")
    public String dump(HttpServletRequest request, Model model, String server, String app, String dumpType,
            String startdate, String enddate) {
        int currentPage = request.getParameter("page") == null ? 1 : NumberUtils.toInt(request.getParameter("page"), 1);
        if (currentPage <= 0) {
            currentPage = 1;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = null;
        Date end = null;
        try {
            if (StringUtils.isNotEmpty(startdate)) {
                start = format.parse(startdate);
            }
            if (StringUtils.isNotEmpty(enddate)) {
                end = format.parse(enddate);
            }
        }
        catch (Exception e) {
            log.error("DetailController::dump()", e);
        }
        model.addAttribute("dumpType", dumpType);
        model.addAttribute("start", startdate);
        model.addAttribute("end", enddate);
        model.addAttribute("page", "dump");
        Pager<JmxThreadDumpInfo> pager = threadService.getDumpInfos(currentPage, 3, dumpType, start, end, server, app);
        model.addAttribute("dumpInfos", pager.getItems());
        model.addAttribute("paginationBar", pager.getPaginationBar(getHref(app, server, dumpType, startdate, enddate)));
        ServerConvert convert = new ServerConvert(server, app);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        return "detail/dump";
    }

    @RequestMapping(value = "/jmx/detail/failconn.html")
    public String dump(HttpServletRequest request, Model model, String server, String app) {
        ServerConvert convert = new ServerConvert(server, app);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        model.addAttribute("page", "failconn");

        return "detail/jmxfailconn";
    }

    @RequestMapping(value = "/jmx/detail/cpu.html")
    public String getCpu(HttpServletRequest request, Model model, String server, String app) {
        ServerConvert convert = new ServerConvert(server, app);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        model.addAttribute("page", "cpu");

        return "detail/cpu";
    }

    private String getHref(String app, String server, String dumpType, String startDate, String endDate) {
        StringBuilder sb = new StringBuilder("/jmx/detail/dump.html?server=" + server + "&app=" + app);
        if (StringUtils.isNotEmpty(dumpType)) {
            sb.append("&dumpType=" + dumpType);
        }
        if (StringUtils.isNotEmpty(startDate)) {
            sb.append("&startdate=" + startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sb.append("&enddate=" + endDate);
        }
        return sb.toString();
    }

    // dump文件下载
    @RequestMapping(value = "/jmx/dumpfile/download.html")
    public void fileDownload(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getParameter("filePath");
        String[] splitArr = StringUtils.split(filePath, File.separator);
        response.setContentType("application/octet-stream");
        String fileName = splitArr[splitArr.length - 1];
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        filePath = System.getProperty("dump_path") + filePath;
        try {
            bis = new BufferedInputStream(new FileInputStream(filePath.toString()));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("DetailController::fileDownload", e);
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (Exception e) {
            }
        }

    }

    /**
     * 实时dump线程堆栈
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/jmx/dumpnow/download.html")
    public String dumpInfoNow(HttpServletRequest request, HttpServletResponse response) {
        String hostId = request.getParameter("server");
        String appId = request.getParameter("app");
        String url = "redirect:" + getHref(appId, hostId, null, null, null);
        if (NumberUtils.toInt(hostId, 0) <= 0) {
            return url;
        }

        JmxServer jmxServer = jmxServerService.selectServerByHostId(NumberUtils.toInt(hostId, 0));
        if (null == jmxServer) {
            return url;
        }

        Server server = Server.builder().setHost(jmxServer.getHostIp()).setPort(jmxServer.getJmxPort() + "")
                .setAlias(jmxServer.getHostIp()).setNumQueryThreads(1).setHostId(jmxServer.getHostId())
                .setAppId(jmxServer.getAppId()).build();
        MBeanServerConnection mbeanServer = null;
        JMXConnector conn = null;
        JMXConnectionParams connectionParams = null;
        try {

            connectionParams = new JMXConnectionParams(server.getJmxServiceURL(), server.getEnvironment());
            if (server.isLocal()) {
                mbeanServer = server.getLocalMBeanServer();
            }
            else {
                conn = jmxConnectionPool.getJmxPool().borrowObject(connectionParams);
                mbeanServer = conn.getMBeanServerConnection();
            }

            JmxThreadDumpInfo dumpInfo = new JmxThreadDumpInfo();
            dumpInfo.setHostId(server.getHostId());
            dumpInfo.setAppId(server.getAppId());
            dumpInfo.setReasonType(JmxThreadDumpInfo.HAND_DUMP);
            dumpInfo.setReason(JmxThreadDumpInfo.HAND_DUMP_STR);
            dumpInfo.setAddTime(new Date());
            dumpInfo.setFilePath(getThreadDumpFilePath());

            new JmxThreadDumpProcessor().dump(mbeanServer, dumpInfo);

            Thread.currentThread().sleep(1500l);
        }
        catch (Exception e) {
            log.error("Error in job server: " + server, e);
            JmxConnectionFail po = new JmxConnectionFail();
            po.setAddTime(new Date());
            po.setAppId(jmxServer.getAppId());
            po.setHostId(server.getHostId());
            po.setReason(StringUtils.substring(e.getMessage(), 0, 100));
            jmxConnectionFailDAO.addFailRecord(po);
        }
        finally {
            try {
                if (null != connectionParams && null != conn) {
                    jmxConnectionPool.getJmxPool().returnObject(connectionParams, conn);
                }
            }
            catch (Exception e) {
                log.error("Error returning object to pool for server: " + server, e);
            }
        }

        return url;

    }

    @RequestMapping(value = "/jmx/detail/thread.html")
    public String thread(Model model, String server, String app) {
        model.addAttribute("page", "thread");

        ServerConvert convert = new ServerConvert(server, app);
        String activeparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.THREAD_KEY_ACTIVE, JmxParameter.THREAD_PARAM_NAME);

        String idleparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.THREAD_KEY_IDLE, JmxParameter.THREAD_PARAM_NAME);

        String totalparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.THREAD_KEY_TOTAL, JmxParameter.THREAD_PARAM_NAME);

        model.addAttribute("activecount", activeparam);
        model.addAttribute("idlecount", idleparam);
        model.addAttribute("totalcount", totalparam);

        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        return "detail/thread";
    }

    @RequestMapping(value = "/jmx/detail/gc.html")
    public String gc(Model model, String server, String app) {
        model.addAttribute("page", "gc");
        ServerConvert convert = new ServerConvert(server, app);
        String collectionparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.CMS_KEY_COLLECTION, JmxParameter.CMS_PARAM_NAME);

        String durationparam = systemThresholdService.selectParamterValue(convert.getAppId(),
                JmxParameter.PARAMTYPE_THRESHOLD, JmxParameter.CMS_KEY_DURATION, JmxParameter.CMS_PARAM_NAME);
        model.addAttribute("collection", collectionparam);
        model.addAttribute("duration", durationparam);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        statisExceptionCount(model, convert.getHostId(), convert.getAppId());
        return "detail/gc";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/jmx/detail/sub.html")
    public String sub(HttpServletRequest request, Model model, String server, String app, String objName,
            String startdate, String enddate) {
        int currentPage = request.getParameter("page") == null ? 1 : NumberUtils.toInt(request.getParameter("page"), 1);
        if (currentPage <= 0) {
            currentPage = 1;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = null;
        Date end = null;
        try {
            if (StringUtils.isNotEmpty(startdate)) {
                start = format.parse(startdate);
            }
            if (StringUtils.isNotEmpty(enddate)) {
                end = format.parse(enddate);
            }
        }
        catch (Exception e) {
            log.error("DetailController::sub()", e);
        }

        model.addAttribute("objName", objName);
        model.addAttribute("start", startdate);
        model.addAttribute("end", enddate);
        // Pager<JmxThreadDumpInfo> pager = threadService.getDumpInfos(currentPage, 3, dumpType, start, end, server,
        // app);
        ServerConvert convert = new ServerConvert(server, app);
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        model.addAttribute("page", "sub");
        setServerCommonModel(model, convert.getHostId(), convert.getAppId());
        List<String> objs = jmxNormalService.selectObjsByhostId(convert.getHostId());
        model.addAttribute("objNames", objs);

        // 获取订阅数据
        List<JmxNormal> list = jmxNormalService.selectNormalInfo(objName, convert.getHostId(), 10, start, end);
        if (CollectionUtils.isNotEmpty(list)) {
            StringBuffer s = new StringBuffer();
            for (JmxNormal val : list) {
                s.append(val.getNormalVal()).append(",");
            }
            // s.append("]");
            System.out.println(s.toString());
            model.addAttribute("subArray", HtmlUtils.htmlEscape(s.toString()));

        }

        return "detail/sub";
    }

    private void setServerCommonModel(Model model, Integer sid, Integer appid) {

        JmxServer server = jmxServerService.selectDetailInfo(sid, appid);
        JmxApplication appObj = appService.selectAppByAppId(appid.intValue());
        model.addAttribute("appObj", appObj);
        model.addAttribute("serverObj", server);
    }

    private void statisExceptionCount(Model model, Integer hostId, Integer appId) {
        for (String s : list) {
            int count = jmxStaitisService.statisExceptions(appId, hostId, s, 24);
            model.addAttribute(s + "_error", count);
        }
    }

    /***
     * 转化server app String类型至Integer
     * 
     * @author liulin
     */
    private class ServerConvert {
        String server;
        String app;

        public ServerConvert(String server, String app) {
            this.app = app;
            this.server = server;
        }

        public Integer getAppId() {
            return NumberUtils.toInt(app, 0);
        }

        public Integer getHostId() {
            return NumberUtils.toInt(server, 0);
        }

    }

    private String getThreadDumpFilePath() {
        StringBuilder sb = new StringBuilder("thread_dump_file");
        sb.append(File.separator);
        Calendar calendar = Calendar.getInstance();
        sb.append("thread-dump.").append(calendar.get(Calendar.YEAR)).append("-")
                .append(calendar.get(Calendar.MONTH) + 1).append("-").append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(".").append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.MINUTE))
                .append(calendar.get(Calendar.MILLISECOND));
        return sb.toString();
    }

}
