package com.focustech.jmx;

import static com.focustech.jmx.model.Server.mergeServerLists;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.client.JmxWatcherConfiguration;
import com.focustech.jmx.exceptions.LifecycleException;
import com.focustech.jmx.model.JmxProcess;
import com.focustech.jmx.model.OutputWriter;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.model.ValidationException;
import com.focustech.jmx.util.JsonUtils;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class JmxWatcher {

    private static final Logger log = LoggerFactory.getLogger(JmxWatcher.class);

    private final Scheduler serverScheduler;

    // private WatchDir watcher;

    private final JmxWatcherConfiguration configuration;

    private List<Server> masterServersList = new ArrayList<Server>();

    /**
     * The shutdown hook.
     */
    private Thread shutdownHook = new ShutdownHook();

    private volatile boolean isRunning = false;

    private final Injector injector;

    @Inject
    public JmxWatcher(Scheduler serverScheduler, JmxWatcherConfiguration configuration, Injector injector) {
        this.serverScheduler = serverScheduler;
        this.configuration = configuration;
        this.injector = injector;
    }

    /**
     * Start.
     * 
     * @throws LifecycleException the lifecycle exception
     */
    public synchronized void start() throws LifecycleException {
        if (isRunning) {
            throw new LifecycleException("Process already started");
        }
        else {
            try {
                this.serverScheduler.start();

                // this.startupWatchdir();

                this.startupSystem();

            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new LifecycleException(e);
            }

            // Ensure resources are free
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            isRunning = true;
        }
    }

    /**
     * Stop.
     * 
     * @throws LifecycleException the lifecycle exception
     */
    public synchronized void stop() throws LifecycleException {
        if (!isRunning) {
            throw new LifecycleException("Process already stoped");
        }
        else {
            try {
                log.info("Stopping Jmxtrans");

                // Remove hook to not call twice
                if (shutdownHook != null) {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                }

                this.stopServices();
                isRunning = false;
            }
            catch (LifecycleException e) {
                log.error(e.getMessage(), e);
                throw new LifecycleException(e);
            }
        }
    }

    /**
     * Stop services.
     * 
     * @throws LifecycleException the lifecycle exception
     */
    // There is a sleep to work around a Quartz issue. The issue is marked to be
    // fixed, but will require further analysis. This should not be reported by
    // Findbugs, but as a more complex issue.
    @SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "Workaround for Quartz issue")
    private synchronized void stopServices() throws LifecycleException {
        try {
            // Shutdown the scheduler
            if (this.serverScheduler.isStarted()) {
                this.serverScheduler.shutdown(true);
                log.debug("Shutdown server scheduler");
                try {
                    // FIXME: Quartz issue, need to sleep
                    Thread.sleep(1500);
                }
                catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }

            // Shutdown the outputwriters
            this.stopWriterAndClearMasterServerList();

        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LifecycleException(e);
        }
    }

    private void stopWriterAndClearMasterServerList() {
        for (Server server : this.masterServersList) {
            for (Query query : server.getQueries()) {
                for (OutputWriter writer : query.getOutputWriters()) {
                    try {
                        writer.stop();
                        log.debug("Stopped writer: " + writer.getClass().getSimpleName() + " for query: " + query);
                    }
                    catch (LifecycleException ex) {
                        log.error("Error stopping writer: " + writer.getClass().getSimpleName() + " for query: "
                                + query);
                    }
                }
            }
        }
        this.masterServersList.clear();
    }

    /**
     * Startup the watchdir service.
     */
    // private void startupWatchdir() throws Exception {
    // File dirToWatch;
    // if (this.configuration.getJsonDirOrFile().isFile()) {
    // dirToWatch = new File(FilenameUtils.getFullPath(this.configuration.getJsonDirOrFile().getAbsolutePath()));
    // }
    // else {
    // dirToWatch = this.configuration.getJsonDirOrFile();
    // }
    // }

    public void executeStandalone(JmxProcess process) throws Exception {
        this.masterServersList = process.getServers();

        this.serverScheduler.start();

        this.processServersIntoJobs();

        Thread.sleep(10 * 1000);
    }

    /**
     * 动态加载配置服务
     * 
     * @throws LifecycleException
     */
    private void startupSystem() throws LifecycleException {
        // // process all the json files into Server objects
        this.processFilesIntoServers();

        // process the servers into jobs
        this.processServersIntoJobs();
    }

    private void validateSetup(Server server, ImmutableSet<Query> queries) throws ValidationException {
        for (Query q : queries) {
            this.validateSetup(server, q);
        }
    }

    private void validateSetup(Server server, Query query) throws ValidationException {
        List<OutputWriter> writers = query.getOutputWriters();
        for (OutputWriter w : writers) {
            injector.injectMembers(w);
            try {
                w.validateSetup(server, query);
            }
            catch (javax.xml.bind.ValidationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes all the json files and manages the dedup process
     */
    private void processFilesIntoServers() throws LifecycleException {
        // Shutdown the outputwriters and clear the current server list - this gives us a clean
        // start when re-reading the json config files
        try {
            this.stopWriterAndClearMasterServerList();
        }
        catch (Exception e) {
            log.error("Error while clearing master server list: " + e.getMessage(), e);
            throw new LifecycleException(e);
        }

        for (File jsonFile : getJsonFiles()) {
            JmxProcess process;
            try {
                process = JsonUtils.getJmxProcess(jsonFile);
                if (log.isDebugEnabled()) {
                    log.debug("Loaded file: " + jsonFile.getAbsolutePath());
                }
                this.masterServersList = mergeServerLists(this.masterServersList, process.getServers());
            }
            catch (Exception ex) {
                if (configuration.isContinueOnJsonError()) {
                    throw new LifecycleException("Error parsing json: " + jsonFile, ex);
                }
                else {
                    // error parsing one file should not prevent the startup of JMXTrans
                    log.error("Error parsing json: " + jsonFile, ex);
                }
            }
        }

    }

    /**
     * Processes all the Servers into Job's
     * <p/>
     * Needs to be called after processFiles()
     */
    private void processServersIntoJobs() throws LifecycleException {
        for (Server server : this.masterServersList) {
            try {

                // need to inject the poolMap
                for (Query query : server.getQueries()) {
                    for (OutputWriter writer : query.getOutputWriters()) {
                        writer.start();
                    }
                }

                // Now validate the setup of each of the OutputWriter's per
                // query.
                this.validateSetup(server, server.getQueries());

                // Now schedule the jobs for execution.
                this.scheduleJob(server);
            }
            catch (ParseException ex) {
                throw new LifecycleException("Error parsing cron expression: " + server.getCronExpression(), ex);
            }
            catch (SchedulerException ex) {
                throw new LifecycleException("Error scheduling job for server: " + server, ex);
            }
            catch (ValidationException ex) {
                throw new LifecycleException("Error validating json setup for query", ex);
            }
        }
    }

    /**
     * Schedules an individual job.
     */
    private void scheduleJob(Server server) throws ParseException, SchedulerException {

        // String name = server.getHost() + ":" + server.getPort();
        // JobDetail jd = new JobDetail(name, "ServerJob", ServerJob.class);
        //
        // JobDataMap map = new JobDataMap();
        // map.put(Server.class.getName(), server);
        // jd.setJobDataMap(map);
        //
        // Trigger trigger;
        //
        // if ((server.getCronExpression() != null) && CronExpression.isValidExpression(server.getCronExpression())) {
        // trigger = new CronTrigger();
        // ((CronTrigger) trigger).setCronExpression(server.getCronExpression());
        // trigger.setName(server.getHost() + ":" + server.getPort());
        // trigger.setStartTime(new Date());
        // }
        // else {
        // Trigger minuteTrigger = TriggerUtils.makeSecondlyTrigger(configuration.getRunPeriod());
        // minuteTrigger.setName(server.getHost() + ":" + server.getPort());
        // minuteTrigger.setStartTime(new Date());
        //
        // trigger = minuteTrigger;
        // }
        //
        // serverScheduler.scheduleJob(jd, trigger);
        // if (log.isDebugEnabled()) {
        // log.debug("Scheduled job: " + jd.getName() + " for server: " + server);
        // }
    }

    /**
     * Deletes all of the Jobs
     */
    private void deleteAllJobs() throws Exception {
        // List<JobDetail> allJobs = new ArrayList<JobDetail>();
        // String[] jobGroups = serverScheduler.getJobGroupNames();
        // for (String jobGroup : jobGroups) {
        // String[] jobNames = serverScheduler.getJobNames(jobGroup);
        // for (String jobName : jobNames) {
        // allJobs.add(serverScheduler.getJobDetail(jobName, jobGroup));
        // }
        // }
        //
        // for (JobDetail jd : allJobs) {
        // serverScheduler.deleteJob(jd.getName(), jd.getGroup());
        // if (log.isDebugEnabled()) {
        // log.debug("Deleted scheduled job: " + jd.getName() + " group: " + jd.getGroup());
        // }
        // }
    }

    private List<File> getJsonFiles() {
        // TODO : should use a FileVisitor (Once we update to Java 7)
        File[] files;
        if ((this.configuration.getJsonDirOrFile() != null) && this.configuration.getJsonDirOrFile().isFile()) {
            files = new File[1];
            files[0] = this.configuration.getJsonDirOrFile();
        }
        else {
            files = this.configuration.getJsonDirOrFile().listFiles();
        }

        List<File> result = new ArrayList<File>();
        for (File file : files) {
            if (this.isJsonFile(file)) {
                result.add(file);
            }
        }
        return result;
    }

    private boolean isJsonFile(File file) {
        if (this.configuration.getJsonDirOrFile().isFile()) {
            return file.equals(this.configuration.getJsonDirOrFile());
        }

        return file.isFile() && file.getName().endsWith(".json");
    }

    public void fileModified(File file) throws Exception {
        if (this.isJsonFile(file)) {
            Thread.sleep(1000);
            log.info("Configuration file modified: " + file);
            this.deleteAllJobs();
            this.startupSystem();
        }
    }

    public void fileDeleted(File file) throws Exception {
        log.info("Configuration file deleted: " + file);
        Thread.sleep(1000);
        this.deleteAllJobs();
        this.startupSystem();
    }

    public void fileAdded(File file) throws Exception {
        if (this.isJsonFile(file)) {
            Thread.sleep(1000);
            log.info("Configuration file added: " + file);
            this.deleteAllJobs();
            this.startupSystem();
        }
    }

    protected class ShutdownHook extends Thread {
        public void run() {
            try {
                JmxWatcher.this.stopServices();
            }
            catch (LifecycleException e) {
                log.error("Error shutdown hook", e);
            }
        }
    }
}
