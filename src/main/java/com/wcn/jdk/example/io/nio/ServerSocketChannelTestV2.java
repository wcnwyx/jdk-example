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

/**
 * 两个selector（一个负责accept，一个负责read），一个线程
 * 和v1相比，虽然selector职责分开了，但是还是一个线程顺序处理，还是会有V1的问题。
 * v2又引入了新的问题，因为一个线程要处理两个selector，那么每个selector就不能使用不带超时时间的select()方法，
 * 因为如果acceptSelector.select() 阻塞时，没有新连接创建，那其它以创建的连接数据就没有办法被读取。
 */
public class ServerSocketChannelTestV2 extends ServerSocketChannelTestV1{
    public static void main(String[] args) throws Exception{
        ServerSocketChannelTestV2 test = new ServerSocketChannelTestV2();
        test.start();
    }

    private void start() throws Exception{
        Selector selectorAccept = Selector.open();//单独用于注册accept事件
        Selector selectorRead = Selector.open();//单独用于注册read事件

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置未非阻塞模式
        ssc.register(selectorAccept, SelectionKey.OP_ACCEPT);//注册accept事件
        ssc.bind(new InetSocketAddress(8080), 1);//backlog为操作系统可以为进程保留多少个未accept的连接

        while(true){
            int selectNum = selectorAccept.select(500);//带超时时间
            System.out.println("selectNumAccept:"+selectNum);
            if(selectNum>0){
                //有新的连接accept
                Set<SelectionKey> keys = selectorAccept.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    //因为是从selectorAccept中select出来的key，所以必定是accept事件，因为该selector上只注册了accept事件
                    iterator.remove();
                    assert key.isAcceptable();
                    super.accept(selectorRead, key);
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
                    //必定是read事件，因为是从selectorRead中select出来的
                    assert key.isReadable();
                    iterator.remove();
                    super.read(key);
                }
            }
        }
    }
}
