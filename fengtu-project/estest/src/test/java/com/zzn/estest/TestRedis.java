package com.zzn.estest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzn.estest.entity.DeviceWarn;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRedis {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testGet(){
//        Set<String> keys = redisTemplate.keys("*");
//        Map<Object, Object> base_alarm_type = redisTemplate.boundHashOps("base_alarm_type").entries();
//        Map<Object, Object> base_alarm_type = redisTemplate.boundHashOps("huaweicloud.dis.consumer").entries();
//        Map<Object, Object> base_alarm_type = redisTemplate.boundHashOps("huaweicloud.dis.consumer-dis-Detect").entries();
        Map<Object, Object> alarm_level_config_yzfkcs01 = redisTemplate.boundHashOps("alarm_level_config_yzfkcs01").entries();
        System.out.println(1);

    }

    @Test
    public void testSet(){
        redisTemplate.boundHashOps("huaweicloud.dis.consumer-dis-Detect")
                .put("shardId-0000000000","12602655");
        System.out.println(1);
    }

    @Test
    public void testJdbcTemplate(){

        DeviceWarn deviceWarn = jdbcTemplate.queryForObject("select  * from  de_device_warn where  id=?", new RowMapper<DeviceWarn>() {
            @Override
            public DeviceWarn mapRow(ResultSet resultSet, int i) throws SQLException {
                String imei = resultSet.getString("imei");
                String pai_code = resultSet.getString("pai_code");
                long org = resultSet.getLong("org");
                DeviceWarn deviceWarn = new DeviceWarn();
                deviceWarn.setPaiCode(pai_code);
                deviceWarn.setImei(imei);
                deviceWarn.setOrg(org);
                return deviceWarn;
            }
        }, 456516l);

        Map<String, Object> map = jdbcTemplate.queryForMap("select  * from  de_device_warn where  id=?", 456516l);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select  * from  de_device_warn");
        System.out.println(1);
    }

    @Test
    public void testJdbcTemplate2(){
        String t="11-1";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select  * from  dv_common_alarm_code_config");
        for (Map<String, Object> map : mapList) {
            Object obj = map.get("alarm_alg_config");
            JSONObject jsonObject = JSON.parseObject(obj.toString());
            JSONObject alarmAlgConfig = jsonObject.getJSONObject("alarmAlgConfig");
            alarmAlgConfig.forEach((k,v)->{
                JSONArray jsonArray = (JSONArray) v;
                for (Object o : jsonArray) {
                    String s = o.toString();
                    if(StringUtils.equals(s,t)){
                        String common_type_code = map.get("common_type_code").toString();
                        System.out.println(1);
                        break;
                    }
                }
                List<Object> collect = jsonArray.stream().collect(Collectors.toList());
                System.out.println(1);
            });

        }
    }

}
