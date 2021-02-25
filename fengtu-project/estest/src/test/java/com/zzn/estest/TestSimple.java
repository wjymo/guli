package com.zzn.estest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestSimple {
    public static final FastDateFormat ISO_DATE_FORMAT  = FastDateFormat.getInstance("yyyy-MM-dd");
    @Test
    public void testPOIStr(){
        String poi="Point(120.10160053296106 33.33777229773991)";
        String substring = poi.substring(poi.indexOf("(")+1, poi.indexOf(")"));
        String[] s = substring.split(" ");
        System.out.println(s);
    }

    @Test
    public void getWeek(){
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        int i = calendar.get(Calendar.WEEK_OF_MONTH);
        System.out.println("当前周为"+i);
    }

    @Test
    public void getBeforeDays(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -37);
        Date date = calendar.getTime();
        String format = ISO_DATE_FORMAT.format(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        int i = calendar.get(Calendar.WEEK_OF_MONTH);
        System.out.println("当前周为"+format+"-"+i);

//        String format = ISO_DATE_FORMAT.format(date);
//        System.out.println(format);
    }


    @Test
    public void testCompareTo(){
        int i = "2021-01-04".compareTo("2021-01-03");
        System.out.println(i);
    }


    @Test
    public void testXxlSuccTime() throws IOException {
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        String s = FileUtils.readFileToString(new File("d:\\user\\80005542\\桌面\\xxl-成功时间间隔.txt"),
                "utf-8");
        String[] lines = s.split("\r\n");
        for (String line : lines) {
            String[] items = line.split("@@");
            String startTimeStr = items[0];
            String endTimeStr = items[3];
            try {
                Date startTime = fastDateFormat.parse(startTimeStr);
                Date endTime = fastDateFormat.parse(endTimeStr);
                long minute = (endTime.getTime() - startTime.getTime()) / 1000 / 60;
                System.out.println(minute);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
