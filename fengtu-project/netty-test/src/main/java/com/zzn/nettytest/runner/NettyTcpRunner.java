package com.zzn.nettytest.runner;

import com.zzn.nettytest.netty.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class NettyTcpRunner implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {
        new Thread(()->{
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup workGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                    .option(ChannelOption.SO_RCVBUF, 1024 * 2048)
//                    .option(ChannelOption.SO_BACKLOG, 100)//最大工作线程
                    .childHandler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
                            // pipeline.addLast("encoder", new MessageEncoder());//编码器，目前暂时用不到
                            // pipeline.addLast("stdecode", new DelimiterBasedFrameDecoder(1024,
                            // Unpooled.copiedBuffer("#".getBytes())));// 当数据量过大可以配置此项进行拆包粘包
                            pipeline.addLast( new MessagePacketDecoder());//解码器
                            pipeline.addLast( new AuthHandler());//身份验证处理拦截器
                            pipeline.addLast( new TCPServerHandler());//业务处理拦截器
//                        pipeline.addLast("logging", new LoggingHandler());//todo netty日誌配置，生產環境刪除
                        }
                    });
            try {
                ChannelFuture channelFuture = b.bind(10003).sync();
                if(channelFuture.isSuccess()){
                    System.out.println("TCP服务启动 成功---------------");
                }
//                ChannelFuture sync = channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
            }
        },"netty-tcp线程").start();


    }
}
