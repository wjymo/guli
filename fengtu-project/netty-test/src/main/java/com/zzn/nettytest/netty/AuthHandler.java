package com.zzn.nettytest.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        try {
            Byte i = byteBuf.readByte();
            if(i!=null&&i==100){
                ctx.fireChannelRead(byteBuf);
            }else {
                log.warn("客户端：{} 认证失败！",ctx.channel().id());
                ctx.close();
            }
        } finally {
            ctx.pipeline().remove(this);
        }

    }

}