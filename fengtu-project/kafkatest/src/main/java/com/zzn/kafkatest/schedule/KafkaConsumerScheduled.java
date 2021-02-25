package com.zzn.kafkatest.schedule;

import com.alibaba.fastjson.JSON;
import com.zzn.kafkatest.dao.DeviceWarnMapper;
import com.zzn.kafkatest.entity.DeviceWarn;
import com.zzn.kafkatest.service.DeviceWarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaConsumerScheduled {
    public static final FastDateFormat FAST_DATE_MONTH_FORMAT = FastDateFormat.getInstance("yyyy-MM");
    @Autowired
    private KafkaConsumer<Object, String> consumer;
    @Autowired
    private DeviceWarnService deviceWarnService;
    @Autowired
    private DeviceWarnMapper deviceWarnMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Scheduled(fixedDelay=13*1000)
    public void processData(){
        List<String> topics = Arrays.asList("deviceWran1");
        consumer.subscribe(topics);
        ConsumerRecords<Object, String> records = consumer.poll(Duration.ofMillis(100));
        if(records!=null&&records.isEmpty()){
            sleep(10);
            return;
        }
        System.out.println("#############################开始消费主题："
                +Arrays.toString(topics.toArray())+"##########################");
        Set<TopicPartition> partitions = records.partitions();
        for (TopicPartition partition : partitions) {
            List<ConsumerRecord<Object, String>> pRecords = records.records(partition);
            try {
                for (ConsumerRecord<Object, String> record : pRecords) {
                    deviceWarnService.processData(record);
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
            sleep(3000);
        }
    }


    private void sleep(long milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void processData(ConsumerRecord<Object, String> record) throws IOException {
//        int i = 10 / 0;
        System.out.printf("partition=%d,offset=%d, key=%s, value=%s%n",
                record.partition(), record.offset(), record.key(), record.value());
        String value = record.value();
        DeviceWarn deviceWarn = JSON.parseObject(value, DeviceWarn.class);
        //插入时有from_id作为唯一建，可保证消费端幂等性
        Integer integer = deviceWarnMapper.insertOne(deviceWarn);
        if(integer!=1){
            throw new RuntimeException("插入mysql失败!");
        }

        //插入mysql成功后，再连表查询告警信息，然后再录入es

        Date date = new Date();
        String format = FAST_DATE_MONTH_FORMAT.format(date);
        int week = getWeek(date);
        String indexName = "device_warn_" + format + "-" + week;
        IndexRequest indexRequest = new IndexRequest(indexName, "_doc");
        indexRequest.id(String.valueOf(deviceWarn.getId()));
        indexRequest.source(JSON.toJSONString(deviceWarn), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        RestStatus restStatus = response.status();
        int status = restStatus.getStatus();
        String name = restStatus.name();
        if (!Objects.equals(RestStatus.CREATED.getStatus(), status) && !Objects.equals(RestStatus.OK.getStatus(), status)) {
            RestStatus[] values = RestStatus.values();
            for (RestStatus item : values) {
                if (Objects.equals(status, item.getStatus())) {
                    System.out.println(item.name());
                }
            }
        }
        String s = response.toString();
        log.info("es新增的状态:{},{} ,详细信息：{}",name, status,s);
    }

    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);  //美国是以周日为每周的第一天 现把周一设成第一天
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }
}
