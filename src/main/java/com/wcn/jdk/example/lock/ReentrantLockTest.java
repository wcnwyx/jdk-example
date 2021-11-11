package com.wcn.jdk.example.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    public static void main(String[] args) {
//        normal();
        testCancel();
    }

    public static void testCancel(){
        ReentrantLock lock = new ReentrantLock();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread1 lock begin.");
                    lock.lock();
                    System.out.println("thread1 lock success.");
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread1 unlock begin.");
                    lock.unlock();
                    System.out.println("thread1 unlock success.");
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread2 lock begin.");
                    lock.lock();
                    System.out.println("thread2 lock success.");
//                    Thread.sleep(Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread2 unlock begin.");
                    lock.unlock();
                    System.out.println("thread2 unlock success.");
                }
            }
        });

        thread1.start();
        try {
            Thread.sleep(1*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
        try {
            Thread.sleep(20*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("before interrupt");
        thread2.interrupt();
    }

    public static void normal() {
        ReentrantLock lock = new ReentrantLock();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread1 lock begin.");
                    lock.lock();
                    System.out.println("thread1 lock success.");
                    Thread.sleep(5*1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread1 unlock begin.");
                    lock.unlock();
                    System.out.println("thread1 unlock success.");
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread2 lock begin.");
                    lock.lock();
                    System.out.println("thread2 lock success.");
//                    Thread.sleep(Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread2 unlock begin.");
                    lock.unlock();
                    System.out.println("thread2 unlock success.");
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("thread3 lock begin.");
                    lock.lock();
                    System.out.println("thread3 lock success.");
//                    Thread.sleep(Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread3 unlock begin.");
                    lock.unlock();
                    System.out.println("thread3 unlock success.");
                }
            }
        });
        thread1.setName("thread1");
        thread2.setName("thread2");
        thread3.setName("thread3");
        thread1.start();
        try {
            Thread.sleep(1*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
        try {
            Thread.sleep(1*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread3.start();
    }

}
