package com.zzn.guli.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/test")
public class TestController {
    @GetMapping()
    public String test(){
        return "success";
    }
}
