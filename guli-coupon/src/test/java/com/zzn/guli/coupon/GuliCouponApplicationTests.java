package com.zzn.guli.coupon;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
class GuliCouponApplicationTests {

    @Test
    void contextLoads() throws IOException {
        File file = new File("C:\\");
        File[] files = file.listFiles();
        for (File file1 : files) {
            System.out.println(file.getName());
        }
//        String s = FileUtils.readFileToString(new File(""), "utf-8");


    }


}
