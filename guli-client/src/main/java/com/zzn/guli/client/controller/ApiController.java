package com.zzn.guli.client.controller;

import com.alibaba.fastjson.JSON;
import com.zzn.guli.client.feign.Client2FeignApi;
import com.zzn.guli.client.feign.CouponFeignApi;
import com.zzn.guli.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/api")
public class ApiController {
    @Autowired
    private CouponFeignApi couponFeignApi;
    @Autowired
    private Client2FeignApi client2FeignApi;
    @GetMapping("/test1")
    public String test(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        R api1 = client2FeignApi.api1();
        String s="";
         s = JSON.toJSONString(api1);
        return "test1: "+s;
    }
}
