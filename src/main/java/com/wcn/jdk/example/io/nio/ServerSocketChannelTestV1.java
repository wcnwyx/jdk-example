package com.wcn.jdk.example.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 一个selector、一个线程处理所有的accept和read操作
 * accept操作和read操作都要在一个线程中顺序执行，
 * accept和read都要和操作系统交互，是比较慢的操作，
 * 可能会导致：
 * 1. read操作过多过慢，accept处理不及时;
 * 2. accept操作过多过慢，read处理不及时
 */
public class ServerSocketChannelTestV1 {
    public static void main(String[] args) throws Exception{
        ServerSocketChannelTestV1 test = new ServerSocketChannelTestV1();
        test.start();;
    }

    private void start() throws Exception{
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置未非阻塞模式
        ssc.register(selector, SelectionKey.OP_ACCEPT);//注册accept事件
        ssc.bind(new InetSocketAddress(8080), 1);//backlog为操作系统可以为进程保留多少个未accept的连接

        while(true){
            int selectedNum = selector.select();//阻塞永久等待
            System.out.println("selectedNum:"+selectedNum);
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isValid()){
                    if(key.isAcceptable()){
                        accept(selector, key);
                    }else if(key.isReadable()){
                        read(key);
                    }
                }else{
                    System.out.println("key cancel.");
                }
            }
        }
    }

    protected SocketChannel accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("socket accept:"+socketChannel.socket().getInetAddress()+" port:"+socketChannel.socket().getPort());
        socketChannel.configureBlocking(false);//socketChannel设置为非阻塞
        if(selector!=null){
            socketChannel.register(selector, SelectionKey.OP_READ);//注册到register
        }
        return socketChannel;
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int readNum = socketChannel.read(byteBuffer);
        System.out.println(Thread.currentThread().getName()+" readBytesNum: "+readNum);
        if(readNum>0){
            //读取到了数据
            byte[] bytes = new byte[readNum];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            System.out.println(Thread.currentThread().getName()+" receive msg:"+new String(bytes));

            //读取到的数据原样返回给客户端
            byteBuffer.compact();
            byteBuffer.put("response_".getBytes(StandardCharsets.UTF_8));
            byteBuffer.put(bytes);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        }else if(readNum<0){
            //-1表示客户端关闭
            System.out.println(Thread.currentThread().getName()+" clint close: "+socketChannel.getRemoteAddress());
            socketChannel.close();
        }
    }
}
