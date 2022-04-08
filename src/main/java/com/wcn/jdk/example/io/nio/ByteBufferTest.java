package com.wcn.jdk.example.io.nio;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.nio.ByteBuffer;

public class ByteBufferTest {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        System.out.println("init_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        //put添加数据，position向后移动
        byteBuffer.putChar('1');
        byteBuffer.putChar('2');
        System.out.println("put_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        //flip反转后，position未0，limit为position
        byteBuffer.flip();
        System.out.println("flip_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        //get之前一定要先flip，将position重置为0
        //get读取数据，从position位置开始读取，position向后移动
        System.out.println(byteBuffer.getChar());
        System.out.println("get_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        //compact将未读取过的数据从0开始放置，limit重置到capacity,position重置为remaining=2
        //此时数据为00320032000000000000，继续put后会将2-3位置的老数据数据覆盖掉
        byteBuffer.compact();
        System.out.println("compact_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        //读取过后再次put，必须先compact将position的位置放对了
        byteBuffer.put((byte)0x03);
        System.out.println("put_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        byteBuffer.flip();
        System.out.println("flip_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));

        System.out.println(byteBuffer.getChar());
        System.out.println("get_"+byteBuffer+" "+ HexBin.encode(byteBuffer.array()));
    }

    private static void print(ByteBuffer byteBuffer){
//        System.out.println("limit:"+byteBuffer.limit()+" capacity:"+byteBuffer.capacity()+" position:"+byteBuffer.position()+" offset:"+byteBuffer.arrayOffset());
        System.out.println(byteBuffer);
    }
}
