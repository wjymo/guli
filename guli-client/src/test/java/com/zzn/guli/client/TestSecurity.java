package com.zzn.guli.client;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

public class TestSecurity {
    @Test
    public void testAntMatch(){
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        boolean match = antPathMatcher.match("/test/**", "/test/client");
        match = antPathMatcher.match("/test/**", "/test1/client");
        System.out.println(1);
    }
}
