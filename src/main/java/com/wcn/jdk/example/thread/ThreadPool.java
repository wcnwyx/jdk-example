package com.wcn.jdk.example.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        while(true){
            threadPoolExecutor.submit(new TestRunnable());
            Thread.sleep(1000);
        }
    }

    public static class TestRunnable implements Runnable{

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" |"+Thread.currentThread().getState());
        }
    }
}
