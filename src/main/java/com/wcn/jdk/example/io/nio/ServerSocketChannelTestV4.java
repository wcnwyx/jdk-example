package com.wcn.jdk.example.io.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 为了解决V3的问题，该类引入一个队列来传递accept到的SocketChannel。
 *
 * 两个selector（一个负责accept，一个负责read），两个线程（一个负责accept，一个负责read）
 * 和v1 v2相比，selector角色分开了，accept和read的操作也由独立的线程来处理，不会互相影响。
 * 但是，正常业务常经中，read的操作是频繁的，一个连接accept之后，会多次发送请求，
 * 一个线程和一个selector处理所有连接的read就显得力不从心了。
 */
public class ServerSocketChannelTestV4 extends ServerSocketChannelTestV1{
    //加入一个队列，用于保存threadAccept线程accept到的SocketChannel，
    //然后threadRead线程自己进行channel.register操作
    private static ConcurrentLinkedQueue<SocketChannel> queue = new ConcurrentLinkedQueue<>();
    public static void main(String[] args) throws Exception{
        ServerSocketChannelTestV4 test = new ServerSocketChannelTestV4();
        test.start();
    }

    private void start() throws Exception{
        Selector selectorAccept = Selector.open();//单独用于注册accept事件
        Selector selectorRead = Selector.open();//单独用于注册read事件

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置未非阻塞模式
        ssc.register(selectorAccept, SelectionKey.OP_ACCEPT);//注册accept事件
        ssc.bind(new InetSocketAddress(8080), 1);//backlog为操作系统可以为进程保留多少个未accept的连接

        //accept线程
        Thread threadAccept = new Thread(new AcceptRunnable(selectorAccept, selectorRead));
        threadAccept.start();

        //read线程
        Thread threadRead = new Thread(new ReadRunnable(selectorRead));
        threadRead.start();

    }

    static class AcceptRunnable extends ServerSocketChannelTestV1 implements Runnable{
        private Selector selectorAccept;
        private Selector selectorRead;

        public AcceptRunnable(Selector selectorAccept, Selector selectorRead) {
            this.selectorAccept = selectorAccept;
            this.selectorRead = selectorRead;
        }

        @Override
        public void run() {
            while(true){
                try {
                    int selectNum = selectorAccept.select();
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

                            //此处的selectorRead传入null，为了不再此线程进行register操作
                            SocketChannel socketChannel = super.accept(null, key);
                            //将accept到的SocketChannel放入到队列
                            queue.add(socketChannel);
                            //唤醒selectorRead，让其处理队列里的Channel，进行注册
                            selectorRead.wakeup();
                            System.out.println("wakeup read selector.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ReadRunnable extends ServerSocketChannelTestV1 implements Runnable{
        private Selector selectorRead;

        public ReadRunnable(Selector selectorRead) {
            this.selectorRead = selectorRead;
        }

        @Override
        public void run() {
            while(true){
                try {
                    int selectNumRead = selectorRead.select();
                    System.out.println("selectNumRead:"+selectNumRead);
                    if(selectNumRead>0){
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

                    //处理队列里的SocketChannel
                    SocketChannel socketChannel = null;
                    while((socketChannel = queue.poll())!=null){
                        socketChannel.register(selectorRead, SelectionKey.OP_READ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
