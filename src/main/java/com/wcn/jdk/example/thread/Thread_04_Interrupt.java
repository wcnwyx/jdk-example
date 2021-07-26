package com.wcn.jdk.example.thread;

public class Thread_04_Interrupt {
    public static void main(String[] args) throws InterruptedException {
        testInterruptNone();
//        testInterruptSleepWaitJoin();
    }

    /**
     * 如果线程处于运行中，执行interrupt后，线程会正常继续执行，只是将线程的中断状态置位true
     */
    public static void testInterruptNone(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long beginTime = System.currentTimeMillis();
                while(System.currentTimeMillis()-beginTime<=2000L){

                }
                System.out.println("线程运行完毕");
            }
        });
        thread.start();
        System.out.println("isInterrupted:"+thread.isInterrupted());
        thread.interrupt();
        System.out.println("isInterrupted:"+thread.isInterrupted());
    }

    /**
     * 测试使用sleep、wait、join方法使线程进入等待状态后 中断线程
     * @throws InterruptedException
     */
    public static void testInterruptSleepWaitJoin() throws InterruptedException {
        final Object object = new Object();
        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("进入sleep");
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    System.out.println("从sleep中被中断");
                }

                try {
                    System.out.println("进入wait");
                    synchronized (object){
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("从wait中被中断");
                }

                try {
                    System.out.println("进入join");
                    thread1.join();
                } catch (InterruptedException e) {
                    System.out.println("从join中被中断");
                }
                System.out.println("线程执行完毕");
            }
        });

        thread1.start();
        thread2.start();
        Thread.sleep(500);
        thread2.interrupt();
        Thread.sleep(500);
        thread2.interrupt();
        Thread.sleep(500);
        thread2.interrupt();
    }
}
