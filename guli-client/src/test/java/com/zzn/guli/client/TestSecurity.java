package com.zzn.guli.client;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestSecurity {
    @Test
    public void testAntMatch(){
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        boolean match = antPathMatcher.match("/test/**", "/test/client");
        match = antPathMatcher.match("/test/**", "/test1/client");
        System.out.println(1);
    }

    @Test
    public void testDate(){
        Date d= new Date(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(d);
        System.out.println(1);
    }
}
