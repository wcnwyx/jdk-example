package com.wcn.jdk.example.thread;

/**
 * 线程通过sleep(long millis)来休眠指定的时间，
 * 可以通过interrupt方法来中断，中断后抛出InterruptedException
 */
public class Thread_01_Sleep {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("before sleep");
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    //sleep期间可以使用interrupt()方法来打断睡眠，线程将抛出InterruptedException
                    e.printStackTrace();
                }
                System.out.println("after sleep");
            }
        });

        thread.start();
    }

}
