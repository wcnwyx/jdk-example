```java
/**
 * A synchronization aid that allows one or more threads to wait until
 * a set of operations being performed in other threads completes.
 * 一种同步辅助工具，允许一个或多个线程等待，直到在其他线程中执行的一组操作完成。
 *
 * <p>A {@code CountDownLatch} is initialized with a given <em>count</em>.
 * The {@link #await await} methods block until the current count reaches
 * zero due to invocations of the {@link #countDown} method, after which
 * all waiting threads are released and any subsequent invocations of
 * {@link #await await} return immediately.  This is a one-shot phenomenon
 * -- the count cannot be reset.  If you need a version that resets the
 * count, consider using a {@link CyclicBarrier}.
 * CountDownLatch是使用一个给定的count来初始化。 
 * await 方法会阻塞，直到当前计数由于调用 countDown 方法而达到零，之后释放所有等待的线程，
 * 后续任何调用 await 会立即返回。这是一种一次性现象——无法重置计数。
 * 如果需要重置计数的版本，请考虑使用 CyclicBarrier。
 *
 * <p>A {@code CountDownLatch} is a versatile synchronization tool
 * and can be used for a number of purposes.  A
 * {@code CountDownLatch} initialized with a count of one serves as a
 * simple on/off latch, or gate: all threads invoking {@link #await await}
 * wait at the gate until it is opened by a thread invoking {@link
 * #countDown}.  A {@code CountDownLatch} initialized to <em>N</em>
 * can be used to make one thread wait until <em>N</em> threads have
 * completed some action, or some action has been completed N times.
 * CountDownLatch 是一种多用途的同步工具，可用于多种目的。
 * CountDownLatch用一个计数初始化，用作简单的开/关 门闩或闸门：
 * 所有线程调用 await 方法等待闸门，知道一个线程调用countDown方法来打开闸门。
 * CountDownLatch的计数被初始化为N，可以让一个线程等待，知道N个想成完成某些动作，
 * 或者某些动作被完成N次。
 *
 * <p>A useful property of a {@code CountDownLatch} is that it
 * doesn't require that threads calling {@code countDown} wait for
 * the count to reach zero before proceeding, it simply prevents any
 * thread from proceeding past an {@link #await await} until all
 * threads could pass.
 * CountDownLatch一个有用的特性是，它不要求调用countDown的线程在继续之前要等待计数达到0，
 * 它只是防止任何线程通过await，知道所有线程都可以通过。
 *
 * <p><b>Sample usage:</b> Here is a pair of classes in which a group
 * of worker threads use two countdown latches:
 * 示例用法：这里有一对类，其中一组工作线程使用两个CountDownLatch：
 * <ul>
 * <li>The first is a start signal that prevents any worker from proceeding
 * until the driver is ready for them to proceed;
 * 第一个是一个启动信号，它阻止任何工人继续工作，直到司机准备好让他们继续工作；
 * 
 * <li>The second is a completion signal that allows the driver to wait
 * until all workers have completed.
 * 第二个是完成信号，允许司机等待所有工人完成。
 * </ul>
 *
 *  <pre> {@code
 * class Driver { // ...
 *   void main() throws InterruptedException {
 *     CountDownLatch startSignal = new CountDownLatch(1);
 *     CountDownLatch doneSignal = new CountDownLatch(N);
 *
 *     for (int i = 0; i < N; ++i) // create and start threads
 *       new Thread(new Worker(startSignal, doneSignal)).start();
 *
 *     doSomethingElse();            // don't let run yet
 *     startSignal.countDown();      // let all threads proceed
 *     doSomethingElse();
 *     doneSignal.await();           // wait for all to finish
 *   }
 * }
 *
 * class Worker implements Runnable {
 *   private final CountDownLatch startSignal;
 *   private final CountDownLatch doneSignal;
 *   Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
 *     this.startSignal = startSignal;
 *     this.doneSignal = doneSignal;
 *   }
 *   public void run() {
 *     try {
 *       startSignal.await();
 *       doWork();
 *       doneSignal.countDown();
 *     } catch (InterruptedException ex) {} // return;
 *   }
 *
 *   void doWork() { ... }
 * }}</pre>
 *
 * <p>Another typical usage would be to divide a problem into N parts,
 * describe each part with a Runnable that executes that portion and
 * counts down on the latch, and queue all the Runnables to an
 * Executor.  When all sub-parts are complete, the coordinating thread
 * will be able to pass through await. (When threads must repeatedly
 * count down in this way, instead use a {@link CyclicBarrier}.)
 * 另一个典型用法是将问题划分为N个部分，用一个Runnable描述每个部分，
 * Runnable执行该部分并对闩锁进行倒计时，然后将所有Runnable排队给一个执行器。
 * 当所有子部件完成时，协调线程将能够通过等待。（当线程必须以这种方式重复倒计时时，请改用{@link CyclicBarrier}。）
 *
 *  <pre> {@code
 * class Driver2 { // ...
 *   void main() throws InterruptedException {
 *     CountDownLatch doneSignal = new CountDownLatch(N);
 *     Executor e = ...
 *
 *     for (int i = 0; i < N; ++i) // create and start threads
 *       e.execute(new WorkerRunnable(doneSignal, i));
 *
 *     doneSignal.await();           // wait for all to finish
 *   }
 * }
 *
 * class WorkerRunnable implements Runnable {
 *   private final CountDownLatch doneSignal;
 *   private final int i;
 *   WorkerRunnable(CountDownLatch doneSignal, int i) {
 *     this.doneSignal = doneSignal;
 *     this.i = i;
 *   }
 *   public void run() {
 *     try {
 *       doWork(i);
 *       doneSignal.countDown();
 *     } catch (InterruptedException ex) {} // return;
 *   }
 *
 *   void doWork() { ... }
 * }}</pre>
 *
 * <p>Memory consistency effects: Until the count reaches
 * zero, actions in a thread prior to calling
 * {@code countDown()}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions following a successful return from a corresponding
 * {@code await()} in another thread.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class CountDownLatch {
    /**
     * Synchronization control For CountDownLatch.
     * Uses AQS state to represent count.
     * CountDownLatch的同步控制，使用AQS的state来表示count
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;

    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@linkplain Thread#interrupt interrupted}.
     * 导致当前线程等待锁计数倒计时到零，除非该线程为 interrupted 。
     *
     * <p>If the current count is zero then this method returns immediately.
     * 如果当前计数器时0则该方法会立即返回。
     *
     * <p>If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     * 如果当前计数大于零，则出于线程调度目的，当前线程将被禁用，并处于休眠状态，直到发生以下两种情况之一：
     * <ul>
     * <li>The count reaches zero due to invocations of the
     * {@link #countDown} method; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts}
     * the current thread.
     * </ul>
     * 计数通过调用countDown方法达到0；或者其他线程中断当前线程。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 如果当前线程：
     *      在进入该方法时已经有中断状态了；或者时在等待中被中断，则会抛出InterruptedException
     *      异常，并清除当前线程的中断状态。
     *
     * @throws InterruptedException if the current thread is interrupted
     *         while waiting
     */
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     * 减少该latch的计数，当计数达到0时，释放所有等待的线程。
     *
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     * 如果当前计数大于0则将它递减。如果新的计数时0则处于线程调度目的所有等待的线程将重新可用。
     *
     * <p>If the current count equals zero then nothing happens.
     * 如果当前计数等于0则什么都不发生。
     */
    public void countDown() {
        sync.releaseShared(1);
    }

    public long getCount() {
        return sync.getCount();
    }

}
```
总结：
1. 基本用法，一个或多个线程await，然后等待计数减到0，到0时将所有等待中的线程唤醒。
2. 只要count不为0，所有的tryAcquireShared就不会成功。
3. 只要count没有减到0，所有的tryReleaseShared也不会成功，最后一个将count见到0的线程才成功，再执行doReleaseShared操作。
4. 一个大任务分片处理，多个线程分片执行，都执行完了再继续。则是一个线程去wait，多个线程去countDown。
  这种情况下不太能显示出共享模式的用法，只是最后一个countDown为0的线程会去release掉那一个wait的线程。没有体现出传播的特性。
5. 还有一种用法是多个线程在wait，一个线程countDown后，所有wait的线程开始工作，这是就体现出了传播的特性。
  多个线程wait的时候，AQS队列中会有多个节点，countDown后第一个节点会被head给unPark，这只是唤醒了一个线程，
  其它几个线程可还在park中呢，第一个线程获取锁后，会通过setHeadAndPropagate方法将后面的一个唤醒，后面的再
  通过setHeadAndPropagate将其后面的一个唤醒，依次逐个唤醒，就体现了传播的动作了。
  