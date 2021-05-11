package com.zzn.nettytest.netty;

import com.alibaba.fastjson.JSON;
import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.bean.MsgLog;
import com.zzn.nettytest.constant.MsgStatus;
import com.zzn.nettytest.service.MsgLogService;
import com.zzn.nettytest.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Date;

@Slf4j
public class ClientUdpHandler extends SimpleChannelInboundHandler<MessageInfo> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageInfo messageInfo) throws Exception {
        MsgLogService msgLogService = SpringContextUtil.getBean(MsgLogService.class);
        msgLogService.sendMsg(messageInfo);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("通道：{} 注册上来",ctx.channel().id());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道：{} 激活",ctx.channel().id());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("通道：{} handler添加，当前对象：{}",ctx.channel().id(),this);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("通道：{} handler移除，当前对象：{}",ctx.channel().id(),this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        log.error("发送异常，关闭通道，异常：{}",cause.getMessage(),cause);
        ctx.close();
    }
}
