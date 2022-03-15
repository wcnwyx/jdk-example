package com.wcn.jdk.example.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {
    public static void main(String[] args) {
        testOneReadWrite();
        testOneWriteRead();
    }

    /**
     * 单个线程，先获取读锁后，不释放读锁的时候，不能再成功获取写锁
     */
    public static void testOneReadWrite(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        boolean read = lock.readLock().tryLock();
        boolean write = lock.writeLock().tryLock();
        System.out.printf("read:%s write:%s \r\n", read, write);
    }

    /**
     * 单个线程，现获取写锁后，不释放写锁的时候可以再次成功获取读锁
     */
    public static void testOneWriteRead(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        boolean write = lock.writeLock().tryLock();
        boolean read = lock.readLock().tryLock();
        System.out.printf("write:%s read:%s \r\n", write, read);
    }
}
