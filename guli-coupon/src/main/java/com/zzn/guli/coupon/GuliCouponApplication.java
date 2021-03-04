package com.zzn.guli.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@SpringBootApplication()
@MapperScan(basePackages = "com.zzn.guli.coupon.dao")
@EnableDiscoveryClient
public class GuliCouponApplication {
    public static String jwtKey=null;
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GuliCouponApplication.class, args);
//        ConfigurableEnvironment environment = context.getEnvironment();
//        String clientId = environment.getProperty("zzn.auth.clientId");
//        String clientSecret = environment.getProperty("zzn.auth.clientSecret");
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic " + Base64Utils.encodeToString((clientId+":"+clientSecret).getBytes()));
//        ResponseEntity<Map> exchange = null;
//        try {
//            exchange = restTemplate.exchange("http://localhost:20000/oauth/token_key",
//                    HttpMethod.GET, new HttpEntity<>(null, headers), Map.class);
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        boolean needExit=false;
//        if(exchange!=null){
//            HttpStatus statusCode = exchange.getStatusCode();
//            if(Objects.equals(HttpStatus.OK.value(),statusCode.value())){
//                Map body = exchange.getBody();
//                if(body!=null&&!body.isEmpty()){
//                    String jwtKeyByAuth = (String) body.get("value");
//                    jwtKey=jwtKeyByAuth;
//                }else {
//                    needExit=true;
//                }
//            }else {
//                needExit=true;
//            }
//        }else {
//            needExit=true;
//        }
//        if(needExit){
//            System.exit(1);
//        }

        System.out.println(1);
    }

}
