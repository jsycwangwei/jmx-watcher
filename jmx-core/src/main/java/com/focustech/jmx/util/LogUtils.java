package com.focustech.jmx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.log.LogCategory;

/**
 * 日志工具类
 *
 * @author wangwei-ww
 */
public class LogUtils {
    public static final Logger optLog = LoggerFactory.getLogger("opt");

    public static final Logger jmxLog = LoggerFactory.getLogger("com.focustech.jmx");

    public static final Logger dumpLog = LoggerFactory.getLogger(LogCategory.DUMP.toString());

    /**
     * 记录错误日志供实时报警系统调用,本系统报警不用这个函数
     * @param err
     */
    public static void logDumpErrorInfo(String err){
        dumpLog.error(err);
    }
}
