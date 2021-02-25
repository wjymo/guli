package com.zzn.kafkatest.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaProducer<Object,String> kafkaProducer(){
        Properties props = new Properties();
//        props.put("bootstrap.servers", "119.3.167.123:9191");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");//所有follower都响应了才认为消息提交成功，即"committed"
        props.put("retries", "2");//连接失败重试次数
        props.put("batch.size", "16384");//每个分区缓冲区大小，当数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        props.put("linger.ms", "1");//producer将会等待给定的延迟时间以允许其他消息记录一起发送，默认为0
        //props.put("buffer.memory", "33554432");//producer缓冲区大小
        //props.put("max.block.ms", "60000");//当缓冲区满了，发送消息时最大等待时间
        KafkaProducer<Object,String> producer = new KafkaProducer<>(props);
        return producer;
    }

    @Bean
    public KafkaConsumer<Object, String> kafkaConsumer(){
        Properties props = new Properties();
//        props.put("bootstrap.servers", "119.3.167.123:9191");
        props.put("bootstrap.servers", "localhost:9092");
//        props.put(CommonClientConfigs.MAX_POLL_INTERVAL_MS_CONFIG, "");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("group.id", "zzn1");
        props.setProperty("enable.auto.commit", "false");
//        props.setProperty("auto.commit.interval.ms", "1000");
        KafkaConsumer<Object, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
}
