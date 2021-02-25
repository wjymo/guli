package com.zzn.nettytest.netty;

import com.zzn.nettytest.bean.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class ClientTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        String s = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("收到服务器返回消息："+s);
    }
}
