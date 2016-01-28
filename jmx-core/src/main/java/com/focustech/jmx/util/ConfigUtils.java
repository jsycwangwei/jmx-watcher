package com.focustech.jmx.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.focustech.jmx.guice.JmxWatcherModule;
import com.google.common.io.Closer;

/**
 * 系统配置工具
 * 
 * @author wangwei-ww
 */
public class ConfigUtils {

    static String DUMP_PATH = "dump_path";

    static Properties properties = new Properties();

    static {
        InputStream stream;
        Closer closer = Closer.create();
        stream = closer.register(JmxWatcherModule.class.getClassLoader().getResourceAsStream("conf-app.properties"));
        try {
            properties.load(stream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String val) {
        properties.setProperty(key, val);
    }

    public static void main(String[] args) {
        System.out.println(getProperty(DUMP_PATH));
    }
}
