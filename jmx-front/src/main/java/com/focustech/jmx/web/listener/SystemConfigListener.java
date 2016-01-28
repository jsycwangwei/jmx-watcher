package com.focustech.jmx.web.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.focustech.jmx.log.LogCategory;

public class SystemConfigListener implements ServletContextListener {

    protected Log logger = LogFactory.getLog(LogCategory.SERVICE.toString());

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ResourcePatternResolver res = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = res.getResources("classpath*:/context/properties/conf-*.properties");
            for (Resource resource : resources) {
                initCofig(resource);
            }
        }
        catch (IOException e) {
            logger.error("init system properties error:" + e);
        }
    }

    private void initCofig(Resource resource) {
        Properties conf = new Properties();
        InputStream is = null;
        try {
            is = resource.getInputStream();
            conf.load(is);
            conf.putAll(System.getProperties());
            System.setProperties(conf);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    logger.error("initConfig error ", e);
                }
            }
        }
    }
}
