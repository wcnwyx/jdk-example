package com.wcn.jdk.example.io.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 该类是无法运作的，因为Selector是线程安全的，threadRead先进行了selectorRead.select()，
 * 这时threadRead阻塞，并不释放selector的，threadAccept在accept到新的SocketChannel时，
 * 无法调用进行register(), 也会进入阻塞等待。
 */
public class ServerSocketChannelTestV3 extends ServerSocketChannelTestV1{
    public static void main(String[] args) throws Exception{
        ServerSocketChannelTestV3 test = new ServerSocketChannelTestV3();
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
//                            selectorRead.wakeup(); 这里wakeup唤醒也是没用的，因为可能threadRead比该线程运行的快，又调用了select()方法。
                            super.accept(selectorRead, key);
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
                int selectNumRead = 0;
                try {
                    selectNumRead = selectorRead.select();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
