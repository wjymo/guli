package com.zzn.nettyclient.udp;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.RandomUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class NettyUdpClient {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new UdpClientHandler());
            //获得channel对象
//            Channel channel = bootstrap.connect("localhost", 10002).sync().channel();
            Channel channel=bootstrap.bind(0).sync().channel();
            //通过键盘输入给服务端发消息，并且死循环的监听服务端的消息
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                channel.writeAndFlush(br.readLine() + "\r\n");
                ByteBuffer byteBuffer = mockData(br.readLine());
                channel.writeAndFlush(new DatagramPacket(, new InetSocketAddress(
                        "255.255.255.255",10002)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private static final String[] names=new String[]{
            "骁儿","胡尧","庚庚","王佩","方牙","程达","胶皮王勇"
    };
    public static ByteBuffer mockData(String input){
        String content=names[RandomUtils.nextInt(0,names.length)]+"爱"+names[RandomUtils.nextInt(0,names.length)]
                +":"+input;
        Integer code = RandomUtils.nextInt(0,10);
        int contentLen = content.getBytes().length;

//        ByteBuf byteBuf=

        ByteBuffer buf=ByteBuffer.allocate(4+contentLen+4);
        buf.clear();
        buf.putInt(contentLen);
        buf.put(content.getBytes());
        buf.putInt(code);
        buf.flip();

        return buf;
    }
}
