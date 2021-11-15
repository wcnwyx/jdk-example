package com.wcn.jdk.example.lock;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread1 before await. count="+cdl.getCount());
                    cdl.await();
                    System.out.println("thread1 after await. count="+cdl.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread1");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread2 before await. count="+cdl.getCount());
                    cdl.await();
                    System.out.println("thread2 after await. count="+cdl.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread2");

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread3 before await. count="+cdl.getCount());
                    cdl.await();
                    System.out.println("thread3 after await. count="+cdl.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread3");

        thread1.start();
        Thread.sleep(500);
        thread2.start();
        Thread.sleep(500);
        thread3.start();
        Thread.sleep(500);

        cdl.countDown();
    }

}
