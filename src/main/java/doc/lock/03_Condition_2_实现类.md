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
     * <ol>
     * <li> If current thread is interrupted, throw InterruptedException.
     * <li> Save lock state returned by {@link #getState}.
     * <li> Invoke {@link #release} with saved state as argument,
     *      throwing IllegalMonitorStateException if it fails.
     * <li> Block until signalled or interrupted.
     * <li> Reacquire by invoking specialized version of
     *      {@link #acquire} with saved state as argument.
     * <li> If interrupted while blocked in step 4, throw InterruptedException.
     * </ol>
     */
    public final void await() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        Node node = addConditionWaiter();
        int savedState = fullyRelease(node);
        int interruptMode = 0;
        while (!isOnSyncQueue(node)) {
            LockSupport.park(this);
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                break;
        }
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
            interruptMode = REINTERRUPT;
        if (node.nextWaiter != null) // clean up if cancelled
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
}
```