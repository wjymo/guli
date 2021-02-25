package com.zzn.nettyclient.log;

import com.zzn.nettyclient.exception.TestException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TestLog {
    private final static SimpleDateFormat SIMPLE_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        try {
            int a = 10;
            try {
    //            int i = 1/0;
                mockException();
            }
            catch (TestException e) {
                log.error(e.getMessage(), e);
                System.out.println("-------------------------");
                //将异常完整打印出来
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                String exception = baos.toString();
                log.error(exception);
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            System.out.println("-------------------------");
            //将异常完整打印出来
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            String exception = baos.toString();
            log.error(exception);
        }
    }

    public static void mockException() throws ParseException {
        try {
            String dateStr="111111111";
            Date date = SIMPLE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw e;
        }
    }
}
