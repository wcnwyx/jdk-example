package com.wcn.jdk.example.thread;

/**
 * Thread.State 有6个状态：
 * NEW: 新建状态，未启动（start）时，只能从该状态启动线程
 * RUNNABLE: 可执行状态(就绪)，不管是等待CPU执行还是在执行中，都是这个状态
 * WAITING: 等待状态， Object.wait、Thread.join、 LockSupport.park都会使线程进入WAITING状态
 * TIMED_WAITING: 带有超时时间的等待状态，Thread.sleep(long)、Object.wait(long)、Thread.join(long)、 LockSupport.parkNanos(long)、LockSupport.parkUntil(long) 都会使线程进入WAITING状态
 * BLOCKED: 阻塞状态，进入synchronized时，如果获取不到锁，就会进入该状态
 * TERMINATED: 终止状态，线程运行完毕，进入终止状态，一旦终止，不可以再启动
 */
public class Thread_03_State {
    public static final Object WAIT_OBJECT = new Object();
    public static void main(String[] args) throws Exception {
        testRunnable();
        testWaiting();
        testTimedWaiting();
        testBlocked();
    }

    /**
     * 状态变化路径： NEW->RUNNABLE->TERMINATED
     * @throws Exception
     */
    public static void testRunnable() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("STATE:["+Thread.currentThread().getState()+"]");
            }
        });

        System.out.println("------------------RUNNABLE----------------");
        System.out.println("STATE:["+thread.getState()+"]");
        thread.start();
        Thread.sleep(100);
        System.out.println("STATE:["+thread.getState()+"]");
        System.out.println("------------------RUNNABLE----------------");
    }

    /**
     * 状态变化路径： NEW->WAITING->TERMINATED
     * @throws Exception
     */
    public static void testWaiting() throws Exception {
        final Object object = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(object){
                    try {
                        //Object.wait、Thread.join、 LockSupport.park都会使线程进入WAITING状态
                        System.out.println("wait begin...");
                        object.wait();
                        System.out.println("wait end.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("------------------WAITING----------------");
        System.out.println("STATE:["+thread.getState()+"]");
        thread.start();
        Thread.sleep(500L);
        System.out.println("STATE:["+thread.getState()+"]");
        synchronized (object){
            object.notify();
        }
        Thread.sleep(500L);
        System.out.println("STATE:["+thread.getState()+"]");
        System.out.println("------------------WAITING----------------");
    }

    /**
     * 状态变化路径： NEW->TIMED_WAITING->TERMINATED
     * @throws Exception
     */
    public static void testTimedWaiting() throws Exception {
        final Object object = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(object){
                    try {
                        //Thread.sleep(long)、Object.wait(long)、Thread.join(long)、 LockSupport.parkNanos(long)、LockSupport.parkUntil(long) 都会使线程进入WAITING状态
                        System.out.println("wait(long) begin...");
                        object.wait(1000);
                        System.out.println("wait(long) end.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("------------------TIMED_WAITING----------------");
        System.out.println("STATE:["+thread.getState()+"]");
        thread.start();
        Thread.sleep(500L);
        System.out.println("STATE:["+thread.getState()+"]");
        Thread.sleep(600L);
        System.out.println("STATE:["+thread.getState()+"]");
        System.out.println("------------------TIMED_WAITING----------------");
    }

    /**
     * 状态变化路径： NEW->Blocked->TERMINATED
     * @throws Exception
     */
    public static void testBlocked() throws Exception {
        final Object object = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("enter synchronized begin...");
                synchronized(object){
                    System.out.println("enter synchronized end.");
                }
            }
        });
        System.out.println("------------------BLOCKED----------------");
        System.out.println("STATE:["+thread.getState()+"]");
        synchronized (object){
            thread.start();
            Thread.sleep(500L);
            System.out.println("STATE:["+thread.getState()+"]");
        }
        Thread.sleep(600L);
        System.out.println("STATE:["+thread.getState()+"]");
        System.out.println("------------------BLOCKED----------------");
    }
}
