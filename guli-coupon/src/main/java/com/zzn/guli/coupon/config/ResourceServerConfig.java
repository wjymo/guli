package com.zzn.guli.coupon.config;

import com.alibaba.fastjson.JSON;
import com.zzn.guli.common.response.CommonResponse;
import com.zzn.guli.common.response.ResponseUtil;
import com.zzn.guli.common.response.ResultCode;
import com.zzn.guli.coupon.GuliCouponApplication;
import com.zzn.guli.coupon.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableResourceServer
public class ResourceServerConfig  /*extends ResourceServerConfigurerAdapter*/ {
//    @Value("${zzn.auth.clientId}")
//    private String clientId;
//
//    @Value("${zzn.auth.clientSecret}")
//    private String clientSecret;
//    @Override
////    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
////        resources.resourceId("coupon");
////    }
////    @Override
    public void configure(HttpSecurity http) throws Exception {
        /**
         * 所有请求，哪些要拦截，哪些要放过，在这里配置
         */
        http.authorizeRequests()
                .antMatchers("/coupon/coupon/**")
                .permitAll() //放过/haha不拦截
                .anyRequest().authenticated();//其余所有请求都拦截

    }
}
