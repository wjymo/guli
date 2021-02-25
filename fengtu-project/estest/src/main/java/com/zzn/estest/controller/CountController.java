package com.zzn.estest.controller;

import com.zzn.estest.canal.CanalScheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/count")
public class CountController {
    @Autowired
    private CanalScheduled canalScheduled;
    @GetMapping("/es")
    public Long getEsInsertCount(){
        return canalScheduled.longAdder.longValue();
    }
}
