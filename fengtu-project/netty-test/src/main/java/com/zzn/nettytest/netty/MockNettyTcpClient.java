package com.zzn.nettytest.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MockNettyTcpClient {
    public static void main(String[] args) {
        // 首先，netty通过ServerBootstrap启动服务端
        Bootstrap bootstrap = new Bootstrap();

        //第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group );

        //第2步 绑定客户端通道
        bootstrap.channel(NioSocketChannel.class);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ClientTcpHandler());
            }
        });

        //连接服务器
        try {
            ChannelFuture future = bootstrap.connect("localhost", 10003).sync();
            while (true){
//                InputStreamReader is = new InputStreamReader(System.in); //new构造InputStreamReader对象
//                BufferedReader br = new BufferedReader(is); //拿构造的方法传到BufferedReader中
//                String content = br.readLine();
                Scanner sc = new Scanner(System.in);
                System.out.println("ScannerTest, Please Enter content:");
                String content = sc.nextLine();  //读取字符串型输入
                //发送数据给服务器
                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
                byteBuf.writeByte(100);
                byteBuf.writeCharSequence("胡尧牛逼"+content, CharsetUtil.UTF_8);
                future.channel().writeAndFlush(byteBuf);
                TimeUnit.MILLISECONDS.sleep(50);
            }
            //当通道关闭了，就继续往下走
//            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  finally {
            group.shutdownGracefully();
        }

    }
}
