package com.zzn.nettytest.chuanzhiboke;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class NettyTcpServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss,worker) // 1
                    .channel(NioServerSocketChannel.class) // 2
                    .childHandler(new ChannelInitializer<NioSocketChannel>() { // 3
                        protected void initChannel(NioSocketChannel ch) {
//                            ch.pipeline().addLast(new LoggingHandler()); // 5
                            ch.pipeline().addLast(new StringDecoder()); // 5
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() { // 6
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    System.out.println("接到客户端的消息："+msg);
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    int i = buffer.refCnt();
                                    try {
                                        buffer.writeCharSequence(msg, CharsetUtil.UTF_8);
                                        ctx.writeAndFlush(buffer);
                                    } finally {
                                        //                                    buffer.release();
                                    }
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(9090);// 4
            ChannelFuture sync = channelFuture.sync();
            sync.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
