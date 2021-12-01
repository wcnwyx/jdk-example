##线程的状态
操作系统的进程状态分为： 就绪、运行中、阻塞、终止。阻塞状态只能先变为就绪，再变为运行中。
```
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

## sleep方法  
1. 让线程睡眠指定的毫秒数。
2. sleep不会失去任何监视器的所有权。
3. 可以被中断。当被中断并抛出异常时，线程的interrupted状态会被清除。
```
/**
 * Causes the currently executing thread to sleep (temporarily cease
 * execution) for the specified number of milliseconds, subject to
 * the precision and accuracy of system timers and schedulers. The thread
 * does not lose ownership of any monitors.
 * 根据系统计时器和调度程序的精度和准确性，使当前执行的线程休眠（暂时停止执行）指定的毫秒数。
 * 线程不会失去任何监视器的所有权。
 *
 * @param  millis
 *         the length of time to sleep in milliseconds
 *
 * @throws  IllegalArgumentException
 *          if the value of {@code millis} is negative
 *
 * @throws  InterruptedException
 *          if any thread has interrupted the current thread. The
 *          <i>interrupted status</i> of the current thread is
 *          cleared when this exception is thrown.
 *          如果任何线程中断了该线程。当异常抛出时当前线程的interrupted状态也将清除。
 */
public static native void sleep(long millis) throws InterruptedException;
```

## join方法
1. b线程内部调用a.join()，说明b要等待a线程终止后再继续执行。
2. a线程终止后，虚拟机底层会调用a.notifyAll方法将b唤醒。
3. 可以被中断，可以被中断。当被中断并抛出异常时，线程的interrupted状态会被清除。
```
/**
 * Waits at most {@code millis} milliseconds for this thread to
 * die. A timeout of {@code 0} means to wait forever.
 * 等待该线程死亡，最多等待millis毫秒。如果超时时间为0意味着永远等待。
 *
 * <p> This implementation uses a loop of {@code this.wait} calls
 * conditioned on {@code this.isAlive}. As a thread terminates the
 * {@code this.notifyAll} method is invoked. It is recommended that
 * applications not use {@code wait}, {@code notify}, or
 * {@code notifyAll} on {@code Thread} instances.
 * 此实现使用this.isAlive循环调用this.wait。
 * 当线程终止时，调用this.notifyAll方法。
 * 不建议应用在Thread实例上使用 wait、notify、notifyAll。
 *
 * @param  millis
 *         the time to wait in milliseconds
 *
 * @throws  IllegalArgumentException
 *          if the value of {@code millis} is negative
 *
 * @throws  InterruptedException
 *          if any thread has interrupted the current thread. The
 *          <i>interrupted status</i> of the current thread is
 *          cleared when this exception is thrown.
 *          如果任何线程中断了该线程。当异常抛出时当前线程的interrupted状态也将清除。
 */
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

## yield方法
该方法就是说我让出一下cpu的使用权，但是我还是就绪状态，如果没有其它竞争者获得CPU，
CPU的执行权还是会给我，如果有其它竞争者获得CPU，那么就先让它执行。
```
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

## interrupt
1. 线程阻塞在不同的地方，抛出的异常是不一样的。
2. 线程阻塞在不同的地方，中断状态有的清除有的不清除。
```

/**
 * Interrupts this thread.
 * 中断该线程。
 *
 * <p> Unless the current thread is interrupting itself, which is
 * always permitted, the {@link #checkAccess() checkAccess} method
 * of this thread is invoked, which may cause a {@link
 * SecurityException} to be thrown.
 * 除非当前线程中断自身（这是始终允许的），否则将调用此线程的checkAccess方法，
 * 这可能会导致抛出SecurityException。
 *
 * <p> If this thread is blocked in an invocation of the {@link
 * Object#wait() wait()}, {@link Object#wait(long) wait(long)}, or {@link
 * Object#wait(long, int) wait(long, int)} methods of the {@link Object}
 * class, or of the {@link #join()}, {@link #join(long)}, {@link
 * #join(long, int)}, {@link #sleep(long)}, or {@link #sleep(long, int)},
 * methods of this class, then its interrupt status will be cleared and it
 * will receive an {@link InterruptedException}.
 * 如果在调用Object类的wait()、wait(long)或wait(long, int)方法时阻塞了该线程，
 * 或者在该类的join()、join(long)、join(long, int)、sleep(long)、sleep(long, int)方法阻塞，
 * 然后它的中断状态将被清除，它将收到一个InterruptedException。
 *
 * <p> If this thread is blocked in an I/O operation upon an {@link
 * java.nio.channels.InterruptibleChannel InterruptibleChannel}
 * then the channel will be closed, the thread's interrupt
 * status will be set, and the thread will receive a {@link
 * java.nio.channels.ClosedByInterruptException}.
 * 如果此线程在java.nio.channels.interruptablechannel上的I/O操作中被阻塞，
 * 然后通道将被关闭，线程的中断状态将被设置，线程将收到java.nio.channels.closedbyinteruptexception。
 *
 * <p> If this thread is blocked in a {@link java.nio.channels.Selector}
 * then the thread's interrupt status will be set and it will return
 * immediately from the selection operation, possibly with a non-zero
 * value, just as if the selector's {@link
 * java.nio.channels.Selector#wakeup wakeup} method were invoked.
 * 如果该线程在ava.nio.channels.Selector中被阻塞，那么该线程的中断状态将被设置，
 * 并且它将立即从选择操作返回，可能带有非零值，
 * 就像调用了选择器的java.nio.channels.Selector#wakeup方法一样。
 *
 * <p> If none of the previous conditions hold then this thread's interrupt
 * status will be set. </p>
 * 如果前面的条件都不成立，那么将设置该线程的中断状态。
 *
 * <p> Interrupting a thread that is not alive need not have any effect.
 * 中断一个非活动线程不需要有任何效果。
 *
 * @throws  SecurityException
 *          if the current thread cannot modify this thread
 *
 * @revised 6.0
 * @spec JSR-51
 */
public void interrupt() {
    if (this != Thread.currentThread())
        checkAccess();

    synchronized (blockerLock) {
        Interruptible b = blocker;
        if (b != null) {
            interrupt0();           // Just to set the interrupt flag
            b.interrupt(this);
            return;
        }
    }
    interrupt0();
}


/**
 * Tests if some Thread has been interrupted.  The interrupted state
 * is reset or not based on the value of ClearInterrupted that is
 * passed.
 * 测试线程是否已经中断。中断状态是否重置取决于传递的ClearInterrupted值。
 */
private native boolean isInterrupted(boolean ClearInterrupted);
    
```