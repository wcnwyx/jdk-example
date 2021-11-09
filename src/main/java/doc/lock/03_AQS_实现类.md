##实现类一： AQS注释中的一个实现类Mutex
```java
/**
 * Here is a non-reentrant mutual exclusion lock class that uses
 * the value zero to represent the unlocked state, and one to
 * represent the locked state. While a non-reentrant lock
 * does not strictly require recording of the current owner
 * thread, this class does so anyway to make usage easier to monitor.
 * It also supports conditions and exposes
 * one of the instrumentation methods。
 * 这是一个不可重入的互斥锁类，使用0表示未锁定状态，使用1表示锁定状态。
 * 虽然不可重入的锁并不严格要求记录当前所有者线程，但该类仍然这样做，以便更容易监视使用情况。
 * 它还支持条件（conditions）并公开其中一种检测方法。
 **/
class Mutex implements Lock, java.io.Serializable {

    // 内部的帮助类，AbstractQueuedSynchronizer的子类
    private static class Sync extends AbstractQueuedSynchronizer {
        // Reports whether in locked state 报告是否处于锁定状态
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // Acquires the lock if state is zero
        //如果状态为0则获取锁  
        public boolean tryAcquire(int acquires) {
            assert acquires == 1; // Otherwise unused
            if (compareAndSetState(0, 1)) {//CAS操作将state从0修改为1
                setExclusiveOwnerThread(Thread.currentThread());//记录当前线程
                return true;
            }
            return false;
        }

        // Releases the lock by setting state to zero
        //通过将state设置为0来释放锁  
        protected boolean tryRelease(int releases) {
            assert releases == 1; // Otherwise unused
            if (getState() == 0) throw new IllegalMonitorStateException();//如果当前状态是0，表示没有锁定，则抛出异常
            setExclusiveOwnerThread(null);//当前持有锁的线程设为null
            setState(0);//状态更新为0
            return true;
        }

        // The sync object does all the hard work. We just forward to it.
        private final Sync sync = new Sync();

        public boolean tryLock() {
            return sync.tryAcquire(1);
        }

        public void unlock() {
            sync.release(1);
        }
    }
}
```
Mutex总结：  
1. 通过state来表示锁定和未锁定状态，0为未锁定，1为锁定状态。
2. Mutex为Lock接口实现，所以有tryLock方法和unlock方法。
    2.1 tryLock方法通过syn.tryAcquire(1)方法来获取锁，将state通过ACS方法从0改为1即可。
    2.2 unlock方法通过syn.release(1)方法来释放，最终调用tryRelease(1)将state设置为0。
3. Nutex是不可重入的，因为一个线程再第一次获取锁后，第二次再去获取锁时，此时state已经为1，tryAcquire方法中将state通过ACS方法从0改为1就会失败。
4. 问题：这里其实只是体现出了state的用法，但是AQS里面的队列其实没有体现出来，下面继续看。

