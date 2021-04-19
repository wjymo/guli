package com.zzn.guli.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzn.guli.upload.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class RedisTest  {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
//    @Qualifier("myRedisTemplate")
    private RedisTemplate<String, User> userRedisTemplate;


    @Test
    public void testDate() throws JsonProcessingException {
//        redisTemplate.opsForValue().set("wjy-redisTemplate", new User("zhuye", 36));
//        stringRedisTemplate.opsForValue().set("wjy-stringRedisTemplate", objectMapper.writeValueAsString(new User("zhuye", 36)));
        userRedisTemplate.opsForValue().set("wjy-redisTemplate2", new User("zhuye", 36));
    }

    @Test
    public void testGet(){
//        log.info("redisTemplate get {}", redisTemplate.opsForValue().get("wjy-stringRedisTemplate"));
//        log.info("stringRedisTemplate get {}", stringRedisTemplate.opsForValue().get("wjy-redisTemplate"));
        log.info("userRedisTemplate get {}", userRedisTemplate.opsForValue().get("wjy-redisTemplate2"));
    }


}
