package com.zzn.guli.sort.easy;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;

public class FindK {

    public static void main(String[] args) {
        ByteBuffer buffer=ByteBuffer.allocate(1024);
//        buffer.compact();

        buffer.put(new byte[512]);

        System.out.println("hasRemaining "+buffer.hasRemaining());

        buffer.put(new byte[512]);

        System.out.println("hasRemaining "+buffer.hasRemaining());
    }
}
