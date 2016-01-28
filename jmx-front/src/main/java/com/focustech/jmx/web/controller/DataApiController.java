package com.focustech.jmx.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.focustech.jmx.face.PagerJsonObj;
import com.focustech.jmx.face.SelectObj;
import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxCms;
import com.focustech.jmx.po.JmxConnectionFail;
import com.focustech.jmx.po.JmxCpu;
import com.focustech.jmx.po.JmxDatabase;
import com.focustech.jmx.po.JmxMemory;
import com.focustech.jmx.po.JmxParameter;
import com.focustech.jmx.po.JmxServer;
import com.focustech.jmx.po.JmxThread;
import com.focustech.jmx.po.JmxThreadDumpInfo;
import com.focustech.jmx.po.Pager;
import com.focustech.jmx.pojo.JmxConnectionFailPOJO;
import com.focustech.jmx.pojo.JmxDatabasePOJO;
import com.focustech.jmx.pojo.JmxMemoryPOJO;
import com.focustech.jmx.pojo.JmxServerPOJO;
import com.focustech.jmx.pojo.JmxThreadPOJO;
import com.focustech.jmx.service.JmxApplicationService;
import com.focustech.jmx.service.JmxCmsService;
import com.focustech.jmx.service.JmxConnectionFailService;
import com.focustech.jmx.service.JmxCpuService;
import com.focustech.jmx.service.JmxDatabaseService;
import com.focustech.jmx.service.JmxMemoryService;
import com.focustech.jmx.service.JmxServerService;
import com.focustech.jmx.service.JmxStaitisService;
import com.focustech.jmx.service.SystemThresholdService;
import com.focustech.jmx.service.ThreadService;
import com.focustech.jmx.util.ConvertUtils;

@Controller
@RequestMapping("/api")
public class DataApiController {

    protected Log log = LogFactory.getLog(LogCategory.CONTROLLER.toString());
    @Autowired
    JmxApplicationService appService;
    @Autowired
    JmxServerService serverService;
    @Autowired
    JmxMemoryService memoryService;
    @Autowired
    JmxCmsService gcService;
    @Autowired
    ThreadService threadService;
    @Autowired
    JmxCpuService cpuService;

    @Autowired
    JmxDatabaseService dbService;

    @Autowired
    SystemThresholdService paramService;

    @Autowired
    JmxStaitisService staitisService;

    @Autowired
    JmxConnectionFailService connectionFailService;

    private final static String DEFAULT_TIME_RANGE_STR = System.getProperty("jmx.default.monitor.timerange");
    private static Integer DEFAULT_TIME_RANGE = 10;

    public final static int FAILCONN_PAGESIZE = 10;

    static {
        if (StringUtils.isNotBlank(DEFAULT_TIME_RANGE_STR)) {
            DEFAULT_TIME_RANGE = Integer.parseInt(DEFAULT_TIME_RANGE_STR, 10);
        }
    }

    @RequestMapping("overview/topevent")
    @ResponseBody
    public String getOverviewTopEvent(Integer topnum) {
        if (topnum == null) {
            topnum = 10;
        }
        JSONObject jsonObj = new JSONObject();
        JSONArray dumpArray = new JSONArray();
        try {
            List<JmxThreadDumpInfo> dumplist = threadService.getAppAbnormalDumpInfos(null, null, null, null, 0, topnum);
            for (JmxThreadDumpInfo dump : dumplist) {
                JSONObject dumpobj = new JSONObject();
                JmxServerPOJO serverinfo = serverService.selectServerExtraInfo(dump.getHostId(), dump.getAppId());
                dumpobj.put("reason", dump.getReason());
                dumpobj.put("servername", serverinfo.getHostIp());
                dumpobj.put("appname", serverinfo.getAppName());
                dumpobj.put("dumppath", dump.getFilePath());
                dumpobj.put("dumptime", ConvertUtils.convertDateToString(dump.getAddTime(), null));
                dumpArray.add(dumpobj);

            }
        }
        catch (Exception e) {
            log.error("data api getOverviewTopEvent", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", dumpArray);
        return jsonObj.toString();
    }

    /***
     * 获取jmx连接监控应用失败的次数
     * 
     * @return
     */
    @RequestMapping("overview/jmxFailConn")
    @ResponseBody
    public String getOverviewFailConn(String project, String app, int currentpage) {

        JSONObject jsonObj = new JSONObject();
        JSONArray failArray = new JSONArray();
        JSONObject dataObj = new JSONObject();
        Integer appid = 0;
        if (StringUtils.isEmpty(project)) {
            project = null;
        }
        if (StringUtils.isNotEmpty(app)) {
            appid = Integer.parseInt(app);
        }
        int totalcount = 0;
        int totalPage = 0;
        try {
            Pager<JmxConnectionFailPOJO> pager =
                    staitisService.selectStatisFailRecord(project, appid, currentpage, FAILCONN_PAGESIZE);
            totalPage = pager.getTotalPage();
            for (JmxConnectionFailPOJO po : pager.getItems()) {
                JSONObject failobj = new JSONObject();
                failobj.put("appid", po.getAppId());
                failobj.put("hostid", po.getHostId());
                failobj.put("appname", po.getAppName());
                failobj.put("failcount", po.getCount());
                failobj.put("lasttime", ConvertUtils.convertDateToString(po.getAddTime(), null));
                failobj.put("hostip", po.getHostIp());
                totalcount += po.getCount();
                failArray.add(failobj);
            }
        }
        catch (Exception e) {
            log.error("data api getOverviewFailConn", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        dataObj.put("pager", new PagerJsonObj(totalPage, currentpage).toJson());
        dataObj.put("serverfail", failArray);
        jsonObj.put("data", dataObj);
        return jsonObj.toString();
    }

    @RequestMapping("overview/dbactivecount")
    @ResponseBody
    public String getOverviewDBActive() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dbArray = new JSONArray();
        try {
            List<JmxDatabasePOJO> totaldbactive = dbService.selectDBActiveConn();
            for (JmxDatabasePOJO activepo : totaldbactive) {
                JSONArray db = new JSONArray();
                db.add(activepo.getActiveCount());
                db.add(activepo.getAppName());
                db.add(activepo.getHostIp());
                dbArray.add(db);
            }
        }
        catch (Exception e) {
            log.error("data api getOverviewDBActive", e);
        }

        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", dbArray);
        return jsonObj.toString();
    }

    @RequestMapping("overview/threadactivecount")
    @ResponseBody
    public String getOverviewThreadActive() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dbArray = new JSONArray();
        try {
            List<JmxThreadPOJO> totalthreadactive = threadService.getAllServerNowThreadActiveCount();
            for (JmxThreadPOJO activepo : totalthreadactive) {
                JSONArray item = new JSONArray();
                item.add(activepo.getActiveCount());
                item.add(activepo.getAppName());
                item.add(activepo.getHostIp());
                dbArray.add(item);
            }
        }
        catch (Exception e) {
            log.error("data api getOverviewThreadActive", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", dbArray);
        return jsonObj.toString();
    }

    @RequestMapping("overview/mheapcount")
    @ResponseBody
    public String getOverviewMemoryHeap() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dbArray = new JSONArray();
        try {
            List<JmxMemoryPOJO> totalmemoryactive = memoryService.getAllServerNowMemoryHeap();
            for (JmxMemoryPOJO activepo : totalmemoryactive) {
                JSONArray item = new JSONArray();
                item.add(activepo.getHeapMemorySize());
                item.add(activepo.getAppName());
                item.add(activepo.getHostIp());
                dbArray.add(item);
            }
        }
        catch (Exception e) {
            log.error("data api getOverviewMemoryHeap", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", dbArray);
        return jsonObj.toString();
    }

    @RequestMapping("serverlist")
    @ResponseBody
    public String getServerList(String project, String appid) {
        JSONObject jsonObj = new JSONObject();
        JSONArray appArray = new JSONArray();
        List<JmxApplication> apps = null;
        try {
            if (StringUtils.isEmpty(appid)) {
                if (StringUtils.isEmpty(project)) {
                    project = null;
                }
                apps = appService.selectMonitorAppByProject(project);
            }
            else {
                apps = new ArrayList<JmxApplication>();
                apps.add(appService.selectAppByAppId(Integer.parseInt(appid)));
            }

            if (CollectionUtils.isNotEmpty(apps)) {
                int appIndex = 1;
                for (JmxApplication app : apps) {
                    JSONObject appObject = new JSONObject();
                    JSONArray serverArray = new JSONArray();
                    appObject.put("appid", app.getAppId());
                    appObject.put("id", appIndex);
                    appObject.put("appname", app.getAppName());
                    int appfailcount = staitisService.statisServerAbnormalExceptions(app.getAppId(), null, 24);
                    appObject.put("failCount", appfailcount);
                    List<JmxServer> servers = serverService.selectServersByAppId(app.getAppId());
                    for (JmxServer server : servers) {
                        JSONObject serverObj = new JSONObject();
                        // JSONObject sp = new JSONObject();
                        serverObj.put("serverid", server.getHostId());
                        serverObj.put("servername", server.getHostIp());
                        int serverfailcount =
                                staitisService
                                        .statisServerAbnormalExceptions(server.getAppId(), server.getHostId(), 24);
                        serverObj.put("failCount", serverfailcount);
                        // for (int i = 0; i < 3; i++) {
                        // sp.put("p" + i, i);
                        // }
                        // serverObj.put("params", sp);
                        serverArray.add(serverObj);
                    }
                    appObject.put("server", serverArray);
                    appArray.add(appObject);
                    appIndex++;
                }
            }
        }
        catch (Exception e) {
            log.error("data api getServerList", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", appArray);
        return jsonObj.toString();
    }

    @RequestMapping("serversetting")
    @ResponseBody
    public String getServerSetting(String project) {
        JSONObject jsonObj = new JSONObject();
        JSONObject totalObj = new JSONObject();
        JSONArray appArray = new JSONArray();
        JSONArray serverArray = new JSONArray();
        try {
            List<JmxApplication> apps = appService.selectAppByProject(project);
            List<JmxServer> servers = serverService.selectServersByApps(apps);
            for (JmxApplication app : apps) {
                JSONObject appObj = new JSONObject();
                appObj.put("appid", app.getAppId());
                appObj.put("appname", app.getAppName());
                appArray.add(appObj);
            }
            for (JmxServer server : servers) {
                JSONObject serverObj = new JSONObject();
                serverObj.put("appid", server.getAppId());
                serverObj.put("servername", server.getHostIp());
                serverObj.put("serverid", server.getHostId());
                serverObj.put("monitor", server.getJmxStatus() == 1);
                serverObj.put("unmonitor", server.getJmxStatus() == 0);
                serverArray.add(serverObj);
            }
        }
        catch (Exception e) {
            log.error("data api getServerSetting", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        totalObj.put("appdata", appArray);
        totalObj.put("serverdata", serverArray);
        jsonObj.put("data", totalObj);
        return jsonObj.toString();
    }

    @RequestMapping("appsetting")
    @ResponseBody
    public String getAppSetting(Integer app) {
        JSONObject jsonObj = new JSONObject();
        JSONArray typeArray = new JSONArray();
        try {
            if (app != null) {
                Map<String, List<JmxParameter>> typeMap = new HashMap<String, List<JmxParameter>>();
                List<JmxParameter> params = paramService.selectParamtersByAppid(app);
                for (JmxParameter p : params) {
                    if (!typeMap.containsKey(p.getParamName())) {
                        List<JmxParameter> paramslist = new ArrayList<JmxParameter>();
                        paramslist.add(p);
                        typeMap.put(p.getParamName(), paramslist);
                    }
                    else {
                        typeMap.get(p.getParamName()).add(p);
                    }
                }
                Set<Map.Entry<String, List<JmxParameter>>> set = typeMap.entrySet();
                Iterator<Map.Entry<String, List<JmxParameter>>> it = set.iterator();
                while (it.hasNext()) {
                    Entry<String, List<JmxParameter>> entry = it.next();
                    String paramName = entry.getKey();
                    List<JmxParameter> typeParams = entry.getValue();
                    JSONObject typeObj = new JSONObject();
                    JSONArray paramArray = new JSONArray();
                    typeObj.put("paramName", paramName);
                    for (JmxParameter p : typeParams) {
                        JSONObject pObj = new JSONObject();
                        pObj.put("appid", p.getAppId());
                        pObj.put("paramDesc", p.getParamDesc());
                        pObj.put("paramType", p.getParamType());
                        pObj.put("paramKey", p.getParamKey());
                        pObj.put("paramValue", p.getParamValue());
                        paramArray.add(pObj);
                    }
                    typeObj.put("params", paramArray);
                    typeArray.add(typeObj);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getAppSetting", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", typeArray);
        return jsonObj.toString();
    }

    @RequestMapping("detail/main/memoryinfo")
    @ResponseBody
    public String getDetailMemory(String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONObject lastObj = new JSONObject();
        Integer appId = NumberUtils.toInt(app, 0);
        Integer hostId = NumberUtils.toInt(server, 0);
        try {
            JmxMemory lastestmemory = memoryService.selectLastestMemoryInfo(appId, hostId);
            if (lastestmemory != null) {
                lastObj.put("heap_memory_size", lastestmemory.getHeapMemorySize());
                lastObj.put("noheap_memory_size", lastestmemory.getNoHeapMemorySize());
                lastObj.put("eden_size", lastestmemory.getEdenSize());
                lastObj.put("survivor_size", lastestmemory.getSurvivorSize());
                lastObj.put("old_size", lastestmemory.getOldSize());
                lastObj.put("perm_size", lastestmemory.getPermSize());
            }
        }
        catch (Exception e) {
            log.error("data api getDetailMemory", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", lastObj);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/threadActivecount")
    @ResponseBody
    public String getDetailThreadInfo(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Date[] dateArray = getFromToDateArray(from, to, null);
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            List<JmxThread> threadlist =
                    threadService.getThreadStatisticsInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
            for (JmxThread t : threadlist) {
                if (t.getActiveCount() != null && t.getActiveCount() != -1) {
                    Date addTime = t.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(t.getActiveCount());
                    values.add(t.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailThreadInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/threadIdlecount")
    @ResponseBody
    public String getDetailThreadActiveCountInfo(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Date[] dateArray = getFromToDateArray(from, to, null);
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            List<JmxThread> threadlist =
                    threadService.getThreadStatisticsInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
            for (JmxThread t : threadlist) {
                if (t.getIdleCount() != null && t.getIdleCount() != -1) {
                    Date addTime = t.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(t.getIdleCount());
                    values.add(t.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailThreadActiveCountInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/threadtotalcount")
    @ResponseBody
    public String getDetailThreadTotalCountInfo(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Date[] dateArray = getFromToDateArray(from, to, null);
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            List<JmxThread> threadlist =
                    threadService.getThreadStatisticsInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
            for (JmxThread t : threadlist) {
                if (t.getCurTotalCount() != null && t.getCurTotalCount() != -1) {
                    Date addTime = t.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(t.getCurTotalCount());
                    values.add(t.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailThreadTotalCountInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/threadinfo")
    @ResponseBody
    public String getDetailLastThreadInfo(String app, String server) {
        JSONObject jsonObj = new JSONObject();
        Integer serverId = NumberUtils.toInt(server, 0);
        JSONObject lastObj = new JSONObject();
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            JmxThread lastestThreadInfo = threadService.getLastestThreadInfo(appId, serverId);

            if (lastestThreadInfo != null) {
                lastObj.put("thread_active_count", lastestThreadInfo.getActiveCount());
                lastObj.put("thread_total_count", lastestThreadInfo.getCurTotalCount());
                lastObj.put("thread_idle_count", lastestThreadInfo.getIdleCount());
            }
        }
        catch (Exception e) {
            log.error("data api getDetailLastThreadInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", lastObj);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/oldsize")
    @ResponseBody
    public String getDetailOldSize(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxMemory> memorylist = getMemoryListByDate(from, to, app, server);
            for (JmxMemory m : memorylist) {
                if (m.getOldSize() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getOldSize());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailOldSize", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/heap")
    @ResponseBody
    public String getDetailHeap(String from, String to, String app, String server) {

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxMemory> memorylist = getMemoryListByDate(from, to, app, server);
            for (JmxMemory m : memorylist) {
                if (m.getHeapMemorySize() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getHeapMemorySize());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
            jsonObj.put("timestamp", new Date().toString());
            jsonObj.put("data", jsonArray);
        }
        catch (Exception e) {
            log.error("data api getDetailHeap", e);
        }
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/eden")
    @ResponseBody
    public String getDetailEden(String from, String to, String app, String server) {

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxMemory> memorylist = getMemoryListByDate(from, to, app, server);
            for (JmxMemory m : memorylist) {
                if (m.getEdenSize() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getEdenSize());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailEden", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/survivor")
    @ResponseBody
    public String getDetailSurvivor(String from, String to, String app, String server) {

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxMemory> memorylist = getMemoryListByDate(from, to, app, server);
            for (JmxMemory m : memorylist) {
                if (m.getSurvivorSize() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getSurvivorSize());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailSurvivor", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/gcduration")
    @ResponseBody
    public String getDetailGcDuration(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxCms> gclist = getGcListByDate(from, to, app, server);
            for (JmxCms m : gclist) {
                if (m.getDuration() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getDuration());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailGcDuration", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/gccollectionsize")
    @ResponseBody
    public String getDetailGcCollectionSize(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxCms> gclist = getGcListByDate(from, to, app, server);
            for (JmxCms m : gclist) {
                if (m.getCollectionSize() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getCollectionSize());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailGcCollectionSize", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/gcinfo")
    @ResponseBody
    public String getDetailGcInfo(String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONObject lastObj = new JSONObject();
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            JmxCms lastestCmsInfo = gcService.selectLastestCmsInfo(appId, serverId);

            if (lastestCmsInfo != null) {
                lastObj.put("collection_size", lastestCmsInfo.getCollectionSize());
                lastObj.put("duration", lastestCmsInfo.getDuration());
            }
        }
        catch (Exception e) {
            log.error("data api getDetailGcInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", lastObj);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/dbactivecount")
    @ResponseBody
    public String getDetailDBActiveCount(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxDatabase> dblist = getDBListByDate(from, to, app, server);
            for (JmxDatabase m : dblist) {
                if (m.getActiveCount() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getActiveCount());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailDBActiveCount", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/dbidlecount")
    @ResponseBody
    public String getDetailDBIdleCount(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxDatabase> dblist = getDBListByDate(from, to, app, server);
            for (JmxDatabase m : dblist) {
                if (m.getIdleCount() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getIdleCount());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailDBIdleCount", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/dbfailcount")
    @ResponseBody
    public String getDetailDBFailCount(String from, String to, String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxDatabase> dblist = getDBListByDate(from, to, app, server);
            for (JmxDatabase m : dblist) {
                if (m.getFailConnCount() != -1) {
                    Date addTime = m.getAddTime();
                    List<Object> values = new ArrayList<Object>();
                    values.add(m.getFailConnCount());
                    values.add(m.getAbnormalFlg());
                    JSONArray itemArray = getItemJsonArray(values, addTime);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailDBFailCount", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/dbinfo")
    @ResponseBody
    public String getDetailDBInfo(String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONObject lastObj = new JSONObject();
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            JmxDatabase lastestDB = dbService.selectLastestDatabaseInfo(appId, serverId);
            if (lastestDB != null) {
                lastObj.put("active_count", lastestDB.getActiveCount());
                lastObj.put("idle_count", lastestDB.getIdleCount());
                lastObj.put("fail_conn_count", lastestDB.getFailConnCount());
            }
        }
        catch (Exception e) {
            log.error("data api getDetailDBInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", lastObj);
        return jsonObj.toString();

    }

    @RequestMapping("server/appid")
    @ResponseBody
    public List<SelectObj> getServerlistByAppid(Integer appId) {
        // Map<String, List<SelectObj>> result = new HashMap<String, List<SelectObj>>();
        List<SelectObj> items = new ArrayList<SelectObj>();
        try {
            List<JmxServer> servers = serverService.selectServersByAppId(appId);
            for (JmxServer server : servers) {
                SelectObj so = new SelectObj();
                so.setId(server.getHostIp());
                so.setText(server.getHostIp());
                items.add(so);
            }
        }
        catch (Exception e) {
            log.error("data api getServerlistByAppid", e);
        }
        return items;

    }

    @RequestMapping("detail/main/dumpcount")
    @ResponseBody
    public String getDetailDumpCount(String app, String server) {
        JSONArray jsonArray = new JSONArray();
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            Map<String, Long> lastest = threadService.getLastest7dayDumpCount(appId, serverId);
            if (lastest != null) {
                Set<Map.Entry<String, Long>> set = lastest.entrySet();
                Iterator<Map.Entry<String, Long>> it = set.iterator();
                while (it.hasNext()) {
                    Entry<String, Long> entry = it.next();
                    String key = entry.getKey();
                    Long value = entry.getValue();
                    JSONArray itemArray = new JSONArray();
                    itemArray.add(key);
                    itemArray.add(value);
                    jsonArray.add(itemArray);
                }
            }
        }
        catch (Exception e) {
            log.error("data api getDetailDumpCount", e);
        }
        return jsonArray.toString();

    }

    @RequestMapping("detail/main/failconn")
    @ResponseBody
    public String getDetailFailConn(String app, String server, String from, String to, int currentpage) {
        JSONObject jsonObj = new JSONObject();
        JSONArray failArray = new JSONArray();
        JSONObject dataObj = new JSONObject();
        Integer hostId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        Date start = null;
        Date end = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if (StringUtils.isNotEmpty(from)) {
                start = format.parse(from);
            }
            if (StringUtils.isNotEmpty(to)) {
                end = format.parse(to);
            }
        }
        catch (Exception e) {
            log.error("DetailController::dump()", e);
        }
        int totalPage = 0;
        try {
            Pager<JmxConnectionFail> pager =
                    connectionFailService.selectFailRecord(appId, hostId, start, end, currentpage, FAILCONN_PAGESIZE);
            totalPage = pager.getTotalPage();
            for (JmxConnectionFail po : pager.getItems()) {
                JSONObject failobj = new JSONObject();
                failobj.put("fullreason", po.getReason());
                failobj.put("addtime", ConvertUtils.convertDateToString(po.getAddTime(), null));
                failArray.add(failobj);
            }
        }
        catch (Exception e) {
            log.error("data api getDetailFailConn", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        dataObj.put("pager", new PagerJsonObj(totalPage, currentpage).toJson());
        dataObj.put("serverfail", failArray);
        jsonObj.put("data", dataObj);
        return jsonObj.toString();

    }

    @RequestMapping("detail/main/cpuusage")
    @ResponseBody
    public String getDetailCpuUsage(String app, String server, String from, String to) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            List<JmxCpu> cpulist = getCpuListByDate(from, to, app, server);
            for (JmxCpu m : cpulist) {
                Date addTime = m.getAddTime();
                List<Object> values = new ArrayList<Object>();
                values.add(m.getCpuUsage() * 100);
                values.add(m.getAbnormalFlg());
                JSONArray itemArray = getItemJsonArray(values, addTime);
                jsonArray.add(itemArray);
            }
        }
        catch (Exception e) {
            log.error("data api getDetailCpuUsage", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", jsonArray);

        return jsonObj.toString();

    }

    @RequestMapping("detail/main/cpuinfo")
    @ResponseBody
    public String getDetailCpuInfo(String app, String server) {
        JSONObject jsonObj = new JSONObject();
        JSONObject lastObj = new JSONObject();
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        try {
            JmxCpu lastestCpu = cpuService.selectLastestCpuInfo(appId, serverId);
            if (lastestCpu != null) {
                lastObj.put("cpuusage", lastestCpu.getCpuUsage() * 100 + "%");
            }
        }
        catch (Exception e) {
            log.error("data api getDetailCpuInfo", e);
        }
        jsonObj.put("timestamp", new Date().toString());
        jsonObj.put("data", lastObj);
        return jsonObj.toString();

    }

    @RequestMapping("project/apps")
    @ResponseBody
    public List<SelectObj> getApplicationByProject(String project) {
        // Map<String, List<SelectObj>> result = new HashMap<String, List<SelectObj>>();
        List<SelectObj> items = new ArrayList<SelectObj>();
        try {
            List<JmxApplication> applications = appService.selectMonitorAppByProject(project);
            for (JmxApplication app : applications) {
                SelectObj so = new SelectObj();
                so.setId(app.getAppId() + "");
                so.setText(app.getAppName());
                items.add(so);
            }
        }
        catch (Exception e) {
            log.error("data api getApplicationByProject", e);
        }
        return items;

    }

    private List<JmxMemory> getMemoryListByDate(String from, String to, String app, String server) {
        Date[] dateArray = getFromToDateArray(from, to, null);
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        List<JmxMemory> memorylist = memoryService.selectMemoryInfoBySid(appId, serverId, dateArray[0], dateArray[1]);
        return memorylist;
    }

    private List<JmxCms> getGcListByDate(String from, String to, String app, String server) {
        Date[] dateArray = getFromToDateArray(from, to, null);
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        List<JmxCms> gclist = gcService.selectCmsInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
        return gclist;
    }

    private List<JmxDatabase> getDBListByDate(String from, String to, String app, String server) {
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        Date[] dateArray = getFromToDateArray(from, to, null);
        List<JmxDatabase> dblist = dbService.selectDatabaseInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
        return dblist;
    }

    private List<JmxCpu> getCpuListByDate(String from, String to, String app, String server) {
        Integer serverId = NumberUtils.toInt(server, 0);
        Integer appId = NumberUtils.toInt(app, 0);
        Date[] dateArray = getFromToDateArray(from, to, null);
        List<JmxCpu> cpulist = cpuService.selectCpuInfoByDate(appId, serverId, dateArray[0], dateArray[1]);
        return cpulist;
    }

    private JSONArray getItemJsonArray(List<Object> values, Date addTime) {
        Calendar mcal = Calendar.getInstance();
        mcal.setTime(addTime);
        JSONArray itemArray = new JSONArray();
        JSONArray dateArray = getJsonDateArray(addTime);
        itemArray.add(dateArray);
        for (Object value : values) {
            itemArray.add(value);
        }
        return itemArray;
    }

    private JSONArray getJsonDateArray(Date time) {
        Calendar mcal = Calendar.getInstance();
        mcal.setTime(time);
        JSONArray dateArray = new JSONArray();
        dateArray.add(mcal.get(Calendar.YEAR));
        dateArray.add(mcal.get(Calendar.MONTH));
        dateArray.add(mcal.get(Calendar.DAY_OF_MONTH));
        dateArray.add(mcal.get(Calendar.HOUR_OF_DAY));
        dateArray.add(mcal.get(Calendar.MINUTE));
        dateArray.add(mcal.get(Calendar.SECOND));
        return dateArray;
    }

    private Date[] getFromToDateArray(String from, String to, String formatStr) {
        Date[] dateArray = new Date[2];
        String defaultFormat = "yyyy-MM-dd HH:mm:ss";
        if (StringUtils.isNotEmpty(formatStr)) {
            defaultFormat = formatStr;
        }
        SimpleDateFormat format = new SimpleDateFormat(defaultFormat);
        Date toDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.add(Calendar.MINUTE, -DEFAULT_TIME_RANGE);
        Date fromDate = cal.getTime();
        if (StringUtils.isNotEmpty(from) || StringUtils.isNotEmpty(to)) {
            try {
                fromDate = format.parse(from);
                toDate = format.parse(to);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }
        dateArray[0] = fromDate;
        dateArray[1] = toDate;
        return dateArray;
    }

}
