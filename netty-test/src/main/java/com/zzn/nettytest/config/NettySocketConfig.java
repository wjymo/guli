package com.zzn.nettytest.config;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.constant.NameSpaceEnum;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@Slf4j
public class NettySocketConfig {
    /**
     * netty-socketio服务器
     */
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("192.168.159.1");
        config.setPort(10000);
        config.setAuthorizationListener(data -> {
            HttpHeaders httpHeaders = data.getHttpHeaders();
            String authorization = httpHeaders.get("Authorization");
            if (StringUtils.isNotEmpty(authorization)) {
                //todo 将当前登录用户和当前服务器地址绑定存入redis
                InetSocketAddress address = data.getAddress();
                String ip = address.getAddress().getHostAddress();
                String hostName = address.getHostName();
                return true;
            }
            return true;
        });
        SocketIOServer server = new SocketIOServer(config);
        NameSpaceEnum[] values = NameSpaceEnum.values();
        for (NameSpaceEnum nameSpaceEnum : values) {
            SocketIONamespace socketIONamespace = server.addNamespace(nameSpaceEnum.getNamespace());
            socketIONamespace.addEventListener("eventData", MessageInfo.class,
                    (socketIOClient, messageInfo, ackRequest) -> {
                        String clientId = socketIOClient.getHandshakeData().getSingleUrlParam("clientId");
                        boolean ackRequested = ackRequest.isAckRequested();
                        String name = socketIOClient.getNamespace().getName();
                        log.info("命名空间：{} 发来消息，clientId:{} ,content:{}",name,clientId,messageInfo.getContent());
                        log.info("pc:" + socketIOClient.getHandshakeData().getAddress().getHostName());
                        log.info("当前是否为ack：{}",ackRequested);
                        log.info("messageInfo:" + messageInfo.toString());
                    });
        }

        return server;
    }

}
