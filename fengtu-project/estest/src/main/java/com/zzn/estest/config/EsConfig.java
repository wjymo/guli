package com.zzn.estest.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        HttpHost httpHost=new HttpHost("127.0.0.1",9200,"http");
//        HttpHost httpHost=new HttpHost("114.116.228.34",9201,"http");
//        HttpHost httpHost=new HttpHost("192.168.3.159",9200,"http");
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost[]{httpHost}));
        return restHighLevelClient;
    }
}
