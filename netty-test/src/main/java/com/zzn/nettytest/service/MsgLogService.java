package com.zzn.nettytest.service;

import com.alibaba.fastjson.JSON;
import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.bean.MsgLog;
import com.zzn.nettytest.constant.MsgStatus;
import com.zzn.nettytest.dao.MsgLogMapper;
import com.zzn.nettytest.utils.SnowFlakeUtil;
import com.zzn.nettytest.utils.SpringContextUtil;
import com.zzn.nettytest.utils.ThreadLocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
@Service
public class MsgLogService {
    @Autowired
    private MsgLogMapper msgLogMapper;
    @Autowired
    private KafkaProducer<Object,String> kafkaProducer;

    @Transactional
    public void sendMsg(MessageInfo messageInfo){
        String content = messageInfo.getContent();
        String nameSpace = messageInfo.getNamespace();
        long nextId = SnowFlakeUtil.getFlowIdInstance().nextId();
        messageInfo.setId(nextId);
        String json = JSON.toJSONString(messageInfo);
        //获取消息后先在mysql中记录日志，然后投递kafka
        MsgLog msgLog=new MsgLog().setContent(json).setUpdateTime(new Date());
        insertOne(msgLog);
        Long id = msgLog.getId();
        KafkaProducer<Object,String> kafkaProducer = SpringContextUtil.getBean(KafkaProducer.class);
        kafkaProducer.send(new ProducerRecord<>("netty", json), (metadata, exception) -> {
            System.out.println(1);
            if(exception==null){
                //消息投递成功
                updateStatus(id, MsgStatus.SUCCESS);
            }else {
                //消息投递失败
                log.error("消息投递失败，消息id：{},异常：{}",id, exception.getMessage(),exception);
                updateStatus(id,MsgStatus.FAIL);
            }
        });
    }
    @Transactional
    public void insertOne(MsgLog msgLog){
        Integer integer = msgLogMapper.insertOne(msgLog);
        if(integer!=1){
            throw new RuntimeException("新增消息记录失败");
        }
    }

    @Transactional
    public void updateStatus(Long id, Integer status){
        Integer integer = msgLogMapper.updateStatus(id, status,new Date());
        if(integer!=1){
            throw new RuntimeException("修改消息记录失败");
        }
    }

    @Transactional
    public void updateStatusAndAddRetries(Long id, Integer status){
        Integer integer = msgLogMapper.updateStatusAndAddRetries(id, status,new Date());
        if(integer!=1){
            throw new RuntimeException("修改消息记录失败");
        }
    }

    public List<MsgLog> getFailAndUnSuccessList(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        Date date = calendar.getTime();
        String doNothingDateTime = ThreadLocalDateTimeUtil.formatDateTime(date);

        calendar.add(Calendar.MINUTE, -1);
        date = calendar.getTime();
        String failDateTime = ThreadLocalDateTimeUtil.formatDateTime(date);

        List<MsgLog> list = msgLogMapper.getFailAndUnSuccessList(doNothingDateTime, failDateTime);
        return list;
    }
}
