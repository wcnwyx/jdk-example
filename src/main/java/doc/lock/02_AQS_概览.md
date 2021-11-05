##一：顶层抽象父类AbstractOwnableSynchronizer
```java
/**
 * A synchronizer that may be exclusively owned by a thread.  This
 * class provides a basis for creating locks and related synchronizers
 * that may entail a notion of ownership.  The
 * {@code AbstractOwnableSynchronizer} class itself does not manage or
 * use this information. However, subclasses and tools may use
 * appropriately maintained values to help control and monitor access
 * and provide diagnostics.
 * 可能被一个线程独占的同步器。此类提供了创建锁和相关同步器的基础，
 * 这些锁和同步器可能包含所有权的概念。该类本身不管理或者使用这些信息。
 * 但是，子类和工具可以使用适当维护的值来帮助控制和监视访问并提供诊断。
 *
 * @since 1.6
 * @author Doug Lea
 */
public abstract class AbstractOwnableSynchronizer
        implements java.io.Serializable {

    /** Use serial ID even though all fields transient. */
    private static final long serialVersionUID = 3737899427754241961L;

    /**
     * Empty constructor for use by subclasses.
     */
    protected AbstractOwnableSynchronizer() { }

    /**
     * The current owner of exclusive mode synchronization.
	 * 独占模式同步的当前占有者（线程）。
     */
    private transient Thread exclusiveOwnerThread;

    /**
     * Sets the thread that currently owns exclusive access.
     * A {@code null} argument indicates that no thread owns access.
     * This method does not otherwise impose any synchronization or
     * {@code volatile} field accesses.
	 * 设置当前拥有独占访问权限的线程。null参数表明当前没有线程有用访问权限。
	 * 此方法不会以其他方式强制任何同步或{@code volatile}字段访问
     * @param thread the owner thread
     */
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
```
AbstractOwnableSynchronizer类总结：  
1. 该类为AbstractQueuedSynchronizer的父类，作用也很简单，用于保存哪个线程当前占有该同步器。

##二：AbstractQueuedSynchronizer
先简单的看下大体框架，还有一些基础代码，后续通过实现类来细看。
```java
/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers (semaphores, events, etc) that rely on
 * first-in-first-out (FIFO) wait queues.  This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic {@code int} value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released.  Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link
 * #setState} and {@link #compareAndSetState} is tracked with respect
 * to synchronization.
 * 提供一个框架，用于实现依赖先进先出（FIFO）等待队列的阻塞锁和相关同步器（信号量、事件等）。
 * 此类被设计为大多数类型的同步器的有用基础类，这些同步器依赖于单个原子 int 值来表示状态。
 * 子类必须定义更改此状态的受保护方法，以及定义此状态在获取或释放此对象方面的含义。
 * 鉴于这些，该类中的其他方法执行所有排队和阻塞机制。子类可以维护其他状态字段，
 * 但只有原子更新的 int 值使用方法 getState 、 setState 和 compareAndSetState 在同步方面被跟踪。
 *
 * <p>Subclasses should be defined as non-public internal helper
 * classes that are used to implement the synchronization properties
 * of their enclosing class.  Class
 * {@code AbstractQueuedSynchronizer} does not implement any
 * synchronization interface.  Instead it defines methods such as
 * {@link #acquireInterruptibly} that can be invoked as
 * appropriate by concrete locks and related synchronizers to
 * implement their public methods.
 * 子类应定义为非公共内部帮助器类，用于实现其封闭类的同步属性。
 * 类 AbstractQueuedSynchronizer 未实现任何同步接口。
 * 相反，它定义了 acquireInterruptibly 等方法，
 * 具体锁和相关同步器可以适当地调用这些方法来实现它们的公共方法。
 *
 * <p>This class supports either or both a default <em>exclusive</em>
 * mode and a <em>shared</em> mode. When acquired in exclusive mode,
 * attempted acquires by other threads cannot succeed. Shared mode
 * acquires by multiple threads may (but need not) succeed. This class
 * does not &quot;understand&quot; these differences except in the
 * mechanical sense that when a shared mode acquire succeeds, the next
 * waiting thread (if one exists) must also determine whether it can
 * acquire as well. Threads waiting in the different modes share the
 * same FIFO queue. Usually, implementation subclasses support only
 * one of these modes, but both can come into play for example in a
 * {@link ReadWriteLock}. Subclasses that support only exclusive or
 * only shared modes need not define the methods supporting the unused mode.
 * 此类支持默认的独占模式和共享模式。在独占模式下获取时，其他线程尝试的获取无法成功。
 * 多线程获取共享模式可能（但不需要）成功。此类不理解这些差异，除了机械意义上的差异，
 * 即当共享模式获取成功时，下一个等待线程（如果存在）还必须确定它是否也可以获取。
 * 在不同模式下等待的线程共享相同的FIFO队列。通常，实现子类只支持其中一种模式，
 * 但这两种模式都可以在{@link ReadWriteLock}中发挥作用。
 * 仅支持独占或共享模式的子类不需要定义支持未使用模式的方法。
 *
 * <p>This class defines a nested {@link ConditionObject} class that
 * can be used as a {@link Condition} implementation by subclasses
 * supporting exclusive mode for which method {@link
 * #isHeldExclusively} reports whether synchronization is exclusively
 * held with respect to the current thread, method {@link #release}
 * invoked with the current {@link #getState} value fully releases
 * this object, and {@link #acquire}, given this saved state value,
 * eventually restores this object to its previous acquired state.  No
 * {@code AbstractQueuedSynchronizer} method otherwise creates such a
 * condition, so if this constraint cannot be met, do not use it.  The
 * behavior of {@link ConditionObject} depends of course on the
 * semantics of its synchronizer implementation.
 * 该类定义了一个嵌套的 ConditionObject 类，该类可用作支持独占模式的子类的 ConditionObject 实现，
 * 方法 isHeldExclusively 报告是否对当前线程独占保持同步，
 * 使用当前 getState 的值调用方法 release 完全释放此对象，
 * 并且 acquire 给定此保存的状态值，最终将此对象恢复到以前获取的状态。
 * 没有{@code AbstractQueuedSynchronizer}方法会创建这样的 Condition ，
 * 因此如果无法满足此约束，请不要使用它。 ConditionObject 的行为当然取决于其同步器实现的语义。
 *
 * <p>This class provides inspection, instrumentation, and monitoring
 * methods for the internal queue, as well as similar methods for
 * condition objects. These can be exported as desired into classes
 * using an {@code AbstractQueuedSynchronizer} for their
 * synchronization mechanics.
 *
 * 此类提供内部队列的检查、检测和监视方法，以及条件对象的类似方法。
 * 可以根据需要使用{@code AbstractQueuedSynchronizer}作为同步机制将它们导出到类中。
 *
 * <p>Serialization of this class stores only the underlying atomic
 * integer maintaining state, so deserialized objects have empty
 * thread queues. Typical subclasses requiring serializability will
 * define a {@code readObject} method that restores this to a known
 * initial state upon deserialization.
 * 此类的序列化只存储底层原子整数维护状态，因此反序列化对象具有空线程队列。
 * 需要序列化的典型子类将定义一个 readObject 方法，该方法在反序列化时将其恢复到已知的初始状态。
 *
 * <h3>Usage</h3>
 *
 * <p>To use this class as the basis of a synchronizer, redefine the
 * following methods, as applicable, by inspecting and/or modifying
 * the synchronization state using {@link #getState}, {@link
 * #setState} and/or {@link #compareAndSetState}:
 * 要将此类用作同步器的基础，请通过使用 getState 、 setState 和/或 compareAndSetState 
 * 检查和/或修改同步状态，重新定义以下方法（如适用）：
 *
 * <ul>
 * <li> {@link #tryAcquire}
 * <li> {@link #tryRelease}
 * <li> {@link #tryAcquireShared}
 * <li> {@link #tryReleaseShared}
 * <li> {@link #isHeldExclusively}
 * </ul>
 *
 * Each of these methods by default throws {@link
 * UnsupportedOperationException}.  Implementations of these methods
 * must be internally thread-safe, and should in general be short and
 * not block. Defining these methods is the <em>only</em> supported
 * means of using this class. All other methods are declared
 * {@code final} because they cannot be independently varied.
 * 默认情况下，这些方法都会抛出 UnsupportedOperationException 。
 * 这些方法的实现必须是内部线程安全的，并且通常应该是简短的，而不是阻塞的。
 * 定义这些方法是使用此类的唯一受支持的方法。所有其他方法都声明为 final ，因为它们不能独立变化。
 *
 * <p>You may also find the inherited methods from {@link
 * AbstractOwnableSynchronizer} useful to keep track of the thread
 * owning an exclusive synchronizer.  You are encouraged to use them
 * -- this enables monitoring and diagnostic tools to assist users in
 * determining which threads hold locks.
 * 您还可能发现从 AbstractOwnableSynchronizer 继承的方法对于跟踪拥有独占同步器的线程非常有用。
 * 我们鼓励您使用它们 ——这使监视和诊断工具能够帮助用户确定哪些线程持有锁。
 *
 * <p>Even though this class is based on an internal FIFO queue, it
 * does not automatically enforce FIFO acquisition policies.  The core
 * of exclusive synchronization takes the form:
 * 即使此类基于内部FIFO队列，它也不会自动强制执行FIFO获得策略。独占同步的核心采用以下形式：
 *
 * <pre>
 * Acquire:
 *     while (!tryAcquire(arg)) {
 *        <em>enqueue thread if it is not already queued</em>;
 *        <em>possibly block current thread</em>;
 *     }
 *
 * Release:
 *     if (tryRelease(arg))
 *        <em>unblock the first queued thread</em>;
 * </pre>
 *
 * (Shared mode is similar but may involve cascading signals.)
 * （共享模式类似，但可能涉及级联信号。）
 *
 * <p id="barging">Because checks in acquire are invoked before
 * enqueuing, a newly acquiring thread may <em>barge</em> ahead of
 * others that are blocked and queued.  However, you can, if desired,
 * define {@code tryAcquire} and/or {@code tryAcquireShared} to
 * disable barging by internally invoking one or more of the inspection
 * methods, thereby providing a <em>fair</em> FIFO acquisition order.
 * In particular, most fair synchronizers can define {@code tryAcquire}
 * to return {@code false} if {@link #hasQueuedPredecessors} (a method
 * specifically designed to be used by fair synchronizers) returns
 * {@code true}.  Other variations are possible.
 * 由于在排队之前调用了acquire，因此新的获取线程可能会在被阻塞和排队的其他线程之前退出。
 * 但是，如果需要，您可以定义 tryAcquire 和/或 tryAcquireShared 
 * 以通过内部调用一个或多个检查方法来禁用barging，从而提供一个<em>公平的</em>FIFO获取顺序。
 * 特别是，大多数公平同步器可以定义 tryAcquire 以返回 false ，
 * 前提是 hasQueuedPredecessors （一种专门为公平同步器设计的方法）返回 true 。
 * 其他变化也是可能的。
 *
 * <p>Throughput and scalability are generally highest for the
 * default barging (also known as <em>greedy</em>,
 * <em>renouncement</em>, and <em>convoy-avoidance</em>) strategy.
 * While this is not guaranteed to be fair or starvation-free, earlier
 * queued threads are allowed to recontend before later queued
 * threads, and each recontention has an unbiased chance to succeed
 * against incoming threads.  Also, while acquires do not
 * &quot;spin&quot; in the usual sense, they may perform multiple
 * invocations of {@code tryAcquire} interspersed with other
 * computations before blocking.  This gives most of the benefits of
 * spins when exclusive synchronization is only briefly held, without
 * most of the liabilities when it isn't. If so desired, you can
 * augment this by preceding calls to acquire methods with
 * "fast-path" checks, possibly prechecking {@link #hasContended}
 * and/or {@link #hasQueuedThreads} to only do so if the synchronizer
 * is likely not to be contended.
 * 默认驳船（也称为贪婪、放弃和避免护航）策略的吞吐量和可伸缩性通常最高。
 * 虽然这不能保证公平或无饥饿，但允许较早排队的线程在稍后排队的线程之前重新调度，
 * 并且每个重新调度都有一个针对传入线程的无偏见的成功机会。
 *
 * <p>This class provides an efficient and scalable basis for
 * synchronization in part by specializing its range of use to
 * synchronizers that can rely on {@code int} state, acquire, and
 * release parameters, and an internal FIFO wait queue. When this does
 * not suffice, you can build synchronizers from a lower level using
 * {@link java.util.concurrent.atomic atomic} classes, your own custom
 * {@link java.util.Queue} classes, and {@link LockSupport} blocking
 * support.
 * 这个类为同步提供了一个高效且可扩展的基础，
 * 部分是通过将其使用范围专门化为可以依赖于{@code int}状态、获取和释放参数以及内部FIFO等待队列的同步器。
 * 当这还不够时，您可以使用atomic类、您自己的自定义Queue类和LockSupport阻塞支持从较低级别构建同步器。
 * 
 * 此处有两个示例，先不看，后续再看。
 *
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractQueuedSynchronizer
        extends AbstractOwnableSynchronizer
        implements java.io.Serializable {

    /**
     * Head of the wait queue, lazily initialized.  Except for
     * initialization, it is modified only via method setHead.  Note:
     * If head exists, its waitStatus is guaranteed not to be
     * CANCELLED.
     * 等待队列的头节点，懒初始化。除了初始化，只有通过setHead方法来改变。
     * Note：如果head已经存在，它的waitStatus必然不是CANCELLED。Node为队列中的一个节点封装类，后续再详细看。
     */
    private transient volatile Node head;

    /**
     * Tail of the wait queue, lazily initialized.  Modified only via
     * method enq to add new wait node.
     * 等待队列的尾部，懒初始化。只有通过enq方法添加新的等待节点来改变。
     */
    private transient volatile Node tail;

    /**
     * The synchronization state.
     * 同步的状态
     */
    private volatile int state;

    /**
     * Atomically sets synchronization state to the given updated
     * value if the current state value equals the expected value.
     * This operation has memory semantics of a {@code volatile} read
     * and write.
     * 原子的在同步状态（state）等于期待值时更新为给定的值。
     * 此操作具有volatile 读/写的内存语义。
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that the actual
     *         value was not equal to the expected value.
     */
    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    /**
     * CAS head field. Used only by enq.
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq.
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * Inserts node into queue, initializing if necessary. See picture above.
     * 队列插入节点，必要时进行初始化。
     * @param node the node to insert
     * @return node's predecessor
     */
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize 如果尾部是空，则new 一个Node作为head和tail
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                //双向链表入链操作。 
                // 1.新节点的前置节点设置为老的tail
                // 2.将新节点通过CAS操作设置为新的tail
                // 3.将老的tail的后续节点设置为新增的节点
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    /**
     * Creates and enqueues node for current thread and given mode.
     * 为当前线程和给定模式来创建并排队节点。
     * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
     * @return the new node
     */
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            //如果尾部不时null，则新节点加入链表。
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    /**
     * Acquires in exclusive mode, ignoring interrupts.  Implemented
     * by invoking at least once {@link #tryAcquire},
     * returning on success.  Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link
     * #tryAcquire} until success.  This method can be used
     * to implement method {@link Lock#lock}.
     * 以独占模式获取，忽略终端。通过调用至少一次tryAcquire来实现，并在成功时返回。
     * 否则线程将排队，可能会重复阻塞和取消阻塞，调用tryAcquire，直到成功。
     * 该方法可以用来实现Lock.lock
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     */
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }

    /**
     * Acquires in exclusive uninterruptible mode for thread already in
     * queue. Used by condition wait methods as well as acquire.
     *
     * @param node the node
     * @param arg the acquire argument
     * @return {@code true} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
}
```
AbstractQueuedSynchronizer初步总结：
1. 内部实现依赖于一个FIFO的队列（双向链表），类里有head、tail两个变量的定义。节点通过Node类来封装。
2. 该类只是一个基础类，一个抽象类，只是提供了一个框架，该类是通过一个int类型的state来实现同步器。
3. 子类必须是非公共内部帮助类，后续再看一些它的实现类。
4. 此类支持独占模式和共享模式。例如ReentrantReadWriteLock

##三： AbstractQueuedSynchronizer.Node
```java
/**
     * Wait queue node class.
     * 等待队列节点类。
     *
     * <p>The wait queue is a variant of a "CLH" (Craig, Landin, and
     * Hagersten) lock queue. CLH locks are normally used for
     * spinlocks.  We instead use them for blocking synchronizers, but
     * use the same basic tactic of holding some of the control
     * information about a thread in the predecessor of its node.  A
     * "status" field in each node keeps track of whether a thread
     * should block.  A node is signalled when its predecessor
     * releases.  Each node of the queue otherwise serves as a
     * specific-notification-style monitor holding a single waiting
     * thread. The status field does NOT control whether threads are
     * granted locks etc though.  A thread may try to acquire if it is
     * first in the queue. But being first does not guarantee success;
     * it only gives the right to contend.  So the currently released
     * contender thread may need to rewait.
     *
     * 等待队列是“CLH”（Craig、Landin和Hagersten）锁队列的变体。CLH锁通常用于自旋锁。
     * 相反，我们使用它们来替代阻塞同步器，但使用相同的基本策略，即在其节点的前一个线程中保存
     * 一些关于该线程的控制信息。每个节点中的“状态”字段跟踪线程是否应该阻塞。
     * 节点在其前一个节点释放时发出信号。队列的每个节点都充当一个特定的通知样式监视器，
     * 其中包含一个等待线程。但是status字段不控制线程是否被授予锁等。如果线程是队列中的第一个线程，
     * 它可能会尝试获取。但第一并不能保证成功；它只给了竞争的权利。因此，当前释放的竞争者线程可能需要重新等待。
     *
     * <p>To enqueue into a CLH lock, you atomically splice it in as new
     * tail. To dequeue, you just set the head field.
     * 要排队进入CLH锁，您可以将其作为新的尾部进行原子拼接。要退出队列，只需设置head字段。
     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
     *
     * <p>Insertion into a CLH queue requires only a single atomic
     * operation on "tail", so there is a simple atomic point of
     * demarcation from unqueued to queued. Similarly, dequeuing
     * involves only updating the "head". However, it takes a bit
     * more work for nodes to determine who their successors are,
     * in part to deal with possible cancellation due to timeouts
     * and interrupts.
     * 插入到CLH队列只需要在“tail”上执行单个原子操作，因此有一个简单的
     * 从unqueued到queued的原子划分点。类似地，出列只涉及更新“头”。然而，
     * 节点需要更多的工作来确定谁是他们的继任者，部分是为了处理由于超时和中断而可能取消的问题。
     *
     * <p>The "prev" links (not used in original CLH locks), are mainly
     * needed to handle cancellation. If a node is cancelled, its
     * successor is (normally) relinked to a non-cancelled
     * predecessor. For explanation of similar mechanics in the case
     * of spin locks, see the papers by Scott and Scherer at
     * http://www.cs.rochester.edu/u/scott/synchronization/
     * "prev"链接（未在原始CLH锁中使用）主要用于处理取消。如果节点被取消，
     * 其后续节点（通常）将重新链接到未取消的前置节点。有关自旋锁的类似力学解释，
     * 请参阅Scott和Scherer在http://www.cs.rochester.edu/u/scott/synchronization/
     *
     * <p>We also use "next" links to implement blocking mechanics.
     * The thread id for each node is kept in its own node, so a
     * predecessor signals the next node to wake up by traversing
     * next link to determine which thread it is.  Determination of
     * successor must avoid races with newly queued nodes to set
     * the "next" fields of their predecessors.  This is solved
     * when necessary by checking backwards from the atomically
     * updated "tail" when a node's successor appears to be null.
     * (Or, said differently, the next-links are an optimization
     * so that we don't usually need a backward scan.)
     * 我们还使用"next"链接来实现阻塞机制。每个节点的线程id都保存在自己的节点中，
     * 因此前置节点通过遍历下一个链接来确定它是哪个线程，从而向下一个节点发出唤醒信号。
     * 确定继任者必须避免与新排队的节点竞争，以设置其前任节点的“下一个”字段。
     * 当节点的后续节点显示为空时，通过从原子更新的“tail”向后检查，在必要时可以解决此问题。
     * （或者，换言之，下一个链接是一个优化，因此我们通常不需要反向扫描。）
     *
     * <p>Cancellation introduces some conservatism to the basic
     * algorithms.  Since we must poll for cancellation of other
     * nodes, we can miss noticing whether a cancelled node is
     * ahead or behind us. This is dealt with by always unparking
     * successors upon cancellation, allowing them to stabilize on
     * a new predecessor, unless we can identify an uncancelled
     * predecessor who will carry this responsibility.
     * 取消为基本算法引入了一些保守性。因为我们必须轮询其他节点的取消，
     * 所以我们可能会忽略已取消的节点是在我们前面还是后面。
     * 这是通过在撤销时始终取消继承人资格来解决的，允许他们稳定在新的前任上，
     * 除非我们能够确定一个未被撤销的前任将承担这一责任。
     *
     * <p>CLH queues need a dummy header node to get started. But
     * we don't create them on construction, because it would be wasted
     * effort if there is never contention. Instead, the node
     * is constructed and head and tail pointers are set upon first
     * contention.
     * CLH队列需要一个虚拟的头结点来启动。但是我们不在构造方法中创建他们，
     * 因为如果没有竞争，这将是徒劳的。相反，在第一次争用时构造节点并设置头指针和尾指针。
     *
     * <p>Threads waiting on Conditions use the same nodes, but
     * use an additional link. Conditions only need to link nodes
     * in simple (non-concurrent) linked queues because they are
     * only accessed when exclusively held.  Upon await, a node is
     * inserted into a condition queue.  Upon signal, the node is
     * transferred to the main queue.  A special value of status
     * field is used to mark which queue a node is on.
     * 等待条件（Conditions）的线程使用相同的节点，但是使用一个额外的连接。
     * 条件（Conditions）只需要链接简单（非并发）链接队列中的节点，因为它们仅在独占持有时才被访问
     *
     * <p>Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill
     * Scherer and Michael Scott, along with members of JSR-166
     * expert group, for helpful ideas, discussions, and critiques
     * on the design of this class.
     */
    static final class Node {
        /** Marker to indicate a node is waiting in shared mode */
        static final Node SHARED = new Node();
        /** Marker to indicate a node is waiting in exclusive mode */
        static final Node EXCLUSIVE = null;

        /** waitStatus value to indicate thread has cancelled */
        static final int CANCELLED =  1;
        /** waitStatus value to indicate successor's thread needs unparking */
        static final int SIGNAL    = -1;
        /** waitStatus value to indicate thread is waiting on condition */
        static final int CONDITION = -2;
        /**
         * waitStatus value to indicate the next acquireShared should
         * unconditionally propagate
         */
        static final int PROPAGATE = -3;

        /**
         * Status field, taking on only the values:
         *   SIGNAL:     The successor of this node is (or will soon be)
         *               blocked (via park), so the current node must
         *               unpark its successor when it releases or
         *               cancels. To avoid races, acquire methods must
         *               first indicate they need a signal,
         *               then retry the atomic acquire, and then,
         *               on failure, block.
         *               该节点的后置节点当前是（或者即将）阻塞（通过park），
         *               所以当前节点必须在释放或者取消时unpark其后置节点。
         *               为了避免竞争，acquire方法必须首先指示它们需要信号，
         *               然后重试原子acquire，然后在失败时阻塞。
         *   CANCELLED:  This node is cancelled due to timeout or interrupt.
         *               Nodes never leave this state. In particular,
         *               a thread with cancelled node never again blocks.
         *               该节点由于超市或者中断已经被取消。节点永远不会再离开该状态。
         *               已取消节点的线程永远不会再阻塞。
         *   CONDITION:  This node is currently on a condition queue.
         *               It will not be used as a sync queue node
         *               until transferred, at which time the status
         *               will be set to 0. (Use of this value here has
         *               nothing to do with the other uses of the
         *               field, but simplifies mechanics.)
         *               该节点目前处于条件队列中。在转变之前，它将不会用作同步队列节点，
         *               此时状态将设置为0。（这里使用该值与字段的其他用途无关，但简化了机制）
         *   PROPAGATE:  A releaseShared should be propagated to other
         *               nodes. This is set (for head node only) in
         *               doReleaseShared to ensure propagation
         *               continues, even if other operations have
         *               since intervened.
         *               releaseShared应传播到其他节点。这是在doReleaseShared中设置的（仅针对头部节点），
         *               以确保传播继续进行，即使其他操作已经介入
         *   0:          None of the above
         *
         * The values are arranged numerically to simplify use.
         * Non-negative values mean that a node doesn't need to
         * signal. So, most code doesn't need to check for particular
         * values, just for sign.
         * 这些值使用数字形式排列，以方便使用。非负值意味着这个节点不需要发信号。
         * 大多数代码不需要检查特定值，只需要检查符号。
         *
         * The field is initialized to 0 for normal sync nodes, and
         * CONDITION for condition nodes.  It is modified using CAS
         * (or when possible, unconditional volatile writes).
         * 对于普通的同步节点该值初始为0，条件节点初始为CONDITION。
         * 使用CAS（或在可能的情况下，使用无条件易失性写入）对其进行修改。
         */
        volatile int waitStatus;

        /**
         * Link to predecessor node that current node/thread relies on
         * for checking waitStatus. Assigned during enqueuing, and nulled
         * out (for sake of GC) only upon dequeuing.  Also, upon
         * cancellation of a predecessor, we short-circuit while
         * finding a non-cancelled one, which will always exist
         * because the head node is never cancelled: A node becomes
         * head only as a result of successful acquire. A
         * cancelled thread never succeeds in acquiring, and a thread only
         * cancels itself, not any other node.
         * 链接到当前 节点/线程 所依赖的前置节点用于检测 waitStatus。
         * 在排队过程中分配，并且仅在退出队列时取消（为了GC）。
         * 此外，在取消前一个节点时，我们会在查找未取消的前一个节点时短路，
         * 因为头部节点从未取消，所以该前一个节点将始终存在：节点仅在成功获取后才成为头部。
         * 被取消的线程永远不会成功获取，线程只会取消自身，而不会取消任何其他节点。
         */
        volatile Node prev;

        /**
         * Link to the successor node that the current node/thread
         * unparks upon release. Assigned during enqueuing, adjusted
         * when bypassing cancelled predecessors, and nulled out (for
         * sake of GC) when dequeued.  The enq operation does not
         * assign next field of a predecessor until after attachment,
         * so seeing a null next field does not necessarily mean that
         * node is at end of queue. However, if a next field appears
         * to be null, we can scan prev's from the tail to
         * double-check.  The next field of cancelled nodes is set to
         * point to the node itself instead of null, to make life
         * easier for isOnSyncQueue.
         * 链接到后续节点，当前节点/线程在释放时将对其进行unparks。
         * 在排队过程中分配，在绕过已取消的前置时进行调整，在退出队列时取消（为了GC）。
         * enq操作直到连接之后才分配前置的next字段，所以看到字段next为空时并不一定意味着节点在队列的末尾。
         * 但是，如果next字段显示为空，我们可以从尾部扫描上一个字段以进行双重检查。
         * 取消节点的next字段设置为指向节点本身，而不是null，以使isOnSyncQueue的工作更轻松。
         */
        volatile Node next;

        /**
         * The thread that enqueued this node.  Initialized on
         * construction and nulled out after use.
         * 使此节点排队的线程。构造时初始化，使用后置空。
         */
        volatile Thread thread;

        /**
         * Link to next node waiting on condition, or the special
         * value SHARED.  Because condition queues are accessed only
         * when holding in exclusive mode, we just need a simple
         * linked queue to hold nodes while they are waiting on
         * conditions. They are then transferred to the queue to
         * re-acquire. And because conditions can only be exclusive,
         * we save a field by using special value to indicate shared
         * mode.
         * 链接到下一个等待着条件（condition）的节点，或者特殊的值 SHARED。
         * 因为只有在独占模式下保持时才访问条件队列，
         * 所以我们只需要一个简单的链接队列来保持节点在等待条件时的状态。
         * 然后将它们转移到队列中以重新获取。
         * 由于条件只能是独占的，我们通过使用特殊值保存字段来表示共享模式。
         */
        Node nextWaiter;

        /**
         * Returns true if node is waiting in shared mode.
         * 如果节点是在共享模式下等待则返回true
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * Returns previous node, or throws NullPointerException if null.
         * Use when predecessor cannot be null.  The null check could
         * be elided, but is present to help the VM.
         * 返回前置节点，如果前置节点为空则抛出NullPointerException。
         * 当前置节点不为空时使用。可以省略空检查，但它的存在是为了帮助VM
         *
         * @return the predecessor of this node
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```
AbstractQueuedSynchronizer.Node总结：  
1. Node即为AQS队列中的节点信息封装类，节点内存有prev和next节点属性，所以是一个双向链表。
2. node内部存着当前节点的Thread信息。