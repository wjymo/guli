package com.zzn.guli.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@MapperScan(basePackages = "com.zzn.guli.auth.dao")
@EnableDiscoveryClient
public class GuliAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliAuthApplication.class, args);
    }

}
