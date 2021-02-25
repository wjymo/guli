package com.zzn.estest.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
public class CanalClient {
    public final static CanalConnector CONNECTOR=null;
//    static {
//        CONNECTOR = CanalConnectors.newSingleConnector(
//                new InetSocketAddress("127.0.0.1",11111)
//                , "example", "", "");
////        CONNECTOR = CanalConnectors.newSingleConnector(
////                new InetSocketAddress("119.3.167.123",9191)
////                , "example", "", "");
////        CONNECTOR = CanalConnectors.newClusterConnector(
////                "127.0.0.1:2181", "example", "canal", "canal");
//        CONNECTOR.connect();
//        CONNECTOR.subscribe();
//        CONNECTOR.rollback();
//    }



    @PreDestroy
    public void destroy(){
        if(CONNECTOR !=null){
            CONNECTOR.disconnect();
        }
    }

}
