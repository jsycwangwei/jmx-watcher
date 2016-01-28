package com.focustech.jmx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.log.LogCategory;
import com.focustech.jmx.model.Query;
import com.focustech.jmx.model.Server;
import com.focustech.jmx.process.JmxQueryProcessor;
import com.focustech.jmx.process.threads.ProcessQueryThread;

/**
 * The worker code.
 */
public class JmxUtils {

    private static final Logger log = LoggerFactory.getLogger(LogCategory.SERVICE.toString());

    public static void processServer(Server server, MBeanServerConnection mbeanServer) throws Exception {

        if (server.isQueriesMultiThreaded()) {
            ExecutorService service = null;
            try {
                service = Executors.newFixedThreadPool(server.getNumQueryThreads());
                if (log.isDebugEnabled()) {
                    log.debug("----- Creating " + server.getQueries().size() + " query threads");
                }

                List<Callable<Object>> threads = new ArrayList<Callable<Object>>(server.getQueries().size());
                for (Query query : server.getQueries()) {
                    ProcessQueryThread pqt = new ProcessQueryThread(mbeanServer, server, query);
                    threads.add(Executors.callable(pqt));
                }

                service.invokeAll(threads);

            }
            finally {
                if (service != null) {
                    shutdownAndAwaitTermination(service);
                }
            }
        }
        else {
            for (Query query : server.getQueries()) {
                new JmxQueryProcessor().processQuery(mbeanServer, server, query);
            }
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService service) {
        service.shutdown(); // Disable new tasks from being submitted
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException ie) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
