package com.focustech.jmx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class ConvertUtils {
    public static int convertByte2MB(long size) {
        return NumberUtils.toInt((size / 1024 / 1024) + "");
    }

    public static int convertByte2KB(long size) {
        return NumberUtils.toInt((size / 1024) + "");
    }

    public static String convertDateToString(Date date, String pattern) {
        String defaultFormat = "yyyy-MM-dd HH:mm:ss";
        if (date == null) {
            return "";
        }
        if (StringUtils.isNotEmpty(pattern)) {
            defaultFormat = pattern;
        }
        SimpleDateFormat format = new SimpleDateFormat(defaultFormat);
        return format.format(date);

    }
}
