package com.zzn.nettytest.controller;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.zzn.nettytest.constant.NameSpaceEnum;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collection;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private SocketIOServer socketIOServer;
    @GetMapping("/test")
    public String test(){
        return "success";
    }

    @GetMapping("/send")
    public String send(@RequestParam("namespace")String namespace,@RequestParam("msg")String msg){
        Collection<SocketIONamespace> allNamespaces = socketIOServer.getAllNamespaces();
        namespace=namespace==null?"":"/"+namespace;
        for (SocketIONamespace socketIONamespace : allNamespaces) {
            String name = socketIONamespace.getName();
            if(StringUtils.equals(name,namespace)){
                socketIONamespace.getBroadcastOperations().sendEvent("messageevent",msg);
                return "发送成功";
            }
        }
        return "没有匹配命名空间";
    }

    @GetMapping("/sendUdp")
    public  String sendUdp(@RequestParam(value = "msg",required = false)String msg,
                           @RequestParam(value = "namespace",required = false)String namespace) throws IOException {
        DatagramChannel datagramChannel=DatagramChannel.open();
        if(StringUtils.isEmpty(msg)){
            msg="{\"can\":"+ RandomUtils.nextInt(0,200) +",\"flow\":"+
                    RandomUtils.nextInt(0,200)+",\"fire\":"+RandomUtils.nextInt(0,200)+"}";
        }
        if(StringUtils.isEmpty(namespace)){
            namespace="/main";
        }else {
            namespace="/"+namespace;
        }
        Integer code = NameSpaceEnum.getCodeByNamespace(namespace);
        byte[] bytes = msg.getBytes();
        int contentLen = bytes.length;
        ByteBuffer buf=ByteBuffer.allocate(4+contentLen+4);
        buf.clear();
        buf.putInt(contentLen);
        buf.put(bytes);
        buf.putInt(code);
        buf.flip();
        /*发送UDP数据包*/
        int bytesSent=datagramChannel.send(buf, new InetSocketAddress("127.0.0.1",10002));
        System.out.println(bytesSent);
        return "发送udp成功";
    }

    @Value("${coupon.user.name}")//从application.properties中获取//不要写user.name，他是环境里的变量
    private String name;
    @Value("${coupon.user.age}")
    private Integer age;

    @RequestMapping("/test")
    public String test2(){
        return name+":"+age;
    }
}
