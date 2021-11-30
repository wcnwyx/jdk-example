##线程的状态
```java
    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         * 线程还未启动时的状态。
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         * 可运行的状态。处于可运行状态的线程正在Java虚拟机中执行，
         * 但它可能正在等待来自操作系统的其他资源，如处理器。
         */
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         * 等待监视器锁定的被阻塞线程的线程状态。
         * 处于阻止状态的线程正在等待监视器锁进入同步块/方法，
         * 或在调用Object.wait后重新进入同步块/方法。
         */
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * 等待线程的线程状态。由于调用以下方法之一，线程处于等待状态：
         * 
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         * 
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         * 处于等待状态的线程正在等待另一个线程执行特定操作。
         *
         * For example, a thread that has called <tt>Object.wait()</tt>
         * on an object is waiting for another thread to call
         * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
         * that object. A thread that has called <tt>Thread.join()</tt>
         * is waiting for a specified thread to terminate.
         * 例如：一个线程调用了一个对象上的Object.wait()方法，是在等待另外一个线程调用该对象的Object.notify或者Object.notifyAll()方法。
         * 一个线程调用了Thread.join()是在等待一个特定的线程终止。
         */
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * 具有指定等待时间的等待线程的线程状态。
         * 使用指定的等待时间调用以下方法会使一个线程处于TIMED_WAITING状态：
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         * 线程已终止。已完成执行。
         */
        TERMINATED;
    }
```

##yield方法
该方法就是说我让出一下cpu的使用权，但是我还是就绪状态，如果没有其它竞争者获得CPU，
CPU的执行权还是会给我，如果有其它竞争者获得CPU，那么就先让它执行。
```java
    /**
     * A hint to the scheduler that the current thread is willing to yield
     * its current use of a processor. The scheduler is free to ignore this
     * hint.
     * 向调度程序发出的提示，表示当前线程愿意放弃处理器。调度程序可以随意忽略此提示。
     *
     * <p> Yield is a heuristic attempt to improve relative progression
     * between threads that would otherwise over-utilise a CPU. Its use
     * should be combined with detailed profiling and benchmarking to
     * ensure that it actually has the desired effect.
     * Yield是一种启发式尝试，旨在改善线程之间的相对进度，否则会过度使用CPU。
     * 它的使用应该与详细的分析和基准测试相结合，以确保它实际具有预期的效果。
     *
     * <p> It is rarely appropriate to use this method. It may be useful
     * for debugging or testing purposes, where it may help to reproduce
     * bugs due to race conditions. It may also be useful when designing
     * concurrency control constructs such as the ones in the
     * {@link java.util.concurrent.locks} package.
     * 很少适合使用该方法。它可能对调试或测试有用，因为它可能有助于重现由于竞争条件而产生的bug。
     * 在设计诸如{@link java.util.concurrent.locks}包中的并发控制结构时，它可能也很有用。
     * 在ReentrantLock的内部类ConditionObject的transferAfterCancelledWait方法里就有使用。
     */
    public static native void yield();
```