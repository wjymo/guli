package com.zzn.guli.upload;

import org.junit.Test;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class NioTest {
    @Test
    public void testMmap(){
        File sourecFile = new File("D:\\temp\\05f25fe5-65d7-43f9-8038-3cbd8fe56233\\0-项目组件机构层级逻辑.txt");
        try (
                FileChannel fcin = new FileInputStream(sourecFile).getChannel();
                FileChannel fcout = new FileOutputStream("D:\\temp\\a.txt").getChannel();
        ) {
            MappedByteBuffer mbb = fcin.map(FileChannel.MapMode.READ_ONLY, 0, sourecFile.length());
            Charset cn = Charset.forName("utf-8");
            System.out.println(cn.decode(mbb));

            mbb.flip(); // 写出去之前要操作就绪
            fcout.write(mbb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
