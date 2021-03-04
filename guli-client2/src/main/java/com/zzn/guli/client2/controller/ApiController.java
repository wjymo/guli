package com.zzn.guli.client2.controller;

import com.zzn.guli.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client2/api")
public class ApiController {
    @GetMapping("/api1")
    public R api1(){
        System.out.println(1);
        return R.ok().put("name","胡尧");
    }
}
