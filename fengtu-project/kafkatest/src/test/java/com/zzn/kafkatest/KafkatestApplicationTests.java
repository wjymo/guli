package com.zzn.kafkatest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class KafkatestApplicationTests {
//    @Autowired
//    private KafkaProducer<String,String> kafkaProducer;

    @Test
    public void testAdmin() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "119.3.167.123:9191");
        AdminClient adminClient = AdminClient.create(properties);
        NewTopic newTopic = new NewTopic("second", 2, (short) 1);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        ListTopicsResult listTopicsResult = adminClient.listTopics(listTopicsOptions);
        listTopicsResult.listings().get().forEach(System.out::println);
        adminClient.close();
    }

    @Test
    public void testProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "119.3.167.123:9191");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");//所有follower都响应了才认为消息提交成功，即"committed"
        props.put("retries", "2");//连接失败重试次数
        props.put("batch.size", "16384");//每个分区缓冲区大小，当数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        //props.put("linger.ms", "1");//producer将会等待给定的延迟时间以允许其他消息记录一起发送，默认为0
        //props.put("buffer.memory", "33554432");//producer缓冲区大小
        //props.put("max.block.ms", "60000");//当缓冲区满了，发送消息时最大等待时间
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
        for (int i = 0; i < 10; i++) {
            //发送key和value
            kafkaProducer.send(new ProducerRecord<>("second", Integer.toString(i)
                    , "消息-" + i));
            //只发送value
            kafkaProducer.send(new ProducerRecord<>("second", "消息2-" + i));
        }
        kafkaProducer.close();
    }


    @Test
    public void testCosumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "119.3.167.123:9191");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("group.id", "zzn1");
        props.setProperty("enable.auto.commit", "false");
//        props.setProperty("auto.commit.interval.ms", "1000");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("deviceWran1"));
//        for (int i = 0; i < 5; i++) {
        while (true) {
            System.out.println("#######################################################");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            if(records!=null&&records.isEmpty()){
                sleep(20);
                continue;
            }
            Set<TopicPartition> partitions = records.partitions();
            for (TopicPartition partition : partitions) {
                List<ConsumerRecord<String, String>> pRecords = records.records(partition);
                try {
                    for (ConsumerRecord<String, String> record : pRecords) {
                        processData(record);
                    }
                } catch (Exception e) {
                    //消费此分区失败时，跳到下一次的循环的分区去消费，本次不提交offset，下次拉取消息再来消费
                    log.error("分区：{} 消费数据失败失败：异常信息：{}",partition.partition(),e.getMessage(),e);
                    continue;
                }
                Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
                long currentOffset = pRecords.get(pRecords.size() - 1).offset();
                System.out.println("当前消费完的进度为：" + currentOffset);
                OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(currentOffset + 1);
                map.put(partition, offsetAndMetadata);
//                try {
//                    consumer.commitSync(map);
//                } catch (Exception e) {
//                    log.error("同步提交offset失败：异常信息：{}",e.getMessage(),e);
//                }
                consumer.commitAsync(map,(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e)->{
                    if(e!=null){
                        offsets.forEach((TopicPartition topicPartition,OffsetAndMetadata offset)->{
                            String metadata = offset.metadata();
                            log.error("topic:{},分区：{}，异步提交offset {} 失败：异常信息：{}",
                                    topicPartition.topic(),topicPartition.partition(),offset.offset(),e.getMessage());
                        });
                        log.error("异步提交offset失败：异常堆栈：",e);
                    }
                });
                sleep(5000);
            }
        }
//        consumer.close();
    }

    private void sleep(long milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processData(ConsumerRecord<String, String> record) {
//        int i = 10 / 0;
        sleep(2000);
        System.out.printf("partition=%d,offset=%d, key=%s, value=%s%n",
                record.partition(), record.offset(), record.key(), record.value());
    }

    @After
    public void after() throws Exception {
//        kafkaProducer.close();
    }


}
