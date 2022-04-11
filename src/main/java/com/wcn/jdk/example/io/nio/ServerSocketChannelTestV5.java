package com.wcn.jdk.example.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 总体结构类似于Netty了。
 *
 * 两个selector角色：
 * 1：accept角色，只要一个就好，因为一个端口就启动用了一个ServerSocketChannel，也就能注册到一个Selector上去。
 * 2：read角色，可以多个，每次accept到的SocketChannel可以分开注册到多个read selector上去，提高处理能力。
 *
 * 多个线程：
 * 1：acceptSelector拥有一个独立的线程。
 * 2：每个readSelector都拥有一个独立的线程。
 */
public class ServerSocketChannelTestV5 extends ServerSocketChannelTestV1{
    public static void main(String[] args) throws Exception{
        ServerSocketChannelTestV5 test = new ServerSocketChannelTestV5();
        test.start(2);
    }

    private void start(int readThreadNum) throws Exception{
        Selector selectorAccept = Selector.open();//单独用于注册accept事件

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置未非阻塞模式
        ssc.register(selectorAccept, SelectionKey.OP_ACCEPT);//注册accept事件
        ssc.bind(new InetSocketAddress(8080), 1);//backlog为操作系统可以为进程保留多少个未accept的连接

        //read线程组
        ReadThreadGroup readThreadGroup = new ReadThreadGroup(2);
        readThreadGroup.start();

        //accept线程
        Thread threadAccept = new Thread(new AcceptRunnable(selectorAccept, readThreadGroup));
        threadAccept.start();
    }

    static class ReadThreadGroup{
        private ReadThread[] readThreadArray;
        private AtomicLong atomicLong = new AtomicLong(0);

        public ReadThreadGroup(int readThreadNum) throws IOException {
            readThreadArray = new ReadThread[readThreadNum];
            for(int i=0;i<readThreadArray.length;i++){
                readThreadArray[i] = new ReadThread();
            }
        }

        public void start(){
            for(ReadThread thread:readThreadArray){
                thread.start();
            }
        }

        public void register(SocketChannel socketChannel){
            next().register(socketChannel);
        }

        //选择一个下次使用到的ReadThread,类似于netty中 EventExecutorChooser的作用
        private ReadThread next(){
            return readThreadArray[(int)(Math.abs(atomicLong.getAndIncrement()%readThreadArray.length))];
        }
    }

    //类似于Netty中的NioEventLoop了
    static class ReadThread{
        private Selector selector;
        private Thread thread;
        private ConcurrentLinkedQueue<SocketChannel> queue;
        public ReadThread() throws IOException {
            selector = Selector.open();
            queue = new ConcurrentLinkedQueue<>();
            thread = new Thread(new ReadRunnable(selector, queue));
        }

        public void start(){
            thread.start();
        }

        public void register(SocketChannel socketChannel){
            queue.add(socketChannel);
            //添加到queue中后，及时的wakeup
            selector.wakeup();
        }
    }

    static class AcceptRunnable extends ServerSocketChannelTestV1 implements Runnable{
        private Selector selectorAccept;
        private ReadThreadGroup readThreadGroup;

        public AcceptRunnable(Selector selectorAccept, ReadThreadGroup readThreadGroup) {
            this.selectorAccept = selectorAccept;
            this.readThreadGroup = readThreadGroup;
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
                            //将accept到的SocketChannel添加到ReadThreadGroup中，具体是那个ReadThread此处不关心
                            readThreadGroup.register(socketChannel);
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
        private ConcurrentLinkedQueue<SocketChannel> queue;

        public ReadRunnable(Selector selectorRead, ConcurrentLinkedQueue<SocketChannel> queue) {
            this.selectorRead = selectorRead;
            this.queue = queue;
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
