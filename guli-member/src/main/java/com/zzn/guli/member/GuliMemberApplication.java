package com.zzn.guli.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages="com.zzn.guli.member.feign")
@MapperScan(basePackages = "com.zzn.guli.member.dao")
public class GuliMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMemberApplication.class, args);
    }

}
