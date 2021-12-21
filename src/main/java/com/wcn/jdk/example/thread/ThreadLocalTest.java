package com.wcn.jdk.example.thread;

public class ThreadLocalTest {
    public static void main(String[] args) {
        ThreadLocal<Long> threadLocal = new ThreadLocal<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i=0;i<=Long.MAX_VALUE;i++){
                    ThreadLocal<Long> threadLocal = new ThreadLocal<>();
                    threadLocal.set(i);
                    if(i%10000==0){
                        System.out.println(i);
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        });
        thread.start();
    }
}
