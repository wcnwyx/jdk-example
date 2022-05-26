package com.wcn.jdk.example.io.file;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.StandardOpenOption;

public class FileChannelTest {
    String filePath = "/Users/wucheng/Documents/temp";

    @Test
    public void testOpen() throws Exception {
        //通过FileInputStream获得FileChannel
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        FileChannel fileChannel = fileInputStream.getChannel();
        int readNum = fileChannel.read(byteBuffer);
        try{
            fileChannel.write(byteBuffer);
        }catch (NonWritableChannelException e){
            System.out.println("FileInputStream 获取到的FileChannel没有写入权限。");
        }
        fileChannel.close();

        System.out.println("-------------------------------------");

        //通过FileOutputStream获取FileChannel
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileChannel = fileOutputStream.getChannel();
        int writeNum = fileChannel.write(byteBuffer);
        try{
            fileChannel.read(byteBuffer);
        }catch (NonReadableChannelException e){
            System.out.println("FileOutputStream 获取到的FileChannel没有读取权限");
        }
        fileChannel.close();

        System.out.println("---------------------------------------");

        //RandomAccessFile获取的FileChannel权限和其本身的权限一致
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
        fileChannel = randomAccessFile.getChannel();
        fileChannel.write(byteBuffer);
        fileChannel.read(byteBuffer);
        fileChannel.close();

        System.out.println("---------------------------------------");

        fileChannel = FileChannel.open(new File(filePath).toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
        fileChannel.write(byteBuffer);
        fileChannel.read(byteBuffer);
        fileChannel.close();
    }
}
