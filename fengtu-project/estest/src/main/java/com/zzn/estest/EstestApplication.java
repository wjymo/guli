package com.zzn.estest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = "com.zzn.estest.dao")
@SpringBootApplication
@EnableScheduling
public class EstestApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstestApplication.class, args);
    }

}
