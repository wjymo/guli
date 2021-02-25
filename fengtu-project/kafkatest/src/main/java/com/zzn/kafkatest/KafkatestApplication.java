package com.zzn.kafkatest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = "com.zzn.kafkatest.dao")
@SpringBootApplication
@EnableScheduling
public class KafkatestApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkatestApplication.class, args);
    }

}
