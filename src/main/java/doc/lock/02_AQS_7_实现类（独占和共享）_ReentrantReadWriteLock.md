
```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    abstract static class Sync extends AbstractQueuedSynchronizer {
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
         * 3. If step 2 fails either because thread
         *    apparently not eligible or CAS fails or count
         *    saturated, chain to version with full retry loop.
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
            //如果
            if (r == 0) {
                firstReader = current;
                firstReaderHoldCount = 1;
            } else if (firstReader == current) {
                firstReaderHoldCount++;
            } else {
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    cachedHoldCounter = rh = readHolds.get();
                else if (rh.count == 0)
                    readHolds.set(rh);
                rh.count++;
            }
            return 1;
        }
        return fullTryAcquireShared(current);
    }
}
```