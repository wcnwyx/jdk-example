
```java
/**
 * An implementation of {@link ReadWriteLock} supporting similar
 * semantics to {@link ReentrantLock}.
 * ReadWriteLock的一个实现，支持类似于ReentrantLock的语义。
 * 
 * <p>This class has the following properties:
 * 该类还有以下属性：
 *
 * <ul>
 * <li><b>Acquisition order</b>
 * 有序获取
 *
 * <p>This class does not impose a reader or writer preference
 * ordering for lock access.  However, it does support an optional
 * <em>fairness</em> policy.
 * 此类不会特别强调写锁或者读锁的获取顺序。但是，确实支持一种公平策略。
 *
 * <dl>
 * <dt><b><i>Non-fair mode (default)</i></b>
 * <dd>When constructed as non-fair (the default), the order of entry
 * to the read and write lock is unspecified, subject to reentrancy
 * constraints.  A nonfair lock that is continuously contended may
 * indefinitely postpone one or more reader or writer threads, but
 * will normally have higher throughput than a fair lock.
 * 非公平模式（默认）
 * 当以非公平（默认）构建时，读写锁的获取顺序是未指定的，受约束于可重入。
 * 一个非公平锁的持续争用可能会无限期地延迟一个或多个读取/写入线程，
 * 但通常比公平锁具有更高的吞吐量。
 *
 * <dt><b><i>Fair mode</i></b>
 * <dd>When constructed as fair, threads contend for entry using an
 * approximately arrival-order policy. When the currently held lock
 * is released, either the longest-waiting single writer thread will
 * be assigned the write lock, or if there is a group of reader threads
 * waiting longer than all waiting writer threads, that group will be
 * assigned the read lock.
 * 
 *
 * <p>A thread that tries to acquire a fair read lock (non-reentrantly)
 * will block if either the write lock is held, or there is a waiting
 * writer thread. The thread will not acquire the read lock until
 * after the oldest currently waiting writer thread has acquired and
 * released the write lock. Of course, if a waiting writer abandons
 * its wait, leaving one or more reader threads as the longest waiters
 * in the queue with the write lock free, then those readers will be
 * assigned the read lock.
 *
 * <p>A thread that tries to acquire a fair write lock (non-reentrantly)
 * will block unless both the read lock and write lock are free (which
 * implies there are no waiting threads).  (Note that the non-blocking
 * {@link ReadLock#tryLock()} and {@link WriteLock#tryLock()} methods
 * do not honor this fair setting and will immediately acquire the lock
 * if it is possible, regardless of waiting threads.)
 * <p>
 * </dl>
 *
 * <li><b>Reentrancy</b>
 *
 * <p>This lock allows both readers and writers to reacquire read or
 * write locks in the style of a {@link ReentrantLock}. Non-reentrant
 * readers are not allowed until all write locks held by the writing
 * thread have been released.
 *
 * <p>Additionally, a writer can acquire the read lock, but not
 * vice-versa.  Among other applications, reentrancy can be useful
 * when write locks are held during calls or callbacks to methods that
 * perform reads under read locks.  If a reader tries to acquire the
 * write lock it will never succeed.
 *
 * <li><b>Lock downgrading</b>
 * <p>Reentrancy also allows downgrading from the write lock to a read lock,
 * by acquiring the write lock, then the read lock and then releasing the
 * write lock. However, upgrading from a read lock to the write lock is
 * <b>not</b> possible.
 *
 * <li><b>Interruption of lock acquisition</b>
 * <p>The read lock and write lock both support interruption during lock
 * acquisition.
 *
 * <li><b>{@link Condition} support</b>
 * <p>The write lock provides a {@link Condition} implementation that
 * behaves in the same way, with respect to the write lock, as the
 * {@link Condition} implementation provided by
 * {@link ReentrantLock#newCondition} does for {@link ReentrantLock}.
 * This {@link Condition} can, of course, only be used with the write lock.
 *
 * <p>The read lock does not support a {@link Condition} and
 * {@code readLock().newCondition()} throws
 * {@code UnsupportedOperationException}.
 *
 * <li><b>Instrumentation</b>
 * <p>This class supports methods to determine whether locks
 * are held or contended. These methods are designed for monitoring
 * system state, not for synchronization control.
 * </ul>
 *
 * <p>Serialization of this class behaves in the same way as built-in
 * locks: a deserialized lock is in the unlocked state, regardless of
 * its state when serialized.
 *
 * <p><b>Sample usages</b>. Here is a code sketch showing how to perform
 * lock downgrading after updating a cache (exception handling is
 * particularly tricky when handling multiple locks in a non-nested
 * fashion):
 *
 * <pre> {@code
 * class CachedData {
 *   Object data;
 *   volatile boolean cacheValid;
 *   final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *
 *   void processCachedData() {
 *     rwl.readLock().lock();
 *     if (!cacheValid) {
 *       // Must release read lock before acquiring write lock
 *       rwl.readLock().unlock();
 *       rwl.writeLock().lock();
 *       try {
 *         // Recheck state because another thread might have
 *         // acquired write lock and changed state before we did.
 *         if (!cacheValid) {
 *           data = ...
 *           cacheValid = true;
 *         }
 *         // Downgrade by acquiring read lock before releasing write lock
 *         rwl.readLock().lock();
 *       } finally {
 *         rwl.writeLock().unlock(); // Unlock write, still hold read
 *       }
 *     }
 *
 *     try {
 *       use(data);
 *     } finally {
 *       rwl.readLock().unlock();
 *     }
 *   }
 * }}</pre>
 *
 * ReentrantReadWriteLocks can be used to improve concurrency in some
 * uses of some kinds of Collections. This is typically worthwhile
 * only when the collections are expected to be large, accessed by
 * more reader threads than writer threads, and entail operations with
 * overhead that outweighs synchronization overhead. For example, here
 * is a class using a TreeMap that is expected to be large and
 * concurrently accessed.
 *
 *  <pre> {@code
 * class RWDictionary {
 *   private final Map<String, Data> m = new TreeMap<String, Data>();
 *   private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *   private final Lock r = rwl.readLock();
 *   private final Lock w = rwl.writeLock();
 *
 *   public Data get(String key) {
 *     r.lock();
 *     try { return m.get(key); }
 *     finally { r.unlock(); }
 *   }
 *   public String[] allKeys() {
 *     r.lock();
 *     try { return m.keySet().toArray(); }
 *     finally { r.unlock(); }
 *   }
 *   public Data put(String key, Data value) {
 *     w.lock();
 *     try { return m.put(key, value); }
 *     finally { w.unlock(); }
 *   }
 *   public void clear() {
 *     w.lock();
 *     try { m.clear(); }
 *     finally { w.unlock(); }
 *   }
 * }}</pre>
 *
 * <h3>Implementation Notes</h3>
 *
 * <p>This lock supports a maximum of 65535 recursive write locks
 * and 65535 read locks. Attempts to exceed these limits result in
 * {@link Error} throws from locking methods.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {

    /** Inner class providing readlock 内部类提供readLock*/
    private final ReentrantReadWriteLock.ReadLock readerLock;
    /** Inner class providing writelock 内部类提供writelock*/
    private final ReentrantReadWriteLock.WriteLock writerLock;
    /** Performs all synchronization mechanics 执行所有同步机制*/
    final Sync sync;

    /**
     * Creates a new {@code ReentrantReadWriteLock} with
     * default (nonfair) ordering properties.
     * 默认策略是非公平的
     */
    public ReentrantReadWriteLock() {
        this(false);
    }

    /**
     * Creates a new {@code ReentrantReadWriteLock} with
     * the given fairness policy.
     * 通过FairSync和NofairSync实现公平和非公平策略
     * 
     * @param fair {@code true} if this lock should use a fair ordering policy
     */
    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }

    public ReentrantReadWriteLock.WriteLock writeLock() { return writerLock; }
    public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }

    /**
     * Synchronization implementation for ReentrantReadWriteLock.
     * Subclassed into fair and nonfair versions.
     * ReentrantReadWriteLock的同步实现类.
     * 子类实现了公平和非公平版本。
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {

        /*
         * Read vs write count extraction constants and functions.
         * Lock state is logically divided into two unsigned shorts:
         * The lower one representing the exclusive (writer) lock hold count,
         * and the upper the shared (reader) hold count.
         * read和write计数提取常量和函数。锁状态在逻辑上分为两个无符号的short：
         * 较低的一个表示独占（写入）锁保持计数，较高的一个表示共享（读取）保持计数。
         */

        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        /** Returns the number of shared holds represented in count  */
        //返回以参数c 表示的共享锁保持数
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        /** Returns the number of exclusive holds represented in count  */
        //返回以计数参数c 表示的排它锁保持数
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }


        /**
         * A counter for per-thread read hold counts.
         * Maintained as a ThreadLocal; cached in cachedHoldCounter
         * 每个线程持有读锁的一个计数器。以ThreadLocal保持，缓存在cachedHoldCounter
         * 
         */
        static final class HoldCounter {
            int count = 0;
            // Use id, not reference, to avoid garbage retention
            final long tid = getThreadId(Thread.currentThread());
        }

        /**
         * ThreadLocal subclass. Easiest to explicitly define for sake
         * of deserialization mechanics.
         * ThreadLocal子类。为了反序列化机制，最容易明确定义。
         */
        static final class ThreadLocalHoldCounter
                extends ThreadLocal<HoldCounter> {
            public HoldCounter initialValue() {
                return new HoldCounter();
            }
        }

        /**
         * The number of reentrant read locks held by current thread.
         * Initialized only in constructor and readObject.
         * Removed whenever a thread's read hold count drops to 0.
         * 当前线程持有的可重入读取锁的数量。仅在构造函数和readObject中初始化。
         * 每当线程的读取保持计数降至0时，就会删除。
         */
        private transient ThreadLocalHoldCounter readHolds;

        /**
         * The hold count of the last thread to successfully acquire
         * readLock. This saves ThreadLocal lookup in the common case
         * where the next thread to release is the last one to
         * acquire. This is non-volatile since it is just used
         * as a heuristic, and would be great for threads to cache.
         * 成功获取readLock的最后一个线程的保持计数。
         * 在下一个要释放的线程是最后一个要获取的线程的常见情况下，这将节省ThreadLocal查找。
         * 这是非易失性的，因为它只是作为一种启发式使用，对于线程缓存来说是非常好的。
         *
         * <p>Can outlive the Thread for which it is caching the read
         * hold count, but avoids garbage retention by not retaining a
         * reference to the Thread.
         * 可以比正在缓存读取保持计数的线程更长寿，但通过不保留对该线程的引用来避免垃圾保留。
         *
         * <p>Accessed via a benign data race; relies on the memory
         * model's final field and out-of-thin-air guarantees.
         * 通过良性数据竞争访问；依赖于内存模型的final字段和out-of-thin-air保证。
         */
        private transient HoldCounter cachedHoldCounter;


        /**
         * firstReader is the first thread to have acquired the read lock.
         * firstReaderHoldCount is firstReader's hold count.
         * firstRead是第一个获取到读锁的线程。
         * firstReaderHoldCount是firstReader的保持计数。
         *
         * <p>More precisely, firstReader is the unique thread that last
         * changed the shared count from 0 to 1, and has not released the
         * read lock since then; null if there is no such thread.
         * 更准确地说，firstReader是唯一一个上次将共享计数从0更改为1的线程，
         * 并且从那时起没有释放读取锁；如果没有这样的线程，则为null。
         *
         * <p>Cannot cause garbage retention unless the thread terminated
         * without relinquishing its read locks, since tryReleaseShared
         * sets it to null.
         * 除非线程终止而不放弃其读锁，否则无法导致垃圾保留，因为tryReleaseShared将其设置为null。
         *
         * <p>Accessed via a benign data race; relies on the memory
         * model's out-of-thin-air guarantees for references.
         * 通过良性数据竞争访问；依赖于内存模型的final字段和out-of-thin-air保证。
         *
         * <p>This allows tracking of read holds for uncontended read
         * locks to be very cheap.
         * 这使得跟踪无争用读锁的read holds非常便宜。
         */
        private transient Thread firstReader = null;
        private transient int firstReaderHoldCount;
        
        /*
         * Acquires and releases use the same code for fair and
         * nonfair locks, but differ in whether/how they allow barging
         * when queues are non-empty.
         * 获取和释放对公平锁和非公平锁使用相同的代码，但在队列非空时是否/如何允许barging上有不同。
         */

        /**
         * Returns true if the current thread, when trying to acquire
         * the read lock, and otherwise eligible to do so, should block
         * because of policy for overtaking other waiting threads.
         * 如果当前线程在尝试获取读锁时，由于超越其他等待线程的策略而阻塞，
         * 并且有资格这样做，则返回true。
         * 就是说在获取读锁时，如果AQS队列里有排队的线程，是否要阻塞排队，
         * 还是说直接进行barging
         */
        abstract boolean readerShouldBlock();

        /**
         * Returns true if the current thread, when trying to acquire
         * the write lock, and otherwise eligible to do so, should block
         * because of policy for overtaking other waiting threads.
         * 获取写锁时，如果AQS队列里有排队的线程，是否要阻塞排队，还是直接barging
         */
        abstract boolean writerShouldBlock();
    }

    /**
     * Nonfair version of Sync
     * 非公平版本的Sync
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -8159625535654395037L;
        final boolean writerShouldBlock() {
            //非公平，write lock的获取不用去判断是否有其它线程在AQS队列中排队
            return false; // writers can always barge
        }
        final boolean readerShouldBlock() {
            //如果AQS队列的头部是一个排它锁（写锁），则需要阻塞等待
            return apparentlyFirstQueuedIsExclusive();
        }
    }

    /**
     * Fair version of Sync
     * 公平版本的Sync
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -2274990926593161451L;
        final boolean writerShouldBlock() {
            //如果有其他线程在自己前面排队，则返回true，自己也去排队，体现了公平性
            return hasQueuedPredecessors();
        }
        final boolean readerShouldBlock() {
            //如果有其他线程在自己前面排队，则返回true，自己也去排队，体现了公平性
            return hasQueuedPredecessors();
        }
    }
}
```

###写锁（排他锁）的逻辑
AQS的排他锁常用逻辑，实现一个内部实现类，实现tryAcquire、tryRelease方法
```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {

    abstract static class Sync extends AbstractQueuedSynchronizer {

        /**
         * 独占模式（写锁）的tryAcquire
         * @param acquires
         * @return
         */
        protected final boolean tryAcquire(int acquires) {
            /*
             * Walkthrough:
             * 1. If read count nonzero or write count nonzero
             *    and owner is a different thread, fail.
             *    如果 read数量不为0 或者 （write数量不为0且拥有者不是自己）， 返回fail
             * 
             * 2. If count would saturate, fail. (This can only
             *    happen if count is already nonzero.)
             *    如果计数会饱和，则失败（只有当count非0的时候）
             * 
             * 3. Otherwise, this thread is eligible for lock if
             *    it is either a reentrant acquire or
             *    queue policy allows it. If so, update state
             *    and set owner.
             *    否则，如果该线程是可重入获取或队列策略允许的，
             *    则该线程有资格被锁定。如果是，请更新状态并设置所有者。
             */
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);////独占锁（write lock）上锁次数
            if (c != 0) {//已有线程持有了该锁（不确定时read还是write）
                // (Note: if c != 0 and w == 0 then shared count != 0)
                //1. w==0表示当前该锁被其它线程上了read lock，则返回false
                // （可以看出来，如果说一个线程自己先获取了读锁，在不释放读锁的情况下，即使是持有该读锁的线程自己也是无法获取写锁）
                //2. w!=0 && current!=getExclusiveOwnerThread() 说明该锁当前被其它线程持有write lock，但不是自己
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    //大于最大锁数量限制，直接抛出异常
                    throw new Error("Maximum lock count exceeded");
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }

            //走到这里说明以下2种情况：
            //1: c==0 该读写锁没有被任何线程上任何锁
            //2: 已经被上了写锁，但是是自己上的锁，则本次可以继续上锁（重入）
            //这里就是会先判断是否要排队，如果要排队，不会直接尝试CAS修改状态
            if (writerShouldBlock() ||
                    !compareAndSetState(c, c + acquires))
                //这里CAS修改状态可能会失败的，比如说一上来两个线程同时来获取写锁，
                //上面的c执行的时候都时0，同时来cas，只能一个成功，一个失败。
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }
    }

    /*
     * Note that tryRelease and tryAcquire can be called by
     * Conditions. So it is possible that their arguments contain
     * both read and write holds that are all released during a
     * condition wait and re-established in tryAcquire.
     * 请注意，tryRelease和tryAcquire可以通过Conditions调用。
     * 因此，它们的参数可能同时包含读保持和写保持，它们都在条件等待期间释放，
     * 并在tryAcquire中重新建立。
     */

    protected final boolean tryRelease(int releases) {
        if (!isHeldExclusively())
            //如果不是当前线程以独占模式（write lock）持有，则抛出异常
            throw new IllegalMonitorStateException();
        int nextc = getState() - releases;
        boolean free = exclusiveCount(nextc) == 0;
        if (free)
            //如果减去当前releases之后为0了，说明以经完全释放了该独占锁
            setExclusiveOwnerThread(null);
        setState(nextc);
        return free;
    }
    
    /**
     * Performs tryLock for write, enabling barging in both modes.
     * This is identical in effect to tryAcquire except for lack
     * of calls to writerShouldBlock.
     * 执行写的tryLock操作，在两种模式（公平和非公平）下都有机会barging（乱撞）。
     * 这在效果上和tryAcquire一样，只是没有调用writerShouldBlock
     */
    final boolean tryWriteLock() {
        Thread current = Thread.currentThread();
        int c = getState();
        if (c != 0) {
            int w = exclusiveCount(c);
            if (w == 0 || current != getExclusiveOwnerThread())
                return false;
            if (w == MAX_COUNT)
                throw new Error("Maximum lock count exceeded");
        }
        if (!compareAndSetState(c, c + 1))
            return false;
        setExclusiveOwnerThread(current);
        return true;
    }
}
```
写锁的获取逻辑：
1. 如果当前以经有读锁存在，则不能获取（lock可以去排队，trylock就直接返回失败）
2. 如果当前已经有写锁存在，并且不是自己，则去排队或直接失败
3. 如果当前已经有写锁存在，且是自己的线程，则重入。


###写锁（共享锁）的逻辑
AQS的共享锁常用逻辑，实现一个内部实现类，实现tryAcquireShared、tryReleaseShared方法
```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    
    protected final int tryAcquireShared(int unused) {
        /*
         * Walkthrough:
         * 1. If write lock held by another thread, fail.
         *    如果其它线程持有写锁，则失败
         * 
         * 2. Otherwise, this thread is eligible for
         *    lock wrt state, so ask if it should block
         *    because of queue policy. If not, try
         *    to grant by CASing state and updating count.
         *    Note that step does not check for reentrant
         *    acquires, which is postponed to full version
         *    to avoid having to check hold count in
         *    the more typical non-reentrant case.
         *    否则，该线程符合锁定wrt状态的条件，因此询问它是否应该因为队列策略而阻塞。
         *    如果没有，请尝试通过CAS写状态和更新计数来授予。
         *    请注意，该步骤不检查可重入获取，它被推迟到完整版本，
         *    以避免在更典型的不可重入情况下必须检查保持计数。
         * 3. If step 2 fails either because thread
         *    apparently not eligible or CAS fails or count
         *    saturated, chain to version with full retry loop.
         *    如果第2步失败，或者是因为线程明显不合格，或者CAS失败，
         *    或者是因为计数饱和，那么通过完整的重试循环链接到版本。
         */
        Thread current = Thread.currentThread();
        int c = getState();
        if (exclusiveCount(c) != 0 &&
                getExclusiveOwnerThread() != current)
            //有排他锁，且不是自己持有，则返回失败（-1）
            return -1;
        int r = sharedCount(c);//共享锁上锁次数
        if (!readerShouldBlock() &&
                r < MAX_COUNT &&
                compareAndSetState(c, c + SHARED_UNIT)) {
            //状态CAS修改成功则表明成功获取锁
            if (r == 0) {
                //r==0表示是第一个获取读锁的线程，记录到firstReader上
                firstReader = current;
                firstReaderHoldCount = 1;
            } else if (firstReader == current) {
                //虽然r不是0了，但是第一个获取读锁的就是自己，
                // 再次重入读锁，持有计数增加
                firstReaderHoldCount++;
            } else {
                //最后一次获取到读锁的线程保存在cachedHoldCounter中
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    cachedHoldCounter = rh = readHolds.get();
                else if (rh.count == 0)
                    readHolds.set(rh);
                rh.count++;
            }
            return 1;
        }
        
        //走到这里可能是因为多个读锁竞争CAS失败
        return fullTryAcquireShared(current);
    }


    /**
     * Full version of acquire for reads, that handles CAS misses
     * and reentrant reads not dealt with in tryAcquireShared.
     * 获取读锁的完全版本，用于处理未在tryAcquireShared中处理的CAS未命中和可重入读取。
     */
    final int fullTryAcquireShared(Thread current) {
        /*
         * This code is in part redundant with that in
         * tryAcquireShared but is simpler overall by not
         * complicating tryAcquireShared with interactions between
         * retries and lazily reading hold counts.
         * 该代码部分与tryAcquireShared中的代码冗余，但总体上更简单，
         * 因为它不会使tryAcquireShared与重试和延迟读取保持计数之间的交互复杂化。
         */
        HoldCounter rh = null;
        for (;;) {
            //循环处理，防止竞争CAS失败
            int c = getState();
            if (exclusiveCount(c) != 0) {
                if (getExclusiveOwnerThread() != current)
                    //如果有排他锁且不是自己，则返回失败
                    return -1;
                // else we hold the exclusive lock; blocking here
                // would cause deadlock.
                //else 有排他锁并且是自己持有，阻塞在此处的话可能引起死锁
            } else if (readerShouldBlock()) {
                // Make sure we're not acquiring read lock reentrantly
                //确保我们没有以可重入方式获取读锁
                if (firstReader == current) {
                    // assert firstReaderHoldCount > 0;
                } else {
                    if (rh == null) {
                        rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current)) {
                            rh = readHolds.get();
                            if (rh.count == 0)
                                readHolds.remove();
                        }
                    }
                    if (rh.count == 0)
                        return -1;
                }
            }
            if (sharedCount(c) == MAX_COUNT)
                throw new Error("Maximum lock count exceeded");
            
            //和tryAcquireShared的逻辑一样
            if (compareAndSetState(c, c + SHARED_UNIT)) {
                if (sharedCount(c) == 0) {
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {
                    firstReaderHoldCount++;
                } else {
                    if (rh == null)
                        rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current))
                        rh = readHolds.get();
                    else if (rh.count == 0)
                        readHolds.set(rh);
                    rh.count++;
                    cachedHoldCounter = rh; // cache for release
                }
                return 1;
            }
        }
    }

    protected final boolean tryReleaseShared(int unused) {
        Thread current = Thread.currentThread();
        if (firstReader == current) {
            // assert firstReaderHoldCount > 0;
            if (firstReaderHoldCount == 1)
                //如果第一个持有读锁的线程是自己，并且这是最后一次释放
                firstReader = null;
            else
                firstReaderHoldCount--;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                rh = readHolds.get();
            int count = rh.count;
            if (count <= 1) {
                //已经完全释放，则从ThreadLocal中移除
                readHolds.remove();
                if (count <= 0)
                    throw unmatchedUnlockException();
            }
            --rh.count;
        }
        for (;;) {
            int c = getState();
            int nextc = c - SHARED_UNIT;
            //CAS修改状态
            if (compareAndSetState(c, nextc))
                // Releasing the read lock has no effect on readers,
                // but it may allow waiting writers to proceed if
                // both read and write locks are now free.
                return nextc == 0;
        }
    }
}
```
读锁的获取逻辑：
1. 有写锁存在，并且不是自己持有，则返回失败，去AQS队列中排队。
2. 有写锁存在，但是是自己持有的，则获取成功。
3. 没有写锁存在，则获取成功。