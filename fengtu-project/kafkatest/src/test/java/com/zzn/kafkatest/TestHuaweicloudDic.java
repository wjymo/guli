package com.zzn.kafkatest;


import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.DISClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestHuaweicloudDic {
    private DIS dic;
    @Before
    public void before(){
        // 创建DIS客户端实例
        dic= DISClientBuilder.standard()
                .withEndpoint("https://dis.cn-north-4.myhuaweicloud.com")
                .withAk("MPSF80YBVEL9LOTTNFDT")
                .withSk("QLzZs029DUcoxLigw0wTU5nY5m0tawO7XM1UJ68A")
                .withProjectId("0607ebf5548010982f77c001df885b9c")
                .withRegion("cn-north-4")
                .build();
    }

    @Test
    public void testConsumer(){

    }
}
