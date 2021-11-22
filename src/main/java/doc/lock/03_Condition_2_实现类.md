##类ConditionObject，该类为AQS的内部类

```java

public class ConditionObject implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 1173984872572414699L;
    /** First node of condition queue. 
     * 这里也是用的AQS中的Node，这里只是用到Node中的nextWaiter，不是pre和next，所以是一个单项链表。
     * */
    private transient Node firstWaiter;
    /** Last node of condition queue. */
    private transient Node lastWaiter;

    /**
     * Creates a new {@code ConditionObject} instance.
     */
    public ConditionObject() { }

    /**
     * Adds a new waiter to wait queue.
     * 往等待队列中添加一个新的等待者。
     * @return its new wait node
     */
    private Node addConditionWaiter() {
        Node t = lastWaiter;
        // If lastWaiter is cancelled, clean out. 如果最后一个等待着已经被取消，则清除掉。
        if (t != null && t.waitStatus != Node.CONDITION) {
            unlinkCancelledWaiters();
            t = lastWaiter;
        }
        //这里就将waitStatus中的CONDITION状态给用到了
        Node node = new Node(Thread.currentThread(), Node.CONDITION);
        if (t == null)
            firstWaiter = node;
        else
            t.nextWaiter = node;
        lastWaiter = node;
        return node;
    }

    /**
     * Unlinks cancelled waiter nodes from condition queue.
     * Called only while holding lock. This is called when
     * cancellation occurred during condition wait, and upon
     * insertion of a new waiter when lastWaiter is seen to have
     * been cancelled. This method is needed to avoid garbage
     * retention in the absence of signals. So even though it may
     * require a full traversal, it comes into play only when
     * timeouts or cancellations occur in the absence of
     * signals. It traverses all nodes rather than stopping at a
     * particular target to unlink all pointers to garbage nodes
     * without requiring many re-traversals during cancellation
     * storms.
     * 从条件队列中将已取消的等待节点的 取消链接。只有在持有锁的时候调用。
     * 当在条件等待期间发生取消时，以及在插入新的等待者期间发现lastWaiter是取消的时候，调用此函数。
     * 需要这种方法来避免在没有信号的情况下垃圾保留。
     * 因此，即使它可能需要一个完整的遍历，它也只有在没有信号的情况下发生超时或取消才会起作用。
     * 它遍历所有节点，而不是在特定目标处停止，以取消所有指向垃圾节点的指针的链接，而无需在取消风暴期间多次重新遍历。
     * 就是从头到位遍历，将所有取消的节点剔除。
     */
    private void unlinkCancelledWaiters() {
        Node t = firstWaiter;
        Node trail = null;
        while (t != null) {
            Node next = t.nextWaiter;
            if (t.waitStatus != Node.CONDITION) {
                t.nextWaiter = null;
                if (trail == null)
                    firstWaiter = next;
                else
                    trail.nextWaiter = next;
                if (next == null)
                    lastWaiter = trail;
            }
            else
                trail = t;
            t = next;
        }
    }
    
    /**
     * Implements interruptible condition wait.
     * 实现可中断的条件等待。
     * <ol>
     * <li> If current thread is interrupted, throw InterruptedException.
     *      如果当前线程已经被中断，则抛出InterruptedException。
     *      
     * <li> Save lock state returned by {@link #getState}.
     *      getState方法返回保存的锁状态。
     *      
     * <li> Invoke {@link #release} with saved state as argument,
     *      throwing IllegalMonitorStateException if it fails.
     *      通过保存状态作为参数来调用release方法，如果失败则抛出IllegalMonitorStateException。
     *      
     * <li> Block until signalled or interrupted.
     *      阻塞至被信号通过或被中断。
     *      
     * <li> Reacquire by invoking specialized version of
     *      {@link #acquire} with saved state as argument.
     *      通过调用acquire的专用版本并将保存的状态作为参数来重新获取
     *      
     * <li> If interrupted while blocked in step 4, throw InterruptedException.
     *      如果在步骤4中阻塞时被中断，则抛出InterruptedException。
     * </ol>
     */
    public final void await() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        Node node = addConditionWaiter(); //创建一个condition类型的Node并入链
        int savedState = fullyRelease(node);//完全释放并返回当时的state
        int interruptMode = 0;
        while (!isOnSyncQueue(node)) {
            //fullyRelease后肯定不在AQS的同步队列中了，所以会立即进入该方法，后续该线程被park
            //其他线程调用signal时会将此线程unpark，也会将此线程添加到AQS的同步队列中去，结束wile循环。后面在细看signal
            LockSupport.park(this);
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                //阻塞的过程中被中断了，就直接break出来
                break;
        }
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
            //acquireQueued再次获取，如果获取到了就顺利执行完了，那什么时候会获取不到呢？
            //如果多个线程在await，然后被signalAll同时唤醒，最终也只能有一个线程有限获取成功，其它线程被park，
            // 然后获取成功的线程在后续unlock时通过AQS同步队列逻辑unparkSuccessor来逐个唤醒、逐个获取成功。
            interruptMode = REINTERRUPT;
        if (node.nextWaiter != null) // clean up if cancelled 清理被取消的节点
            unlinkCancelledWaiters();
        if (interruptMode != 0)
            reportInterruptAfterWait(interruptMode);
    }

    /**
     * Invokes release with current state value; returns saved state.
     * Cancels node and throws exception on failure.
     * 使用当前state值调用release；返回保存的state。失败时取消节点并抛出异常。
     * 
     * @param node the condition node for this wait
     * @return previous sync state
     */
    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            //为什么不是release（1）呢？因为如果是可重入的，比如说thread1锁了2次，这个saveState为2，
            //release(1)不会完全释放当前锁，所以这个方法叫做fullyRelease。
            //为什么又要返回这个savedState呢？ thread1锁了2次，await的时候会通过该方法释放掉，其它线程会再次获得锁，
            //那么等thread1被signal时，会重新acquire（savedState）获取锁，并将state的设置到正确状态，
            // thread1 lock了两次，肯定会unlock两次，如果state为1，第二次unlock时就会报错。
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }

    /** Mode meaning to reinterrupt on exit from wait */
    private static final int REINTERRUPT =  1;
    /** Mode meaning to throw InterruptedException on exit from wait */
    private static final int THROW_IE    = -1;
    
    /**
     * Checks for interrupt, returning THROW_IE if interrupted
     * before signalled, REINTERRUPT if after signalled, or
     * 0 if not interrupted.
     * 检查中断，如果再发出信号前被中断，则返回THROW_IE，发出信号后被中断则返回REINTERRUPT，
     * 没有被中断则返回0.
     */
    private int checkInterruptWhileWaiting(Node node) {
        return Thread.interrupted() ?
                (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
                0;
    }

    /**
     * Throws InterruptedException, reinterrupts current thread, or
     * does nothing, depending on mode.
     * 基于mode 来抛出InterruptedException、重新中断当前线程或者啥都不做。
     */
    private void reportInterruptAfterWait(int interruptMode)
            throws InterruptedException {
        if (interruptMode == THROW_IE)
            throw new InterruptedException();
        else if (interruptMode == REINTERRUPT)
            selfInterrupt();
    }
}
```