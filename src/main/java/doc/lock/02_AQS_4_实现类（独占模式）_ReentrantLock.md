##一：ReentrantLock介绍
可重入的锁，使用AQS来实现。  
```java
/**
 * A reentrant mutual exclusion {@link Lock} with the same basic
 * behavior and semantics as the implicit monitor lock accessed using
 * {@code synchronized} methods and statements, but with extended
 * capabilities.
 * 一个可重入的互斥的Lock，其基本行为和语义与使用synchronized方法和语句访问的隐式监视锁相同，
 * 但具有扩展功能。
 *
 * <p>A {@code ReentrantLock} is <em>owned</em> by the thread last
 * successfully locking, but not yet unlocking it. A thread invoking
 * {@code lock} will return, successfully acquiring the lock, when
 * the lock is not owned by another thread. The method will return
 * immediately if the current thread already owns the lock. This can
 * be checked using methods {@link #isHeldByCurrentThread}, and {@link
 * #getHoldCount}.
 * ReentrantLock被上次成功锁定并未解锁的线程持有。
 * 当锁不属于另一个线程时，调用lock的线程将返回并成功获取锁。
 * 如果当前线程已经拥有锁，该方法将立即返回（这里就体现了可重入，可以多次lock）。
 * 可以使用方法isHeldByCurrentThread和getHoldCount来检查这一点。
 *
 * <p>The constructor for this class accepts an optional
 * <em>fairness</em> parameter.  When set {@code true}, under
 * contention, locks favor granting access to the longest-waiting
 * thread.  Otherwise this lock does not guarantee any particular
 * access order.  Programs using fair locks accessed by many threads
 * may display lower overall throughput (i.e., are slower; often much
 * slower) than those using the default setting, but have smaller
 * variances in times to obtain locks and guarantee lack of
 * starvation. Note however, that fairness of locks does not guarantee
 * fairness of thread scheduling. Thus, one of many threads using a
 * fair lock may obtain it multiple times in succession while other
 * active threads are not progressing and not currently holding the
 * lock.
 * Also note that the untimed {@link #tryLock()} method does not
 * honor the fairness setting. It will succeed if the lock
 * is available even if other threads are waiting.
 * 
 * 此类的构造方法接收一个fairness的参数。当设置true是，在争用的情况下，
 * 锁倾向于授予对等待时间最长的线程的访问权。否则，此锁不保证任何特定的访问顺序。
 * 与使用默认设置的程序相比，使用由多个线程访问的公平锁的程序可能显示较低的总体吞吐量（即较慢；通常较慢），
 * 但在获得锁和保证无饥饿的时间上差异较小。但是请注意，锁的公平性并不能保证线程调度的公平性。
 * 因此，使用公平锁的多个线程中的一个线程可能会连续多次获得公平锁，而其他活动线程则没有进行，并且当前没有持有该锁。
 * 还要注意，不定期的tryLock（）方法不支持公平性设置。如果锁可用，即使其他线程正在等待，它也会成功。
 *
 * <p>It is recommended practice to <em>always</em> immediately
 * follow a call to {@code lock} with a {@code try} block, most
 * typically in a before/after construction such as:
 * 建议在调用lock 后立即使用 try 块，最典型的是在构建之前/之后，例如：
 *
 *  <pre> {@code
 * class X {
 *   private final ReentrantLock lock = new ReentrantLock();
 *   // ...
 *
 *   public void m() {
 *     lock.lock();  // block until condition holds
 *     try {
 *       // ... method body
 *     } finally {
 *       lock.unlock()
 *     }
 *   }
 * }}</pre>
 *
 * <p>In addition to implementing the {@link Lock} interface, this
 * class defines a number of {@code public} and {@code protected}
 * methods for inspecting the state of the lock.  Some of these
 * methods are only useful for instrumentation and monitoring.
 * 除了实现 Lock 接口之外，这个类还定义了许多 public 和 protected 方法来检查锁的状态。
 * 其中一些方法仅适用于仪表和监测。
 *
 * <p>Serialization of this class behaves in the same way as built-in
 * locks: a deserialized lock is in the unlocked state, regardless of
 * its state when serialized.
 * 此类的序列化与内置锁的行为相同：反序列化的锁处于解锁状态，而不管序列化时的状态如何。
 *
 * <p>This lock supports a maximum of 2147483647 recursive locks by
 * the same thread. Attempts to exceed this limit result in
 * {@link Error} throws from locking methods.
 * 此锁支持同一线程最多2147483647个递归锁。试图超过此限制将导致锁方法抛出 Error。
 * 因为AQS的state属性是一个int值
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    /** Synchronizer providing all implementation mechanics */
    //提供所有同步器的实现机制，就是AQS的一个内部是实现类。
    private final Sync sync;

    /**
     * Creates an instance of {@code ReentrantLock}.
     * This is equivalent to using {@code ReentrantLock(false)}.
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    /**
     * Creates an instance of {@code ReentrantLock} with the
     * given fairness policy.
     * 使用给定的公平策略创建ReentrantLock。
     *
     * @param fair {@code true} if this lock should use a fair ordering policy
     *                         如果锁需要使用公平的排序策略
     */
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    /**
     * Acquires the lock.
     * 获取锁
     *
     * <p>Acquires the lock if it is not held by another thread and returns
     * immediately, setting the lock hold count to one.
     * 如果锁没有被其它线程持有，则立即返回并获得锁，设置锁的持有数为1.
     *
     * <p>If the current thread already holds the lock then the hold
     * count is incremented by one and the method returns immediately.
     * 如果当前线程已经持有该锁，则所得持有数加1，并且会立即返回。
     *
     * <p>If the lock is held by another thread then the
     * current thread becomes disabled for thread scheduling
     * purposes and lies dormant until the lock has been acquired,
     * at which time the lock hold count is set to one.
     * 如果锁由另一个线程持有，则当前线程出于线程调度目的将被禁用，并处于休眠状态，
     * 直到获得锁为止，此时锁持有计数设置为1。
     */
    public void lock() {
        sync.lock();
    }

    /**
     * Acquires the lock only if it is not held by another thread at the time
     * of invocation.
     * 只有在调用时锁没有被其它线程持有才会获取成功。
     *
     * <p>Acquires the lock if it is not held by another thread and
     * returns immediately with the value {@code true}, setting the
     * lock hold count to one. Even when this lock has been set to use a
     * fair ordering policy, a call to {@code tryLock()} <em>will</em>
     * immediately acquire the lock if it is available, whether or not
     * other threads are currently waiting for the lock.
     * This &quot;barging&quot; behavior can be useful in certain
     * circumstances, even though it breaks fairness. If you want to honor
     * the fairness setting for this lock, then use
     * {@link #tryLock(long, TimeUnit) tryLock(0, TimeUnit.SECONDS) }
     * which is almost equivalent (it also detects interruption).
     * 如果未被其它线程持有则立即返回true，成功获得所并将持有数设置为1.
     * 如果锁当前可用，即使该锁被设定为使用公平的排序策略，不管当前是否有其它线程在等待该锁，
     * 调用tryLock也会立即获得所。这种 barging（乱闯）的行为在某些情况下还是有用的，即使它破坏了公平性。
     * 如果你想要准守此锁的公平性设置，请使用tryLock(0, TimeUnit.SECONDS)，这几乎是等效的（它还检测中断）
     *
     * <p>If the current thread already holds this lock then the hold
     * count is incremented by one and the method returns {@code true}.
     * 如果当前线程已经持有该锁，则持有数加1并且返回true。
     *
     * <p>If the lock is held by another thread then this method will return
     * immediately with the value {@code false}.
     * 如果该锁已被其它线程持有，则立即返回false。
     *
     * @return {@code true} if the lock was free and was acquired by the
     *         current thread, or the lock was already held by the current
     *         thread; and {@code false} otherwise
     */
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);//调用的是nonfairTryAcquire，非公平的
    }

    /**
     * Attempts to release this lock.
     * 试图释放该锁。
     *
     * <p>If the current thread is the holder of this lock then the hold
     * count is decremented.  If the hold count is now zero then the lock
     * is released.  If the current thread is not the holder of this
     * lock then {@link IllegalMonitorStateException} is thrown.
     * 如果当前线程是该lock的持有者，则持有数减将减少。如果现在持有数是0则表示该锁已经释放。
     * 如果当前线程不是该lock的持有者则抛出IllegalMonitorStateException
     *
     * @throws IllegalMonitorStateException if the current thread does not
     *         hold this lock
     */
    public void unlock() {
        sync.release(1);
    }
}
```
总结：
1. ReentrantLock是一个和synchronized拥有相同行为和语义的可重入的互斥锁，但是又额外的功能。
2. 因为实现了Lock接口，所以拥有lock、tryLock、unlock等方法，但是这些方法都是通过内部AQS实现类实现的，后面细看。
3. ReentrantLock支持公平和非公平两种模式,默认使用的是非公平模式。但是即使在公平的模式下，tryLock（）也会存在barging（乱撞）状况打破公平性。
4. tryLock方法就体现出了AQS里的barging现象，barging可以提高吞吐量，为什么呢？如果说目前有多个线程再排队等待获取锁，然后锁释放了，
  第一个等待的线程（thread1）被unpark并再次尝试获取锁，然后另外一个线程（thread20）第一次来获取锁执行tryLock，thread1虽然被unPark,
  但是不一定拥有cpu资源，或许不会立即执行，但是现在thread20正在执行，其拥有cpu资源，所以让thread20立即获得所，让thread1等一下反而会更好的利用cpu，
  反之，需要thread20让出cpu资源，thread1再等待获得cpu资源才可以获得所，浪费了cpu资源。

##二：ReentrantLock中AQS的逻辑
```java
public class ReentrantLock implements Lock, java.io.Serializable {
    /** Synchronizer providing all implementation mechanics */
    private final Sync sync;

    /**
     * Base of synchronization control for this lock. Subclassed
     * into fair and nonfair versions below. Uses AQS state to
     * represent the number of holds on the lock.
     * 作为该lock的同步控制基础。 子类有一下公平和非公平的版本。
     * 使用AQS的state字段来表示该锁上的保持数。
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        /**
         * Performs {@link Lock#lock}. The main reason for subclassing
         * is to allow fast path for nonfair version.
         * 子类化的主要原因是允许非公平版本的快速通道。
         */
        abstract void lock();

        /**
         * Performs non-fair tryLock.  tryAcquire is implemented in
         * subclasses, but both need nonfair try for trylock method.
         * 执行非公平的tryLock. tryAcquire是在子类中实现的，但是tryAcquire和tryLock都需要该方法（进行非公平的尝试）。
         * 
         * 问题思考：如果此处不要该方法，而是直接在子类的NonfairSync.tryAcquire方法里写该逻辑，和FairSync保持结构一致，整体代码风格不是更规整易读吗？
         * 因为ReentrantLock.tryLock方法的定义，tryLock方法明确表明是非公平的，支持barging现象的，所以只能调用非公平的tryAcquire实现，
         * 如果写在NonfairSync中，sync在构造方法中定义的是FairSync，就调用不到NonfairSync中的非公平版本的tryAcquire了。
         */
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                //非公平性就体现在这里，不管是否有其它线程再排队，上来就改状态，就是横
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                //这里就体现了可重入性，如果当前线程已经持有了该lock，则将state+1，并返回true
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }

        protected final boolean tryRelease(int releases) {
            //这里也体现了可重入性，释放时先将state减1，如果减之后为0了，则表示释放掉了，如果不为零，则表示继续持有该锁。
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
    }

    /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         * 执行lock。尝试立即barge（不管有没有其它线程再等待，上来就改状态，体现了非公平性），失败时再次调用acquire去排队。
         */
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }

        /**
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                //这里就体现了公平性，如果没有线程在排队，才进行修改状态。
                if (!hasQueuedPredecessors() &&
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                //这里就体现了可重入行，如果当前线程已经持有了该lock，则将state+1，并返回true
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        //通过AQS来实现
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    public void lockInterruptibly() throws InterruptedException {
        //通过AQS来实现
        sync.acquireInterruptibly(1);
    }
}
```
总结：
1. 因为ReentrantLock支持公平和非公平，所有AQS的实现也有两个版本。
2. 此类中，就将AQS的state属性 不仅用来表示是否被锁定，还表示锁定的次数，也实现了可重入性功能。
3. 公平和非公平的实现原理就是在tryAcquire方法中，如果状态是0，需不需要先判断是否有线程在排队，然后再去CAS修改状态。