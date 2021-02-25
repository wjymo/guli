package com.zzn.nettytest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadLocalDateTimeUtil {

    private static ThreadLocal<SimpleDateFormat> dateTimeThreadLocal = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static ThreadLocal<SimpleDateFormat> dateMonthThreadLocal = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM"));
    private static ThreadLocal<SimpleDateFormat> dateDayWithoutLineThreadLocal = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    public static Date parseDateTime(String dateStr) throws ParseException {
        return dateTimeThreadLocal.get().parse(dateStr);
    }

    public static String formatDateTime(Date date) {
        return dateTimeThreadLocal.get().format(date);
    }

    public static Date parseDateMonth(String dateStr) throws ParseException {
        return dateMonthThreadLocal.get().parse(dateStr);
    }

    public static String formatDateMonth(Date date) {
        return dateMonthThreadLocal.get().format(date);
    }

    public static Date parseDayWithoutLine(String dateStr) throws ParseException {
        return dateDayWithoutLineThreadLocal.get().parse(dateStr);
    }

    public static String formatDateDayWithoutLine(Date date) {
        return dateDayWithoutLineThreadLocal.get().format(date);
    }
}
