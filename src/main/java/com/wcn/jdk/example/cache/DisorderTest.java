package com.wcn.jdk.example.cache;

/**
 * JVM指令重排序，指令乱序执行验证。
 * 参考于： https://preshing.com/20120515/memory-reordering-caught-in-the-act/
 *
 * 验证逻辑：两个线程同时执行，将四个变量赋值。一共四个指令(1:a=1 2:x=b 3:b=1 4:y=a)。
 * 如果说不会发生乱序执行，那么在同一个线程中的指令1和2只会顺序执行，同样3和4也只会同样执行。
 * 所以两个线程最终地执行顺序无非以下几种排列组合（1永远在2前面，3永远在4前面）：
 * 1、2、3、4
 * 1、3、2、4
 * 1、3、4、2
 * 3、4、1、2
 * 3、1、2、4
 * 3、1、4、2
 * 最终的结果 x和y不可能同时为0。
 *
 * 那么如果说发生了乱序执行的话，就可以可能有这样的执行顺序：2、4、1、3
 * 就导致x和y同时为0。
 *
 *
 * JVM层级内存屏障：
 * LoadLoad屏障：
 *      对于这样的语句 load1; LoadLoad; load2
 *      在load2及后续读取操作要读取的数据被访问之前，保证load1要读取的数据已被读取完毕。
 * StoreStore屏障：
 *      对于这样的语句 store1; StoreStore; store2
 *      在store2及后续写入操作执行前，确保store1的写入操作对其它处理其可见。
 * LoadStore屏障：
 *      对于这样的的语句 load1; LoadStore; store2
 *      在store2及后续写入操作被刷出之前，保证load1要读取的数据被读取完毕。
 * StoreLoad屏障：
 *      对于这样的语句 store1; StoreLoad; load2
 *      在load2及后续的所有读取操作执行前，保证store1的所有写入对所有处理其可见。
 *
 * 所以在DCL单例（Double Check Lock）时，一定要配合使用volatile。
 * java中解决指令重排序地方法就是volatile，禁止指令重排序，JVM对于volatile的屏障如下：
 *
 * StoreStore屏障
 * volatile写操作
 * StoreLoad屏障
 *
 * LoadLoad屏障
 * volatile读操作
 * LoadStore屏障
 *
 * cpu层面解决方法是使用内存屏障元语来禁止指令重排序（lfence、mfence、sfence）
 * sfence（save fence）：屏障语句后地保存操作必须等到屏障前面地保存操作完成后才可以执行。
 * lfence（load fence）：屏障语句后地读取操作必须等到屏障前面地读取操作完成后才可以执行。
 * mfence（mix fence）：屏障语句后地读写操作必须等到屏障钱main地读写操作完成后才可以执行。
 *
 *
 * hanppens-before原则（JVM规定重排序必须遵守的规则）
 * JLS17.4.5
 * •程序次序规则：同一个线程内，按照照代码出现的顺序，前面的代码先行于后面的代码，准确的说是控制流顺序，因为要考虑到分支和循环结构。
 * •管程锁定规则：一个unlock操作先行发生于后面（时间上）对同一个锁的1ock操作。
 * •volatile变量规则：对一个volatile变量的写操作先行发生于后面（时间上）对这个变量的读操作。
 * •线程启动规则：Thread的start()方法先行发生于这个线程的每一个操作。
 * •线程终止规则：线程的所有操作都先行于此线程的终止检测。可以通过Thread.join()方法结束、Thread.isAlive()的返回值等手段检测线程的终止。
 * •线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生，可以通过Thread.interrupt()方法检测线程是否中断
 * •对象终结规则：一个对象的初始化完成先行于发生它的finalize()方法的开始。
 * •传递性：如果操作A先行于操作B，操作B先行于操作C，那么操作A先行于操作C
 *
 * as if serial : 不管如何重排序，单线程执行结果不会改变。
 */
public class DisorderTest {
    static int a, b, x, y;
    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        long times = 0;
        while(true){
            times++;
            a = 0; b = 0;
            x = 0; y = 0;
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    a = 1;
                    x = b;
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    b = 1;
                    y = a;
                }
            });
            thread1.start();thread2.start();
            thread1.join();thread2.join();
            if(x==0 && y==0){
                System.out.println("got it.");
                break;
            }
            if(times%10000==0){
                System.out.println("times:"+times/10000+"W");
            }
        }
        System.out.println("x==0 && y==0。 times="+times+" costTime="+(System.currentTimeMillis()-beginTime));
    }
}
