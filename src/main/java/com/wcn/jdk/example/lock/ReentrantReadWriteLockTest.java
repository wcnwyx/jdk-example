package com.wcn.jdk.example.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {
    public static void main(String[] args) throws InterruptedException {
        testSingleThreadReadWrite();
        testSingleThreadWriteRead();
        testMultiThreadRead();
    }

    /**
     * 单个线程，先获取读锁后，不释放读锁的时候，不能再成功获取写锁
     */
    public static void testSingleThreadReadWrite(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        boolean read = lock.readLock().tryLock();
        boolean write = lock.writeLock().tryLock();
        System.out.printf("read:%s write:%s \r\n", read, write);
    }

    /**
     * 单个线程，先获取写锁后，不释放写锁的时候可以再次成功获取读锁
     */
    public static void testSingleThreadWriteRead(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        boolean writeTryLock = lock.writeLock().tryLock();
        boolean readTryLock = lock.readLock().tryLock();
        System.out.printf("write:%s read:%s \r\n", writeTryLock, readTryLock);

        lock.writeLock().unlock();
        lock.readLock().unlock();

        lock.writeLock().lock();
        lock.readLock().lock();
        System.out.printf("write:%s read:%s \r\n", true, true);
    }

    /**
     *
     */
    public static void testMultiThreadRead() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.readLock().lock();
                System.out.println("thread1 read lock success. ");
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.readLock().lock();
                System.out.println("thread2 read lock success. ");
            }
        });
        t1.start();
        Thread.sleep(1000);
        t2.start();
    }
}
