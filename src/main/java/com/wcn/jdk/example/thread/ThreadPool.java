package com.wcn.jdk.example.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//        while(true){
//            threadPoolExecutor.submit(new TestRunnable());
//            Thread.sleep(2000);
//        }
        threadPoolExecutor.submit(new TestRunnable());
        threadPoolExecutor.shutdown();
        for(int i=0;i<=100;i++){
            try {
                Thread.sleep(1000);
                System.out.println(threadPoolExecutor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class TestRunnable implements Runnable{

        @Override
        public void run() {
            for(int i=0;i<=100;i++){
                try {
                    Thread.sleep(1000);
                    System.out.println("run. i="+i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
