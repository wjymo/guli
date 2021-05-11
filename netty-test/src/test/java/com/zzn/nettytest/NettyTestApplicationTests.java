package com.zzn.nettytest;

import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.utils.SnowFlakeUtil;
import com.zzn.nettytest.utils.ThreadLocalDateTimeUtil;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@SpringBootTest
@RunWith(SpringRunner.class)
class NettyTestApplicationTests {
    @Autowired
    private BBossESStarter bbossESStarter;
    @Test
    void contextLoads() {
    }

    @Test
    public void testSendUdp() throws IOException {
        DatagramChannel datagramChannel=DatagramChannel.open();

        String newData="hello,itbuluoge!"+System.currentTimeMillis();
        ByteBuffer buf=ByteBuffer.allocate(48);
        buf.clear();
        buf.put(newData.getBytes());
        buf.flip();
        /*发送UDP数据包*/
        int bytesSent=datagramChannel.send(buf, new InetSocketAddress("127.0.0.1",10002));
        System.out.println(bytesSent);
    }

    @Test
    public void testAddDocInEs(){
        MessageInfo messageInfo=new MessageInfo().setId(5l)
                .setCreateDate("2021-02-01 03:13:47");
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        String response = clientUtil.addDocument("netty_message",//indice name
                "_doc",//idnex type
                messageInfo,messageInfo.getId(),"refresh=false");
        System.out.println(response);
    }

}
