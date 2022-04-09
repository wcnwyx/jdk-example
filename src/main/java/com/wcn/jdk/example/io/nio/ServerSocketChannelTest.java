package com.wcn.jdk.example.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerSocketChannelTest {
    public static void main(String[] args) throws Exception{
        Selector selectorAccept = Selector.open();//单独用于注册accept事件
        Selector selectorRead = Selector.open();//单独用于注册read事件

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置未非阻塞模式
        ssc.register(selectorAccept, SelectionKey.OP_ACCEPT);//注册accept事件
        ssc.bind(new InetSocketAddress(8080), 3);

        while(true){
            int selectNum = selectorAccept.select(1000);//带超时时间
            System.out.println("selectNumAccept:"+selectNum);
            if(selectNum>0){
                //有新的连接accept
                Set<SelectionKey> keys = selectorAccept.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("socket income:"+socketChannel.socket().getInetAddress()+" port:"+socketChannel.socket().getPort());
                    socketChannel.configureBlocking(false);//socketChannel设置为非阻塞
                    socketChannel.register(selectorRead, SelectionKey.OP_READ);
                }
            }

            selectNum = selectorRead.selectNow();//立马返回，不阻塞
            System.out.println("selectNumRead:"+selectNum);
            if(selectNum>0){
                //有新的数据发送过来，可以read了
                Set<SelectionKey> keys = selectorRead.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int readNum = socketChannel.read(byteBuffer);
                    System.out.println("readNum: "+readNum);
                    if(readNum>0){
                        //读取到了数据
                        byte[] bytes = new byte[readNum];
//                        System.out.println("after read: "+byteBuffer);
                        byteBuffer.flip();
//                        System.out.println("after flip: "+byteBuffer);
                        byteBuffer.get(bytes);
//                        System.out.println("after get: "+byteBuffer);
                        System.out.println("receive msg:"+new String(bytes));

                        //读取到的数据原样返回给客户端
                        byteBuffer.compact();
//                        System.out.println("after compact: "+byteBuffer);
                        byteBuffer.put("response_".getBytes(StandardCharsets.UTF_8));
                        byteBuffer.put(bytes);
//                        System.out.println("after put: "+byteBuffer);
                        System.out.println(byteBuffer);
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                    }else if(readNum<0){
                        //-1表示客户端关闭
                        System.out.println("clint close: "+socketChannel.getRemoteAddress());
                        socketChannel.close();
                    }
                }
            }
        }
    }
}
