package com.smartproperty.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化工具类。
 */
public final class DateUtil {

    private DateUtil() {
    }

    public static String formatDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatDateTime(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * 取当前月份字符串，例如 2026-03。
     */
    public static String currentPeriod() {
        return new SimpleDateFormat("yyyy-MM").format(new Date());
    }
}
