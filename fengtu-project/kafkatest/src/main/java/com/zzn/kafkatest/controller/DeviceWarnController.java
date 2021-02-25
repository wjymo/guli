package com.zzn.kafkatest.controller;

import com.alibaba.fastjson.JSON;
import com.zzn.kafkatest.entity.DeviceWarn;
import com.zzn.kafkatest.query.RiskDeDeviceWarnQuery;
import com.zzn.kafkatest.service.DeviceWarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/deviceWarn")
public class DeviceWarnController {
    @Autowired
    private DeviceWarnService deviceWarnService;
    @Autowired
    private KafkaProducer<Object,String> kafkaProducer;
    @PostMapping("/insertAsync")
    public String addDeviceWranAsync(@RequestBody DeviceWarn deviceWarn){
        mockDeviceWarn(deviceWarn);
        ProducerRecord<Object, String> record = new ProducerRecord<>("deviceWran1", JSON.toJSONString(deviceWarn));
        kafkaProducer.send(record,(RecordMetadata metadata, Exception exception)->{
            log.info(metadata.toString());
            if(exception!=null){
                log.error(exception.getMessage(),exception);
            }
        });
        return "ok";
    }



    @PostMapping("/insert")
    public String addDeviceWran(@RequestBody DeviceWarn deviceWarn){
        mockDeviceWarn(deviceWarn);
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
    public Map<String,Object> getByQuery(@RequestBody RiskDeDeviceWarnQuery query) throws IOException, ExecutionException, InterruptedException {


        return deviceWarnService.getByQuery(query);
    }

    private DeviceWarn mockDeviceWarn(DeviceWarn deviceWarn){
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
        deviceWarn.setCreateDate(new Date());
        return deviceWarn;
    }

}
