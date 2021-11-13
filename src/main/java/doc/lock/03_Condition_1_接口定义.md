```java
/**
 * {@code Condition} factors out the {@code Object} monitor
 * methods ({@link Object#wait() wait}, {@link Object#notify notify}
 * and {@link Object#notifyAll notifyAll}) into distinct objects to
 * give the effect of having multiple wait-sets per object, by
 * combining them with the use of arbitrary {@link Lock} implementations.
 * Where a {@code Lock} replaces the use of {@code synchronized} methods
 * and statements, a {@code Condition} replaces the use of the Object
 * monitor methods. 
 * Condition将 Object的监视器方法（wait、notify和notifyAll）分解成不同的对象，
 * 通过将它们与任意 Lock 实现相结合，为每个对象提供多个等待集的效果。 
 * 当Lock替换了synchronized方法和语句时，Condition替换了Object的监视器方法的使用。
 *
 * <p>Conditions (also known as <em>condition queues</em> or
 * <em>condition variables</em>) provide a means for one thread to
 * suspend execution (to &quot;wait&quot;) until notified by another
 * thread that some state condition may now be true.  Because access
 * to this shared state information occurs in different threads, it
 * must be protected, so a lock of some form is associated with the
 * condition. The key property that waiting for a condition provides
 * is that it <em>atomically</em> releases the associated lock and
 * suspends the current thread, just like {@code Object.wait}.
 * Conditions（也称为<em>条件队列</em>或<em>条件变量</em>）为一个线程提供了一种暂停执行（等待）的方法，
 * 直到另一个线程通知某些状态条件现在可能为真。由于对该共享状态信息的访问发生在不同的线程中，
 * 因此必须对其进行保护，因此某种形式的锁与该条件相关联。等待条件提供的关键属性是，
 * 它以原子方式释放关联的锁并挂起当前线程，就像{@code Object.wait}
 *
 * <p>A {@code Condition} instance is intrinsically bound to a lock.
 * To obtain a {@code Condition} instance for a particular {@link Lock}
 * instance use its {@link Lock#newCondition newCondition()} method.
 * 一个Condition实例本质上绑定到一个锁。要获取特定 Lock 实例的 Condition 实例，
 * 请使用其 Lock.newCondition（）方法.
 *
 * <p>As an example, suppose we have a bounded buffer which supports
 * {@code put} and {@code take} methods.  If a
 * {@code take} is attempted on an empty buffer, then the thread will block
 * until an item becomes available; if a {@code put} is attempted on a
 * full buffer, then the thread will block until a space becomes available.
 * We would like to keep waiting {@code put} threads and {@code take}
 * threads in separate wait-sets so that we can use the optimization of
 * only notifying a single thread at a time when items or spaces become
 * available in the buffer. This can be achieved using two
 * {@link Condition} instances.
 * <pre>
 * class BoundedBuffer {
 *   <b>final Lock lock = new ReentrantLock();</b>
 *   final Condition notFull  = <b>lock.newCondition(); </b>
 *   final Condition notEmpty = <b>lock.newCondition(); </b>
 *
 *   final Object[] items = new Object[100];
 *   int putptr, takeptr, count;
 *
 *   public void put(Object x) throws InterruptedException {
 *     <b>lock.lock();
 *     try {</b>
 *       while (count == items.length)
 *         <b>notFull.await();</b>
 *       items[putptr] = x;
 *       if (++putptr == items.length) putptr = 0;
 *       ++count;
 *       <b>notEmpty.signal();</b>
 *     <b>} finally {
 *       lock.unlock();
 *     }</b>
 *   }
 *
 *   public Object take() throws InterruptedException {
 *     <b>lock.lock();
 *     try {</b>
 *       while (count == 0)
 *         <b>notEmpty.await();</b>
 *       Object x = items[takeptr];
 *       if (++takeptr == items.length) takeptr = 0;
 *       --count;
 *       <b>notFull.signal();</b>
 *       return x;
 *     <b>} finally {
 *       lock.unlock();
 *     }</b>
 *   }
 * }
 * </pre>
 *
 * (The {@link java.util.concurrent.ArrayBlockingQueue} class provides
 * this functionality, so there is no reason to implement this
 * sample usage class.)
 *
 * <p>A {@code Condition} implementation can provide behavior and semantics
 * that is
 * different from that of the {@code Object} monitor methods, such as
 * guaranteed ordering for notifications, or not requiring a lock to be held
 * when performing notifications.
 * If an implementation provides such specialized semantics then the
 * implementation must document those semantics.
 * 一个Condition实现可以提供不同于 Object 监视方法的行为和语义，例如保证通知的顺序，或者在执行通知时不需要持有锁。
 * 如果一个实现提供了这种专门的语义，那么该实现必须记录这些语义。
 *
 * <p>Note that {@code Condition} instances are just normal objects and can
 * themselves be used as the target in a {@code synchronized} statement,
 * and can have their own monitor {@link Object#wait wait} and
 * {@link Object#notify notification} methods invoked.
 * Acquiring the monitor lock of a {@code Condition} instance, or using its
 * monitor methods, has no specified relationship with acquiring the
 * {@link Lock} associated with that {@code Condition} or the use of its
 * {@linkplain #await waiting} and {@linkplain #signal signalling} methods.
 * It is recommended that to avoid confusion you never use {@code Condition}
 * instances in this way, except perhaps within their own implementation.
 * 请注意，Condition实力只是普通对象，它们自身可以用作synchronized语义中的目标，
 * 并且可以调用其自身的监视器方法 Object.wait 和 Object.notify。
 * 获取Condition实例的监视器锁或使用其监视器方法 与 获取与该Condition关联的lock
 * 或使用其{@linkplain#wait waiting}和{@linkplain#signal signaling}方法没有特定的关系。
 * 为了避免混淆，建议不要以这种方式使用Condition实例，可能在它们自己的实现中除外。
 *
 * <p>Except where noted, passing a {@code null} value for any parameter
 * will result in a {@link NullPointerException} being thrown.
 * 除非另有说明，否则为任何参数传递 null 值将导致抛出 NullPointerException。
 *
 * <h3>Implementation Considerations</h3>
 * 实现考虑
 *
 * <p>When waiting upon a {@code Condition}, a &quot;<em>spurious
 * wakeup</em>&quot; is permitted to occur, in
 * general, as a concession to the underlying platform semantics.
 * This has little practical impact on most application programs as a
 * {@code Condition} should always be waited upon in a loop, testing
 * the state predicate that is being waited for.  An implementation is
 * free to remove the possibility of spurious wakeups but it is
 * recommended that applications programmers always assume that they can
 * occur and so always wait in a loop.
 * 在等待 Condition 时，通常允许出现虚假唤醒，作为对底层平台语义的让步。
 * 这对大多数应用程序几乎没有实际影响，因为 Condition 应该始终在循环中等待，测试正在等待的状态谓词。
 * 实现可以自由地消除虚假唤醒的可能性，但建议应用程序程序员始终假设它们可以发生，因此始终在循环中等待。
 *
 * <p>The three forms of condition waiting
 * (interruptible, non-interruptible, and timed) may differ in their ease of
 * implementation on some platforms and in their performance characteristics.
 * In particular, it may be difficult to provide these features and maintain
 * specific semantics such as ordering guarantees.
 * Further, the ability to interrupt the actual suspension of the thread may
 * not always be feasible to implement on all platforms.
 * 条件等待的三种形式（可中断、不可中断和定时）在某些平台上的易实现性和性能特征上可能有所不同。
 * 特别是，可能很难提供这些特性并维护特定的语义，例如排序保证。
 * 此外，中断线程实际暂停的能力可能并不总是能够在所有平台上实现。
 *
 * <p>Consequently, an implementation is not required to define exactly the
 * same guarantees or semantics for all three forms of waiting, nor is it
 * required to support interruption of the actual suspension of the thread.
 * 因此，实现不需要为所有三种形式的等待定义完全相同的保证或语义，也不需要支持线程实际暂停的中断。
 *
 * <p>An implementation is required to
 * clearly document the semantics and guarantees provided by each of the
 * waiting methods, and when an implementation does support interruption of
 * thread suspension then it must obey the interruption semantics as defined
 * in this interface.
 * 一个实现需要清楚地记录每个等待方法提供的语义和保证，当一个实现确实支持线程挂起的中断时，它必须遵守这个接口中定义的中断语义。
 *
 * <p>As interruption generally implies cancellation, and checks for
 * interruption are often infrequent, an implementation can favor responding
 * to an interrupt over normal method return. This is true even if it can be
 * shown that the interrupt occurred after another action that may have
 * unblocked the thread. An implementation should document this behavior.
 * 由于中断通常意味着取消，并且对中断的检查通常不经常发生，所以实现可能更倾向于响应中断而不是正常的方法返回。
 * 即使可以显示中断发生在另一个可能已解除线程阻塞的操作之后，这也是正确的。实现应该记录这种行为。
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Condition {

    /**
     * Causes the current thread to wait until it is signalled or
     * {@linkplain Thread#interrupt interrupted}.
     * 使当前线程等待，直到发出信号或中断。
     *
     * <p>The lock associated with this {@code Condition} is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of four things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     * 与此 Condition 关联的锁被自动释放，当前线程出于线程调度目的被禁用，并处于休眠状态，直到发生四种情况中的一种：
     * 1：其他一些线程为此Condition调用signal方法，而当前线程恰好被选为要唤醒的线程。
     * 2：其他一些线程为此Condition调用signalAll方法。
     * 3：其他一些线程interrupt当前线程，并且支持中断线程挂起。
     * 4：出现虚假唤醒。
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     * 在所有情况下，在该方法返回之前，当前线程必须重新获取与此条件关联的锁。当线程返回时，保证持有此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     * 如果当前线程：在进入该方法时设置了其中断状态；或者在等待时被中断并且支持线程挂起的中断，
     * 则抛出InterruptedException，并清除当前线程的中断状态。
     * 在第一种情况下，未规定是否在释放锁之前进行中断测试。
     *
     * <p><b>Implementation Considerations</b>
     * 实现考虑
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     * 调用此方法时，假定当前线程持有与此Condition关联的锁。
     * 由实现确定是否存在这种情况，如果不是，如何应对。通常，将引发异常（例如IllegalMonitorStateException），
     * 实现必须记录该事实。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal. In that case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     * 与响应信号的正常方法返回相比，实现更倾向于响应中断。
     * 在这种情况下，实现必须确保信号被重定向到另一个等待线程（如果有）。
     *
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     */
    void await() throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled.
     * 使当前线程等待，直到发出信号。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of three things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     * 与此 Condition 关联的锁被自动释放，当前线程出于线程调度目的被禁用，并处于休眠状态，直到发生三种情况中的一种：
     * 1：其他一些线程为此Condition调用signal方法，而当前线程恰好被选为要唤醒的线程。
     * 2：其他一些线程为此Condition调用signalAll方法。
     * 3：出现虚假唤醒。
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     * 在所有情况下，在该方法返回之前，当前线程必须重新获取与此条件关联的锁。当线程返回时，保证持有此锁。
     *
     * <p>If the current thread's interrupted status is set when it enters
     * this method, or it is {@linkplain Thread#interrupt interrupted}
     * while waiting, it will continue to wait until signalled. When it finally
     * returns from this method its interrupted status will still
     * be set.
     * 如果当前线程进入此方法时设置了中断状态，或者在等待时中断，则它将继续等待，直到发出信号。
     * 当它最终从此方法返回时，其中断状态仍将设置。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     * 调用此方法时，假定当前线程持有与此Condition关联的锁。
     * 由实现确定是否存在这种情况，如果不是，如何应对。通常，将引发异常（例如IllegalMonitorStateException），
     * 实现必须记录该事实。
     */
    void awaitUninterruptibly();

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified waiting time elapses.
     * 使当前线程等待，直到发出信号或中断，或者超过指定的等待时间。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of five things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>The specified waiting time elapses; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     * 与此 Condition 关联的锁被自动释放，当前线程出于线程调度目的被禁用，并处于休眠状态，直到发生五种情况中的一种：
     * 1：其他一些线程为此Condition调用signal方法，而当前线程恰好被选为要唤醒的线程。
     * 2：其他一些线程为此Condition调用signalAll方法。
     * 3：其他一些线程interrupt当前线程，并且支持中断线程挂起。
     * 4：超过指定的等待时间。
     * 5：出现虚假唤醒。
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     * 在所有情况下，在该方法返回之前，当前线程必须重新获取与此条件关联的锁。当线程返回时，保证持有此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     * 如果当前线程：在进入该方法时设置了其中断状态；或者在等待时被中断并且支持线程挂起的中断，
     * 则抛出InterruptedException，并清除当前线程的中断状态。
     * 在第一种情况下，未规定是否在释放锁之前进行中断测试。
     *
     * <p>The method returns an estimate of the number of nanoseconds
     * remaining to wait given the supplied {@code nanosTimeout}
     * value upon return, or a value less than or equal to zero if it
     * timed out. This value can be used to determine whether and how
     * long to re-wait in cases where the wait returns but an awaited
     * condition still does not hold. Typical uses of this method take
     * the following form:
     * 该方法返回提供的nanosTimeout值的剩余等待纳秒数的估计值，或者如果超时，则返回小于或等于零的值。
     * 此值可用于确定在等待返回但等待的条件仍然不成立的情况下是否重新等待以及等待多长时间。
     * 该方法的典型用途如下：
     *
     *  <pre> {@code
     * boolean aMethod(long timeout, TimeUnit unit) {
     *   long nanos = unit.toNanos(timeout);
     *   lock.lock();
     *   try {
     *     while (!conditionBeingWaitedFor()) {
     *       if (nanos <= 0L)
     *         return false;
     *       nanos = theCondition.awaitNanos(nanos);
     *     }
     *     // ...
     *   } finally {
     *     lock.unlock();
     *   }
     * }}</pre>
     *
     * <p>Design note: This method requires a nanosecond argument so
     * as to avoid truncation errors in reporting remaining times.
     * Such precision loss would make it difficult for programmers to
     * ensure that total waiting times are not systematically shorter
     * than specified when re-waits occur.
     * 设计说明：此方法需要纳秒参数，以避免报告剩余时间时出现截断错误。
     * 这样的精度损失将使程序员难以确保在重新等待时总等待时间不会系统地短于指定的时间。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     * 调用此方法时，假定当前线程持有与此Condition关联的锁。
     * 由实现确定是否存在这种情况，如果不是，如何应对。通常，将引发异常（例如IllegalMonitorStateException），
     * 实现必须记录该事实。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal, or over indicating the elapse
     * of the specified waiting time. In either case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     * 与响应信号的正常方法返回相比，实现更倾向于响应中断或超过指定的等待时间。
     * 在任何一种情况下，实现必须确保信号被重定向到另一个等待线程（如果有）。
     *
     * @param nanosTimeout the maximum time to wait, in nanoseconds
     * @return an estimate of the {@code nanosTimeout} value minus
     *         the time spent waiting upon return from this method.
     *         A positive value may be used as the argument to a
     *         subsequent call to this method to finish waiting out
     *         the desired time.  A value less than or equal to zero
     *         indicates that no time remains.
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     */
    long awaitNanos(long nanosTimeout) throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified waiting time elapses. This method is behaviorally
     * equivalent to:
     *  <pre> {@code awaitNanos(unit.toNanos(time)) > 0}</pre>
     *  此方法在行为上等同于awaitNanos(unit.toNanos(time)) > 0
     *
     * @param time the maximum time to wait
     * @param unit the time unit of the {@code time} argument
     * @return {@code false} if the waiting time detectably elapsed
     *         before return from the method, else {@code true}
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     */
    boolean await(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified deadline elapses.
     * 使当前线程等待，直到发出信号或中断，或者超过指定的截至时间。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of five things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>The specified deadline elapses; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     * 与此 Condition 关联的锁被自动释放，当前线程出于线程调度目的被禁用，并处于休眠状态，直到发生五种情况中的一种：
     * 1：其他一些线程为此Condition调用signal方法，而当前线程恰好被选为要唤醒的线程。
     * 2：其他一些线程为此Condition调用signalAll方法。
     * 3：其他一些线程interrupt当前线程，并且支持中断线程挂起。
     * 4：超过指定的截至时间。
     * 5：出现虚假唤醒。
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     * 在所有情况下，在该方法返回之前，当前线程必须重新获取与此条件关联的锁。当线程返回时，保证持有此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     * 如果当前线程：在进入该方法时设置了其中断状态；或者在等待时被中断并且支持线程挂起的中断，
     * 则抛出InterruptedException，并清除当前线程的中断状态。
     * 在第一种情况下，未规定是否在释放锁之前进行中断测试。
     *
     * <p>The return value indicates whether the deadline has elapsed,
     * which can be used as follows:
     * 返回值指示截止日期是否已过，可按如下方式使用：
     * 
     *  <pre> {@code
     * boolean aMethod(Date deadline) {
     *   boolean stillWaiting = true;
     *   lock.lock();
     *   try {
     *     while (!conditionBeingWaitedFor()) {
     *       if (!stillWaiting)
     *         return false;
     *       stillWaiting = theCondition.awaitUntil(deadline);
     *     }
     *     // ...
     *   } finally {
     *     lock.unlock();
     *   }
     * }}</pre>
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     * 调用此方法时，假定当前线程持有与此Condition关联的锁。
     * 由实现确定是否存在这种情况，如果不是，如何应对。通常，将引发异常（例如IllegalMonitorStateException），
     * 实现必须记录该事实。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal, or over indicating the passing
     * of the specified deadline. In either case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     * 与响应信号的正常方法返回相比，实现更倾向于响应中断或或超过指定的截止日期。
     * 在任何一种情况下，实现必须确保信号被重定向到另一个等待线程（如果有）。
     *
     * @param deadline the absolute time to wait until
     * @return {@code false} if the deadline has elapsed upon return, else
     *         {@code true}
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     */
    boolean awaitUntil(Date deadline) throws InterruptedException;

    /**
     * Wakes up one waiting thread.
     * 唤醒一个正在等待的线程。
     *
     * <p>If any threads are waiting on this condition then one
     * is selected for waking up. That thread must then re-acquire the
     * lock before returning from {@code await}.
     * 如果有任何线程在此条件下等待，则会选择一个线程进行唤醒。然后，该线程必须在从{@code wait}返回之前重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>An implementation may (and typically does) require that the
     * current thread hold the lock associated with this {@code
     * Condition} when this method is called. Implementations must
     * document this precondition and any actions taken if the lock is
     * not held. Typically, an exception such as {@link
     * IllegalMonitorStateException} will be thrown.
     * 调用此方法时，实现可能（并且通常确实）要求当前线程持有与此{@code Condition}关联的锁。
     * 实现必须记录此先决条件以及未持有锁时所采取的任何操作。通常，会引发IllegalMonitorStateException之类的异常。
     */
    void signal();

    /**
     * Wakes up all waiting threads.
     * 唤醒所有等待中的线程。
     *
     * <p>If any threads are waiting on this condition then they are
     * all woken up. Each thread must re-acquire the lock before it can
     * return from {@code await}.
     * 如果有任何线程在此条件下等待，则会将所有线程进行唤醒。然后，每个线程都必须在从{@code wait}返回之前重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>An implementation may (and typically does) require that the
     * current thread hold the lock associated with this {@code
     * Condition} when this method is called. Implementations must
     * document this precondition and any actions taken if the lock is
     * not held. Typically, an exception such as {@link
     * IllegalMonitorStateException} will be thrown.
     * 调用此方法时，实现可能（并且通常确实）要求当前线程持有与此{@code Condition}关联的锁。
     * 实现必须记录此先决条件以及未持有锁时所采取的任何操作。通常，会引发IllegalMonitorStateException之类的异常。
     */
    void signalAll();
}
```