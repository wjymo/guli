package com.zzn.nettytest.schedule;

import com.zzn.nettytest.bean.MsgLog;
import com.zzn.nettytest.constant.MsgStatus;
import com.zzn.nettytest.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class MsgRetryScheduled {

    @Autowired
    private MsgLogService msgLogService;
    @Autowired
    private KafkaProducer<Object,String> kafkaProducer;

//    @Scheduled(fixedDelay = 3*1000)
    public void getFailMsgRetry(){
        List<MsgLog> failAndUnSuccessList = msgLogService.getFailAndUnSuccessList();
        if(CollectionUtils.isEmpty(failAndUnSuccessList)){
            return;
        }
        for (MsgLog msgLog : failAndUnSuccessList) {
            String content = msgLog.getContent();
            Long id = msgLog.getId();
            kafkaProducer.send(new ProducerRecord<>("netty", content), (metadata, exception) -> {
                System.out.println(metadata.toString());
                if(exception==null){
                    //消息投递成功
                    msgLogService.updateStatusAndAddRetries(id, MsgStatus.SUCCESS);
                }else {
                    //消息投递失败
                    log.error("消息投递失败，消息id：{},异常：{}",id, exception.getMessage(),exception);
                    msgLogService.updateStatusAndAddRetries(id,MsgStatus.FAIL);
                }
            });
        }
    }
}
