package com.zzn.nettytest;

import com.zzn.nettytest.utils.SnowFlakeUtil;
import com.zzn.nettytest.utils.ThreadLocalDateTimeUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class SimpleTest {
    @Test
    public void testDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Date date = calendar.getTime();
        String doNothingDateTime = ThreadLocalDateTimeUtil.formatDateTime(date);

        calendar.add(Calendar.MINUTE, -5);
        date = calendar.getTime();
        String failDateTime = ThreadLocalDateTimeUtil.formatDateTime(date);
        System.out.println(1);
    }

    @Test
    public void testStr(){
        String profile="prodTask1";
        String taskFlag="Task";
        if(StringUtils.endsWith(profile,taskFlag)){
            profile=profile.substring(0, profile.lastIndexOf(taskFlag));
        }
        System.out.println(profile);
    }

    @Test
    public void testSnowId(){
        CountDownLatch countDownLatch=new CountDownLatch(1000);
        Map<Long,String> map=new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    long id = SnowFlakeUtil.getFlowIdInstance().nextId();
//                    Long id = mockLong();
                    map.put(id, "1");
                }
                countDownLatch.countDown();
            });
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map.size());
    }
    private Long mockLong(){
        long l = RandomUtils.nextLong(0, 10000);
        return l;
    }
}
