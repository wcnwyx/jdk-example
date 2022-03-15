
```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    
}
```

###写锁的逻辑
```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {

    abstract static class Sync extends AbstractQueuedSynchronizer {
        /**
         * Performs tryLock for write, enabling barging in both modes.
         * This is identical in effect to tryAcquire except for lack
         * of calls to writerShouldBlock.
         * 执行写的tryLock操作，在两种模式下都有机会barging（乱撞）。
         * 这在效果上和tryAcquire一样，只是没有调用writerShouldBlock
         */
        final boolean tryWriteLock() {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c != 0) {//已有线程持有了该锁
                int w = exclusiveCount(c);//独占锁（write lock）上锁次数

                //1. w==0表示当前该锁被其它线程上了read lock，则返回false
                // （可以看出来，如果说一个线程自己先获取了读锁，在不释放读锁的情况下，即使是持有该读锁的线程自己也是无法获取写锁）
                //2. w!=0 && current!=getExclusiveOwnerThread() 说明该锁当前被其它线程持有write lock，但不是自己
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
            }
            //走到这里说明以下2种情况：
            //1: c==0 该读写锁没有被任何线程上任何锁
            //2: 已经被上了写锁，但是是自己上的锁，则本次可以继续上锁（重入）
            if (!compareAndSetState(c, c + 1))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }


        /**
         * 独占模式（写锁）的tryAcquire，和tryWriteLock基本一致
         * @param acquires
         * @return
         */
        protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {
                // (Note: if c != 0 and w == 0 then shared count != 0)
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }
            if (writerShouldBlock() ||
                    !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }
    }
}
```

```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    protected final int tryAcquireShared(int unused) {
        /*
         * Walkthrough:
         * 1. If write lock held by another thread, fail.
         * 如果其它线程持有写锁，则失败
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
            return -1;
        int r = sharedCount(c);
        if (!readerShouldBlock() &&
                r < MAX_COUNT &&
                compareAndSetState(c, c + SHARED_UNIT)) {
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