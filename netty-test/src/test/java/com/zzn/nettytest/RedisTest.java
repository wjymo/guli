package com.zzn.nettytest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzn.nettytest.utils.SnowFlakeUtil;
import com.zzn.nettytest.utils.ThreadLocalDateTimeUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void testDate() throws JsonProcessingException {
        redisTemplate.opsForValue().set("wjy-redisTemplate", new User("zhuye", 36));
        stringRedisTemplate.opsForValue().set("wjy-stringRedisTemplate", objectMapper.writeValueAsString(new User("zhuye", 36)));
    }

    @AllArgsConstructor
    class User{

        private String name;
        private Integer age;
    }
}
