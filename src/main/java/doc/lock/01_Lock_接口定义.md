```java
/**
 * {@code Lock} implementations provide more extensive locking
 * operations than can be obtained using {@code synchronized} methods
 * and statements.  They allow more flexible structuring, may have
 * quite different properties, and may support multiple associated
 * {@link Condition} objects.
 * 
 * Lock实现提供了比使用synchronized方法和语句更广泛的锁定操作。
 * 它们允许更灵活的结构，可能具有完全不同的属性，并且可能支持多个关联的 Condition 对象。
 *
 * <p>A lock is a tool for controlling access to a shared resource by
 * multiple threads. Commonly, a lock provides exclusive access to a
 * shared resource: only one thread at a time can acquire the lock and
 * all access to the shared resource requires that the lock be
 * acquired first. However, some locks may allow concurrent access to
 * a shared resource, such as the read lock of a {@link ReadWriteLock}.
 * 
 * 锁是一种工具，用于控制多个线程对共享资源的访问。通常，锁提供对共享资源的独占访问：
 * 一次只有一个线程可以获取锁，所有对共享资源的访问都要求首先获取锁。
 * 但是，某些锁可能允许并发访问共享资源，例如 ReadWriteLock 的读锁。
 *
 * <p>The use of {@code synchronized} methods or statements provides
 * access to the implicit monitor lock associated with every object, but
 * forces all lock acquisition and release to occur in a block-structured way:
 * when multiple locks are acquired they must be released in the opposite
 * order, and all locks must be released in the same lexical scope in which
 * they were acquired.
 * 
 * {@code synchronized}方法或语句的使用提供了对与每个对象关联的隐式监视器锁的访问，
 * 但强制所有锁的获取和释放以块结构的方式进行：当获取多个锁时，必须以相反的顺序释放它们，
 * 所有锁都必须在获取它们的相同词法范围内释放。
 *
 * <p>While the scoping mechanism for {@code synchronized} methods
 * and statements makes it much easier to program with monitor locks,
 * and helps avoid many common programming errors involving locks,
 * there are occasions where you need to work with locks in a more
 * flexible way. For example, some algorithms for traversing
 * concurrently accessed data structures require the use of
 * &quot;hand-over-hand&quot; or &quot;chain locking&quot;: you
 * acquire the lock of node A, then node B, then release A and acquire
 * C, then release B and acquire D and so on.  Implementations of the
 * {@code Lock} interface enable the use of such techniques by
 * allowing a lock to be acquired and released in different scopes,
 * and allowing multiple locks to be acquired and released in any
 * order.
 * 
 * 虽然 synchronized 方法和语句的作用域机制使使用监视器锁编程变得更加容易，
 * 并有助于避免许多涉及锁的常见编程错误，但在某些情况下，您需要以更灵活的方式使用锁。
 * 例如，一些用于遍历并发访问的数据结构的算法需要使用交锁或链锁：先获取节点A的锁，
 * 然后获取节点B，然后释放A并获取C，然后释放B并获取D，依此类推。
 * Lock 接口的实现允许在不同的范围内获取和释放锁，并允许以任何顺序获取和释放多个锁，
 * 从而支持使用此类技术。
 *
 * <p>With this increased flexibility comes additional
 * responsibility. The absence of block-structured locking removes the
 * automatic release of locks that occurs with {@code synchronized}
 * methods and statements. In most cases, the following idiom
 * should be used:
 * 
 * 随着灵活性的提高，还需要承担更多的责任。由于没有块结构锁，
 * 因此 synchronized 方法和语句会自动释放锁。在大多数情况下，应使用以下习语
 *
 *  <pre> {@code
 * Lock l = ...;
 * l.lock();
 * try {
 *   // access the resource protected by this lock
 * } finally {
 *   l.unlock();
 * }}</pre>
 *
 * When locking and unlocking occur in different scopes, care must be
 * taken to ensure that all code that is executed while the lock is
 * held is protected by try-finally or try-catch to ensure that the
 * lock is released when necessary.
 * 
 * 当锁定和解锁在不同的作用域中发生时，必须注意确保持有锁时执行的所有代码都受到
 * try-finally或try-catch的保护，以确保在必要时释放锁。
 *
 * <p>{@code Lock} implementations provide additional functionality
 * over the use of {@code synchronized} methods and statements by
 * providing a non-blocking attempt to acquire a lock ({@link
 * #tryLock()}), an attempt to acquire the lock that can be
 * interrupted ({@link #lockInterruptibly}, and an attempt to acquire
 * the lock that can timeout ({@link #tryLock(long, TimeUnit)}).
 * 
 * Lock 在使用 synchronized 方法和语句的基础上提供了额外的功能，
 * 实现通过提供获取锁的非阻塞尝试 tryLock()、获取可中断锁的尝试 lockInterruptibly() ，
 * 以及尝试获取可能超时的锁 tryLock(long，TimeUnit)。
 *
 * <p>A {@code Lock} class can also provide behavior and semantics
 * that is quite different from that of the implicit monitor lock,
 * such as guaranteed ordering, non-reentrant usage, or deadlock
 * detection. If an implementation provides such specialized semantics
 * then the implementation must document those semantics.
 * 
 * Lock 类还可以提供与隐式监视器锁截然不同的行为和语义，例如保证顺序、不可重入使用或死锁检测。
 * 如果一个实现提供了这种专门的语义，那么该实现必须记录这些语义。
 *
 * <p>Note that {@code Lock} instances are just normal objects and can
 * themselves be used as the target in a {@code synchronized} statement.
 * Acquiring the
 * monitor lock of a {@code Lock} instance has no specified relationship
 * with invoking any of the {@link #lock} methods of that instance.
 * It is recommended that to avoid confusion you never use {@code Lock}
 * instances in this way, except within their own implementation.
 * 
 * 请注意，Lock实例只是普通对象，它们本身可以用作 synchronized 语句中的目标。
 * 获取 lock 实例的监视器锁与调用该实例的任何 lock 方法没有指定的关系。
 * 为了避免混淆，建议不要以这种方式使用 Lock 实例，除非在它们自己的实现中使用。
 *
 * <p>Except where noted, passing a {@code null} value for any
 * parameter will result in a {@link NullPointerException} being
 * thrown.
 * 
 * 除非另有说明，否则为任何参数传递 null 值将导致抛出 NullPointerException。
 *
 * <h3>Memory Synchronization</h3>
 * 内存同步
 *
 * <p>All {@code Lock} implementations <em>must</em> enforce the same
 * memory synchronization semantics as provided by the built-in monitor
 * lock, as described in
 * <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.4">
 * The Java Language Specification (17.4 Memory Model)</a>:
 * <ul>
 * <li>A successful {@code lock} operation has the same memory
 * synchronization effects as a successful <em>Lock</em> action.
 * <li>A successful {@code unlock} operation has the same
 * memory synchronization effects as a successful <em>Unlock</em> action.
 * </ul>
 * 
 * 所有 Lock 实现必须执行与内置监视器锁相同的内存同步语义，
 * 如<a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.4">
 * 所诉，Java语言规范（17.4内存模型）：
 * 成功的{@code lock}操作与成功的<em>Lock</em>操作具有相同的内存同步效果。
 * 成功的{@code unlock}操作与成功的<em>unlock</em>操作具有相同的内存同步效果。
 *
 * Unsuccessful locking and unlocking operations, and reentrant
 * locking/unlocking operations, do not require any memory
 * synchronization effects.
 * 
 * 不成功的锁定和解锁操作以及可重入的锁定/解锁操作不需要任何内存同步效果。
 *
 * <h3>Implementation Considerations</h3>
 * 关于实现的考虑
 *
 * <p>The three forms of lock acquisition (interruptible,
 * non-interruptible, and timed) may differ in their performance
 * characteristics, ordering guarantees, or other implementation
 * qualities.  Further, the ability to interrupt the <em>ongoing</em>
 * acquisition of a lock may not be available in a given {@code Lock}
 * class.  Consequently, an implementation is not required to define
 * exactly the same guarantees or semantics for all three forms of
 * lock acquisition, nor is it required to support interruption of an
 * ongoing lock acquisition.  An implementation is required to clearly
 * document the semantics and guarantees provided by each of the
 * locking methods. It must also obey the interruption semantics as
 * defined in this interface, to the extent that interruption of lock
 * acquisition is supported: which is either totally, or only on
 * method entry.
 * 
 * 锁获取的三种形式（可中断、不可中断和定时）在性能特征、顺序保证或其他实现质量方面可能有所不同。
 * 此外，在给定的 lock 类中，中断正在进行的锁获取的能力可能不可用。
 * 因此，实现不需要为所有三种形式的锁获取定义完全相同的保证或语义，也不需要支持正在进行的锁获取的中断。
 * 需要一个实现来清楚地记录每个锁定方法提供的语义和保证。它还必须遵守此接口中定义的中断语义，
 * 以支持锁获取的中断：要么完全中断，要么仅在方法条目上中断。
 *
 * <p>As interruption generally implies cancellation, and checks for
 * interruption are often infrequent, an implementation can favor responding
 * to an interrupt over normal method return. This is true even if it can be
 * shown that the interrupt occurred after another action may have unblocked
 * the thread. An implementation should document this behavior.
 * 
 * 由于中断通常意味着取消，并且对中断的检查通常不经常发生，所以实现可能更倾向于响应中断而不是正常的方法返回。
 * 即使可以显示中断发生在另一个操作解除线程阻塞后，这也是正确的。实现应该记录这种行为。
 *
 * @see ReentrantLock
 * @see Condition
 * @see ReadWriteLock
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Lock {

    /**
     * Acquires the lock.
     * 获取锁
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     * 如果锁不可用，则出于线程调度目的，当前线程将被禁用，并处于休眠状态，直到获得锁为止。
     *
     * <p><b>Implementation Considerations</b>
     * 实现考虑
     *
     * <p>A {@code Lock} implementation may be able to detect erroneous use
     * of the lock, such as an invocation that would cause deadlock, and
     * may throw an (unchecked) exception in such circumstances.  The
     * circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     * Lock实现可能能够检测锁的错误使用，例如可能导致死锁的调用，
     * 并且在这种情况下可能抛出（未经检查的）异常。Lock实现必须记录环境和异常类型。
     */
    void lock();

    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     * 获取锁直到当前线程被中断
     *
     * <p>Acquires the lock if it is available and returns immediately.
     * 获取锁（如果可用）并立即返回。
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     * 如果锁不可用，则出于线程调度目的，当前线程将被禁用，并处于休眠状态，直到发生以下两种情况之一：
     *
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     * 当前线程已获取到该锁。
     * 其它一些线程中断了当前线程，并且支持中断锁获取。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
     * lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 如果当前线程：在进入该方法时设置了其中断状态；或者在获取锁时被中断，并且支持中断锁获取，
     * 则抛出 InterruptedException ，并清除当前线程的中断状态。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some
     * implementations may not be possible, and if possible may be an
     * expensive operation.  The programmer should be aware that this
     * may be the case. An implementation should document when this is
     * the case.
     * 在某些实现中，中断锁获取的能力可能是不可能的，如果可能的话，可能是一个昂贵的操作。
     * 程序员应该知道可能是这样的。在这种情况下，实现应该记录下来。
     *
     * <p>An implementation can favor responding to an interrupt over
     * normal method return.
     * 与正常的方法返回相比，实现更倾向于响应中断。
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would
     * cause deadlock, and may throw an (unchecked) exception in such
     * circumstances.  The circumstances and the exception type must
     * be documented by that {@code Lock} implementation.
     * Lock实现可能能够检测锁的错误使用，例如可能导致死锁的调用，
     * 并且在这种情况下可能抛出（未经检查的）异常。Lock实现必须记录环境和异常类型。
     *
     * @throws InterruptedException if the current thread is
     *         interrupted while acquiring the lock (and interruption
     *         of lock acquisition is supported)
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * Acquires the lock only if it is free at the time of invocation.
     * 仅当锁在调用时可用才获取锁。
     *
     * <p>Acquires the lock if it is available and returns immediately
     * with the value {@code true}.
     * If the lock is not available then this method will return
     * immediately with the value {@code false}.
     * 如果锁可用，则获取锁并立即返回值 true 。如果锁不可用，则此方法将立即返回值 false
     *
     * <p>A typical usage idiom for this method would be:
     *  <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}</pre>
     *
     * This usage ensures that the lock is unlocked if it was acquired, and
     * doesn't try to unlock if the lock was not acquired.
     *
     * @return {@code true} if the lock was acquired and
     *         {@code false} otherwise
     */
    boolean tryLock();

    /**
     * Acquires the lock if it is free within the given waiting time and the
     * current thread has not been {@linkplain Thread#interrupt interrupted}.
     * 如果锁在给定的等待时间内空闲，并且当前线程未被 interrupted 中断，则获取锁。
     *
     * <p>If the lock is available this method returns immediately
     * with the value {@code true}.
     * If the lock is not available then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happens:
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported; or
     * <li>The specified waiting time elapses
     * </ul>
     * 如果锁可用，该方法会立即返回值true。
     * 如果锁不可用，则出于线程调度目的，当前线程将被禁用，并处于休眠状态，直到发生以下三种情况之一：
     * 当前线程已获取到该锁。
     * 其它一些线程中断了当前线程，并且支持中断锁获取。
     * 指定的等待时间已过
     *
     * <p>If the lock is acquired then the value {@code true} is returned.
     * 如果锁被获取到，则会返回值true。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
     * the lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 如果当前线程：在进入该方法时设置了其中断状态；或者在获取锁时被中断，并且支持中断锁获取，
     * 则抛出 InterruptedException ，并清除当前线程的中断状态。
     *
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     * 如果指定的等待时间已过，则返回值false。
     * 如果时间小于等于0，该方法根本不会等待。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some implementations
     * may not be possible, and if possible may
     * be an expensive operation.
     * The programmer should be aware that this may be the case. An
     * implementation should document when this is the case.
     * 在某些实现中，中断锁获取的能力可能是不可能的，如果可能的话，可能是一个昂贵的操作。
     * 程序员应该知道可能是这样的。在这种情况下，实现应该记录下来。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return, or reporting a timeout.
     * 与正常方法返回或报告超时相比，实现更倾向于响应中断
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances.
     * The circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     * Lock实现可能能够检测锁的错误使用，例如可能导致死锁的调用，
     * 并且在这种情况下可能抛出（未经检查的）异常。Lock实现必须记录环境和异常类型。
     *
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @return {@code true} if the lock was acquired and {@code false}
     *         if the waiting time elapsed before the lock was acquired
     *
     * @throws InterruptedException if the current thread is interrupted
     *         while acquiring the lock (and interruption of lock
     *         acquisition is supported)
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Releases the lock.
     * 释放锁
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation will usually impose
     * restrictions on which thread can release a lock (typically only the
     * holder of the lock can release it) and may throw
     * an (unchecked) exception if the restriction is violated.
     * Any restrictions and the exception
     * type must be documented by that {@code Lock} implementation.
     * 
     * Lock 实现通常会对哪个线程可以释放锁施加限制（通常只有锁的持有者可以释放锁），
     * 并且如果违反了限制，可能会抛出（未经检查的）异常。 Lock 实现必须记录任何限制和异常类型。
     */
    void unlock();

    /**
     * Returns a new {@link Condition} instance that is bound to this
     * {@code Lock} instance.
     * 返回绑定到该Lock实例的一个新的Condition实例。
     *
     * <p>Before waiting on the condition the lock must be held by the
     * current thread.
     * A call to {@link Condition#await()} will atomically release the lock
     * before waiting and re-acquire the lock before the wait returns.
     * 在等待condition之前，锁必须由当前线程持有。对 Condition.await() 的调用将在等待之前自动释放锁，
     * 并在等待返回之前重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The exact operation of the {@link Condition} instance depends on
     * the {@code Lock} implementation and must be documented by that
     * implementation.
     * Condition 实例的确切操作取决于 Lock 实现，并且必须由该实现记录。
     *
     * @return A new {@link Condition} instance for this {@code Lock} instance
     * @throws UnsupportedOperationException if this {@code Lock}
     *         implementation does not support conditions
     */
    Condition newCondition();
}
```