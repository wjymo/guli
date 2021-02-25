package com.zzn.estest.controller;

import com.alibaba.fastjson.JSONObject;
import com.zzn.estest.canal.CanalScheduled;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.entity.DeviceWarn;
import com.zzn.estest.query.RiskDeDeviceWarnQuery;
import com.zzn.estest.service.DeviceWarnService;
import com.zzn.estest.util.DropEsDataScheduled;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/deviceWarn")
public class DeviceWarnController {
    @Autowired
    private DeviceWarnService deviceWarnService;

    @PostMapping("/insert")
    public String addDeviceWran(@RequestBody DeviceWarn deviceWarn){
        deviceWarn.setAlarmTime(new Date());
        deviceWarn.setAlarmType("5");
        deviceWarn.setAlarmSubType(RandomUtils.nextBoolean()?"11":"6");
        deviceWarn.setCommAlarmType("adas");
        deviceWarn.setCommSubtype("2");
        deviceWarn.setAlarmLevel(1);
        deviceWarn.setFileType("1");
        deviceWarn.setAlarmLocation("Point(114.05793999999999 22.598979999999997)");
        deviceWarn.setLat(114.05793999999999);
        deviceWarn.setLng(22.598979999999997);
        deviceWarn.setSpeed("12.2");
        deviceWarn.setFromId("86945504918"+RandomUtils.nextInt(1000,9999)+"-5-1583391"+RandomUtils.nextInt(100000,999999));

        deviceWarn.setOrg(RandomUtils.nextBoolean()?3474l:3365l);
        deviceWarn.setTenantId("yzfkcs01");
        deviceWarn.setTaskStatus(2);
        deviceWarn.setAuditResult(0);
        Integer integer = deviceWarnService.addDeviceWran(deviceWarn);
        if(Objects.equals(integer,1)){
            return "sucess";
        }
        return "error";
    }



    @PutMapping()
    public String updateDeviceWran(@RequestParam("id") Long id){
        String paiCode="鄂A"+ RandomUtils.nextInt(10000,99999);
        Integer integer = deviceWarnService.updateDeviceWran(id,paiCode);
        if(Objects.equals(integer,1)){
            return "sucess";
        }
        return "error";
    }

    @DeleteMapping()
    public String delDeviceWran(@RequestParam(value = "id",required = false) Long id){
        id =id==null? RandomUtils.nextLong(456516l, 497104l):id;
        Integer integer = deviceWarnService.delDeviceWran(id);
        if(Objects.equals(integer,1)){
            return "sucess";
        }
        return "error";
    }


    @PostMapping("/query")
    public Map<String,Object> getByQuery(@RequestBody /*@Valid*/ RiskDeDeviceWarnQuery query) throws IOException, ExecutionException, InterruptedException {


        return deviceWarnService.getByQuery(query);
    }

    @PostMapping("/getByBboss")
    public Map<String,Object> getByBboss(@RequestBody RiskDeDeviceWarnQuery query,@RequestParam("indexName")String indexName) throws IOException {

        return deviceWarnService.getInEsConstant(query,indexName);
    }


}
