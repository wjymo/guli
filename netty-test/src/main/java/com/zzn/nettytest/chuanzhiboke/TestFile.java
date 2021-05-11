package com.zzn.nettytest.chuanzhiboke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;


public class TestFile {
    public static void main(String[] args) throws InterruptedException {
        LongAdder longAdder=new LongAdder();
        IntStream.rangeClosed(1,1000).forEach(i->{
            try {
                Files.lines(Paths.get("E:\\demo.txt")).forEach(line->longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println(longAdder.longValue());
//        Thread.sleep(200000);
    }
}
