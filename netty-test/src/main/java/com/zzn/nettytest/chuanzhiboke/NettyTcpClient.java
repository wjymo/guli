package com.zzn.nettytest.chuanzhiboke;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyTcpClient {
    private static final AtomicInteger COUNT=new AtomicInteger(1);
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group) // 1
                    .channel(NioSocketChannel.class) // 2
                    .handler(new ChannelInitializer<NioSocketChannel>() { // 3
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 6
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    try {
                                        int i = buffer.refCnt();
                                        int capacity = buffer.capacity();
                                        int maxCapacity = buffer.maxCapacity();
                                        buffer.writeCharSequence("胡尧0牛逼", CharsetUtil.UTF_8);
                                        ctx.writeAndFlush(buffer);
                                    } finally {
    //                                    buffer.release();
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if(msg instanceof  ByteBuf){
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        System.out.println("接到服务端的消息："+byteBuf.toString( CharsetUtil.UTF_8));
                                    }else {
                                        System.out.println("没有ByteBuf数据传来");
                                    }
                                    TimeUnit.MILLISECONDS.sleep(1000);
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    try {
                                        buffer.writeCharSequence("胡尧"+COUNT.getAndIncrement()+"牛逼", CharsetUtil.UTF_8);
                                        ctx.writeAndFlush(buffer);
                                    } finally {
    //                                    buffer.release();
                                    }
                                }
                            }); // 8
    //                        ch.pipeline().addLast(new StringEncoder()); // 8
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9090) // 4
                    .sync();// 5
            channelFuture.channel().closeFuture().sync();
//        channelFuture.channel() // 6
//                .writeAndFlush(new Date() + ": hello world!"); // 7
        } finally {
            group.shutdownGracefully();
        }
    }
}
