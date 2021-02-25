package com.zzn.nettytest.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.utils.SnowFlakeUtil;
import com.zzn.nettytest.utils.ThreadLocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.MapRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaConsumerRunner implements CommandLineRunner {
    @Autowired
    private SocketIOServer socketIOServer;
    @Autowired
    private BBossESStarter bbossESStarter;
    @Override
    public void run(String... args) throws Exception {
        new Thread(()->{
            KafkaConsumer<String, String> consumer=null;
            try {
                Properties props = new Properties();
                props.put("bootstrap.servers", "localhost:9092");
                props.put("auto.offset.reset", "earliest");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.setProperty("group.id", "zzn1");
                props.setProperty("enable.auto.commit", "false");
//        props.setProperty("auto.commit.interval.ms", "1000");
                consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Arrays.asList("netty"));
                while (true) {
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
                                    log.error("topic:{},分区：{}，异步提交offset {} 失败，异常信息：{}",
                                            topicPartition.topic(),topicPartition.partition(),offset.offset(),e.getMessage(),e);
                                });
                                log.error("异步提交offset失败：异常：{}，堆栈：",e.getMessage(),e);
                            }else {
                                log.info("消费进度提交成功,offset : {}",offsets);
                            }
                        });
                    }
                }
            }finally {
                if(consumer!=null){
                    consumer.close();
                }
            }
        },"kafka-consumer-thread").start();
    }

    private void processData(ConsumerRecord<String, String> record) {
        System.out.println("开始处理消息，消息内容："+record);
        String value = record.value();
        MessageInfo messageInfo = JSON.parseObject(value, MessageInfo.class);
        String content = messageInfo.getContent();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(content);
        } catch (Exception e) {
            log.error("content解析异常，{}",e.getMessage(),e);
            return;
        }
        Integer can = jsonObject.getInteger("can");
        Integer flow = jsonObject.getInteger("flow");
        Integer fire = jsonObject.getInteger("fire");
        String namespace = messageInfo.getNamespace();

        messageInfo.setCan(can).setFlow(flow).setFire(fire).setContent(null);
        messageInfo.setCreateDate(ThreadLocalDateTimeUtil.formatDateTime(new Date()));
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        String response = clientUtil.addDocument("netty_message",//indice name
                "_doc",//idnex type
                messageInfo,messageInfo.getId(),"refresh=false");
        log.info("新增至es，response:{}",response);

        Map<String,Object> map=new HashMap<>();
        map.putIfAbsent("namespace",namespace);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        map.putIfAbsent("createDate",ThreadLocalDateTimeUtil.formatDateTime(date));
        clientUtil= ElasticSearchHelper.getConfigRestClientUtil("esmappers/nettyMessage.xml");
        MapRestResponse restResponse = clientUtil.search("netty_message/_search",
                "aggIndexByNamespace",map);
        List aggAttribute = restResponse.getAggAttribute("groupDate", "buckets", List.class);
        List<Map<String,Object>> mapList=new ArrayList<>();
        for (Object o : aggAttribute) {
            Map result=new HashMap();
            Map mapItem = (Map) o;
            Map avg_can = (Map)mapItem.get("avg_can");
            Object avg_can_value = avg_can.get("value");
            Map avg_flow = (Map)mapItem.get("avg_flow");
            Object avg_flow_value = avg_flow.get("value");
            Map avg_fire = (Map)mapItem.get("avg_fire");
            Object avg_fire_value = avg_fire.get("value");
            result.put("avg_can",avg_can_value);
            result.put("avg_flow",avg_flow_value);
            result.put("avg_fire",avg_fire_value);
            mapList.add(result);
        }
        String s = JSON.toJSONString(mapList);
//((List)restResponse.getAggAttribute("groupDate", "buckets")).get(0)

        Collection<SocketIONamespace> allNamespaces = socketIOServer.getAllNamespaces();
        for (SocketIONamespace socketIONamespace : allNamespaces) {
            String name = socketIONamespace.getName();
            if(StringUtils.equals(name,namespace)){
                socketIONamespace.getBroadcastOperations().sendEvent("messageevent",s);
            }
        }
    }

    private void sleep(long milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
