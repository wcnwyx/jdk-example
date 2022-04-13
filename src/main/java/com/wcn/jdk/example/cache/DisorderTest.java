package com.wcn.jdk.example.cache;

/**
 * 指令乱序执行验证。
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
 * 参考于： https://preshing.com/20120515/memory-reordering-caught-in-the-act/
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
