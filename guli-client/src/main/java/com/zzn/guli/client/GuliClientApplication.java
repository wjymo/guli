package com.zzn.guli.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages = {"com.zzn.guli"})
@EnableFeignClients(basePackages="com.zzn.guli.client.feign")
@EnableDiscoveryClient
public class GuliClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GuliClientApplication.class, args);
        System.out.println(1);

    }

}
