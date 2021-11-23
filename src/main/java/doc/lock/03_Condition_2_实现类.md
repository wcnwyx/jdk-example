##一：类ConditionObject，基础操作和内部结构

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
}
```
总结：
1. 此类中也是使用Node来维护了一个单向链表，维护所有等待该Condition的线程队列，和AQS中的同步队列不是一个东西哦。
2. 此类中的Node的waitStatus都是Condition，表示等待状态。
3. 两个基本的操作，添加新节点和删除取消的节点。


##二：await逻辑
```java
public class ConditionObject implements Condition, java.io.Serializable {
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
        //判断是否在同步队列中，不在表示还未signal，在了表示已经被signal了
        while (!isOnSyncQueue(node)) {
            //1. fullyRelease后node不在AQS的同步队列中了，所以会立即进入该方法，后续该线程被park
            //其他线程调用signal时会将此线程unpark，也会将此线程添加到AQS的同步队列中去，结束wile循环。后面在看signal。
            //2. 会不会此时已经在AQS的同步队列中了呢，应该也是会的，就是此线程刚fullyRelease，
            // 其它线程signal的时候又将该node给加到了同步队列中，此时就不park了，直接往后执行了。
            //3. 如果park之前被中断了，那么此处的park会不起作用，直接往下继续执行。
            LockSupport.park(this);
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                //阻塞的过程中被中断了，就直接break出来
                break;
        }
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
            //acquireQueued再次获取，如果获取到了就继续执行，那什么时候会获取不到呢？
            //如果多个线程在await，然后被signalAll同时唤醒，最终也只能有一个线程优先获取成功，其它线程acquireQueued时被再次park，
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
            //release(1)不会完全释放当前锁，所以这个方法叫做fullyRelease，完全释放。
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
    //该模式意味着退出等待时重新中断
    private static final int REINTERRUPT =  1;
    /** Mode meaning to throw InterruptedException on exit from wait */
    //模式意味着退出等待时抛出InterruptedException
    private static final int THROW_IE    = -1;
    
    /**
     * Checks for interrupt, returning THROW_IE if interrupted
     * before signalled, REINTERRUPT if after signalled, or
     * 0 if not interrupted.
     * 检查中断，
     *  如果在发出信号前被中断，则返回THROW_IE，
     *  发出信号后被中断则返回REINTERRUPT，
     *  没有被中断则返回0.
     */
    private int checkInterruptWhileWaiting(Node node) {
        return Thread.interrupted() ?
                (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
                0;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     * 如有必要，在取消等待后将节点传输到同步队列。如果线程在发出信号之前被取消，则返回true。
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            //场景：一个线程在await，其它线程一直没有对其进行signal，然后该线程又被interrupt了。
            // await进行park前，已经将锁给释放掉了，中断后会从park中醒过来继续acquireQueued获取锁，
            //acquireQueued操作的是同步队列中的node，所以需要将node加到AQS的同步队列中去。
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq().  Cancelling during an
         * incomplete transfer is both rare and transient, so just
         * spin.
         * 如果我们输给了一个signal(),知道它完成enq()否则我们无法继续。
         * 在不完全传输过程中的取消既罕见又短暂，所以只需旋转即可。
         * 
         * 啥意思呢？ signal的第一步就会将node的状态从CONDITION改为0，那么该方法上面的CAS操作将会失败，
         * 出现的原因是因为interrupt和signal方法几乎同时执行，但是signal的CAS操作比此方法的CAS操作快了一步。
         */
        while (!isOnSyncQueue(node))
            // 循环一下等待node在signal中被enq到同步队列中去。
            Thread.yield();
        return false;
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
总结：
1. 正常的等待逻辑比较直观，分为以下几步（一个线程await，一个线程signal）：
    - 1.0 lock()获取锁、await()等待。
    - 1.1 addConditionWaiter创建一个Condition状态的节点并加入到队列。
    - 1.2 fullyRelease 将锁释放，并保存好AQS中的状态（savedState）。
    - 1.3 park线程，等待其它线程signal通知。
    - 1.4 其它线程调用signal，以及unlock，等待线程从park中唤醒
    - 1.5 acquireQueued 重新获取锁，并将1.2中的savedState状态从新设置到AQS的state字段。
    - 1.6 执行业务逻辑，最后释放锁 unlock()。
   
2. 中断发生，并且发生在signal之前（一个线程await，一个线程signal）：
    - 2.1 发生在park之前，那么park不起作用，直接往后执行。
    - 2.2 发生在park之后，线程从park中唤醒。
    - 2.3 transferAfterCancelledWait 将node的状态成功的从CONDITION改为0，并且将node添加到AQS的同步队列。
    - 2.4 上一步可能将node的状态从CONDITION改为0失败，表示signal已经发生了，这时算作中断发生在signal之后。
    - 2.5 acquireQueued 重新获取锁，并将savedState状态重新设置到AQS的state字段。
    - 2.6 reportInterruptAfterWait 将抛出InterruptedException。
   
3. 中断发生，并且发生在signal之后
    - 3.1 其它线程调用signal，以及unlock，等待线程从park中唤醒
    - 3.2 acquireQueued 重新获取锁，并将savedState状态重新设置到AQS的state字段。
    - 3.3 reportInterruptAfterWait 将重新执行Thread.interrupt().

4. 多个线程await，被多次signal，每次signal则唤醒一个await的线程。
5. 多个线程await，被signalAll同时唤醒，则会在acquireQueued 重新获取锁的时候再次park，然后通过AQS的release逐个唤醒。

##三：signal逻辑
```java
public class ConditionObject implements Condition, java.io.Serializable {
    /**
     * Moves the longest-waiting thread, if one exists, from the
     * wait queue for this condition to the wait queue for the
     * owning lock.
     * 将等待时间最长的线程（如果存在）从该条件的等待队列移动到拥有锁的等待队列。
     * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
     *         returns {@code false}
     */
    public final void signal() {
        if (!isHeldExclusively())
            //如果不是当前线程获得的锁，则抛出异常
            throw new IllegalMonitorStateException();
        Node first = firstWaiter;//firstWaiter表示等待时间最长的节点。
        if (first != null)
            doSignal(first);
    }

    /**
     * Removes and transfers nodes until hit non-cancelled one or
     * null. Split out from signal in part to encourage compilers
     * to inline the case of no waiters.
     * 删除和传输节点，直到命中未取消的一个node或null。
     * 从signal中分离出来，部分是为了鼓励编译器在没有等待者的情况下inline。
     * @param first (non-null) the first node on condition queue
     */
    private void doSignal(Node first) {
        do {
            //将first的下个节点作为新的first
            if ( (firstWaiter = first.nextWaiter) == null)
                lastWaiter = null;
            first.nextWaiter = null;
        } while (!transferForSignal(first) &&
                (first = firstWaiter) != null);//如果转换失败，继续循环，继续转换下一个节点
    }

    /**
     * Transfers a node from a condition queue onto sync queue.
     * Returns true if successful.
     * 将节点从等待队列传输到同步队列。成功则返回true。
     * @param node the node
     * @return true if successfully transferred (else the node was
     * cancelled before signal)
     */
    final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         * 如果不能改变waitStatus，则该node已经被取消了。
         * 因为await的时候，如果发生interrupt，则会将此状态设置为0，表示已经取消等待。
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong).
         * 拼接到队列上，并尝试设置前置线程的waitStatus，以指示线程（可能）正在等待。
         * 如果已取消或尝试设置waitStatus失败，则唤醒以重新同步（在这种情况下，waitStatus可能会暂时错误，且不会造成伤害）
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))//何时会导致这种情况呢？
            LockSupport.unpark(node.thread);
        return true;
    }

   /**
    * Moves all threads from the wait queue for this condition to
    * the wait queue for the owning lock.
    * 将此条件的所有线程从等待队列移动到拥有锁的等待队列。
    */
   public final void signalAll() {
      if (!isHeldExclusively())
         throw new IllegalMonitorStateException();
      Node first = firstWaiter;
      if (first != null)
         doSignalAll(first);
   }

   /**
    * Removes and transfers all nodes.
    * @param first (non-null) the first node on condition queue
    */
   private void doSignalAll(Node first) {
      lastWaiter = firstWaiter = null;
      do {
          //循环所有的等待节点，依次transferForSignal
         Node next = first.nextWaiter;
         first.nextWaiter = null;
         transferForSignal(first);
         first = next;
      } while (first != null);
   }
}
```

总结：
1. signal 单个唤醒，则将第一个节点firstWaiter状态从CONDITION改为0，并加等待队列转移到同步队列中。
2. signalAll 则是从第一个循环到最后一个，都处理一遍。
3. 这里的signal并不会直接将等待节点的线程unpark，而是加到了同步队列，等通知者线程unlock了，release逻辑会将等待着线程unpark。