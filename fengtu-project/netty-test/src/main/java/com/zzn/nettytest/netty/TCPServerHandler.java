package com.zzn.nettytest.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TCPServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        String str=byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("接到客户端的消息："+str);
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