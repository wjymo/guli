package com.zzn.nettytest.utils;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class ThreadLocalKafkaConsumerUtil {

    private static ThreadLocal<KafkaConsumer<String, String>> kafkaConsumerThreadLocal = ThreadLocal
            .withInitial(() -> {
                Properties props = new Properties();
//              props.put("bootstrap.servers", "119.3.167.123:9191");
                props.put("bootstrap.servers", "localhost:9092");
//               props.put(CommonClientConfigs.MAX_POLL_INTERVAL_MS_CONFIG, "");
                props.put("auto.offset.reset", "earliest");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.setProperty("group.id", "zzn1");
                props.setProperty("enable.auto.commit", "false");
//              props.setProperty("auto.commit.interval.ms", "1000");
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
                return consumer;
            });

    public static KafkaConsumer<String, String> getKafkaConsumer(){
        return kafkaConsumerThreadLocal.get();
    }
}
