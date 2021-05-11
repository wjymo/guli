package com.zzn.nettytest.runner;

import com.zzn.nettytest.netty.ClientUdpHandler;
import com.zzn.nettytest.netty.DecoderUdp;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyUdpRunner implements CommandLineRunner {
    @Value("${zzn.netty.port}")
    private Integer port;

    @Override
    public void run(String... args) throws Exception {
        new Thread(()->{
            NioEventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 2048)
                    .option(ChannelOption.SO_BACKLOG, 100)//最大工作线程
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
                            // pipeline.addLast("encoder", new MessageEncoder());//编码器，目前暂时用不到
                            // pipeline.addLast("stdecode", new DelimiterBasedFrameDecoder(1024,
                            // Unpooled.copiedBuffer("#".getBytes())));// 当数据量过大可以配置此项进行拆包粘包
                            pipeline.addLast("decoder", new DecoderUdp());//解码器
                            pipeline.addLast("handler", new ClientUdpHandler());//业务处理拦截器
//                        pipeline.addLast("logging", new LoggingHandler());//todo netty日誌配置，生產環境刪除
                        }
                    });
            try {
                ChannelFuture channelFuture = b.bind(port).sync();
                if(channelFuture.isSuccess()){
                    log.info("Netty UDP 服务已启动");
                }
                ChannelFuture sync = channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        },"netty-udp线程").start();


    }
}
