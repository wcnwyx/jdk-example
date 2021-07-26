package com.wcn.jdk.example.thread;

public class Thread_02_Join {
    public static void main(String[] args) throws InterruptedException {
        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("before sleep");
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+" interrupted");
                }
                System.out.println("after sleep");
            }
        }, "thread1");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("before join");
                try {
//                    thread1.join();
                    synchronized (thread1){
                        thread1.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+" interrupted");
                }
                System.out.println("after join");
            }
        }, "thread2");
        thread1.start();
        thread2.start();
        Thread.sleep(1000);
//        thread1.interrupt();
//        thread2.interrupt();
    }
}
