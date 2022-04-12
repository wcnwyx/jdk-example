package com.wcn.jdk.example.cache;

import sun.misc.Contended;

/**
 * 因为缓存一致性协议，处于同一个CacheLine的数据在不同cpu中发生变化，两个cpu会来回进行同步处理，即发生伪共享。
 * 可以采用缓存行对齐策略来避免。即保证l1和l2不会存在一个缓存行中，即l1和l2都单独存在一个缓存行中。
 * jkd7之前，通常使用变量前后多定义几个long类型，来保证和会和其它变量处于一个缓存行。
 * jdk8之后添加了@Contended注解，jvm来做保证，而且针对不通缓存行大小的cpu，jvm自己来适配拼接不通的字节数。
 * 但是启动参数要添加 -XX:-RestrictContended
 */
public class CacheLineTest {
    @Contended
    volatile long l1;
    @Contended
    volatile long l2;

    public static void main(String[] args) throws InterruptedException {
        long times = 1_0000_0000L;
        CacheLineTest test = new CacheLineTest();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i=0;i<times;i++){
                    test.l1 = i;
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i=0;i<times;i++){
                    test.l2 = i;
                }
            }
        });

        long beginTime = System.currentTimeMillis();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("costTime:"+(System.currentTimeMillis()-beginTime));
    }
}
