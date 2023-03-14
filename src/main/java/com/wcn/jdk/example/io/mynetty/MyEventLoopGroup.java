package com.wcn.jdk.example.io.mynetty;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class MyEventLoopGroup {
    private AtomicLong atomicLong = new AtomicLong(0);
    private MyEventLoop[] child;

    public MyEventLoopGroup(int threadNum) throws IOException {
        assert threadNum>0;
        child = new MyEventLoop[threadNum];
        for(int i=0;i<threadNum;i++){
            child[i] = new MyEventLoop();
        }
    }

//    public void register()

    private MyEventLoop next(){
        return child[(int)(atomicLong.getAndIncrement()%child.length)];
    }
}
