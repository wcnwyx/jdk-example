package com.wcn.jdk.example.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketChannelTest {
    public static void main(String[] args) throws Exception{
        for(int i=0;i<3;i++){
            int finalI = i;
            Thread thread = new Thread(()->{
                SocketChannel socketChannel = null;
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    byteBuffer.put(("i am "+ finalI).getBytes(StandardCharsets.UTF_8));
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);
                    System.out.println("send success.");

                    byteBuffer = ByteBuffer.allocate(100);
                    int readNum = socketChannel.read(byteBuffer);
                    System.out.println("readNum: "+readNum+ " "+byteBuffer);
                    byteBuffer.flip();
                    byte[] bytes = new byte[readNum];
                    byteBuffer.get(bytes);
                    System.out.println("receive msg: "+ new String(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            thread.start();
        }

    }
}
