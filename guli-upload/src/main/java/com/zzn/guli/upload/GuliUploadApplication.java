package com.zzn.guli.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class/*,
        NacosConfigAutoConfiguration.class, NacosDiscoveryAutoConfiguration.class, NacosServiceDiscovery.class*/})
public class GuliUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliUploadApplication.class);
    }
}
