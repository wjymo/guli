package com.zzn.guli.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuliProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliProductApplication.class, args);
    }

}
