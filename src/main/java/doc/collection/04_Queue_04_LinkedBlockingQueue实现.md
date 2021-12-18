```java
/**
 * An optionally-bounded {@linkplain BlockingQueue blocking queue} based on
 * linked nodes.
 * This queue orders elements FIFO (first-in-first-out).
 * The <em>head</em> of the queue is that element that has been on the
 * queue the longest time.
 * The <em>tail</em> of the queue is that element that has been on the
 * queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * Linked queues typically have higher throughput than array-based queues but
 * less predictable performance in most concurrent applications.
 * 基于链接节点的可选有界阻塞队列。 此队列对元素 FIFO（先进先出）进行排序。 
 * 队列的头部是在队列中停留时间最长的那个元素。 队列的尾部是在队列中停留时间最短的那个元素。 
 * 新元素插入队列尾部，队列检索操作获取队列头部元素。 
 * 链接队列通常比基于数组的队列具有更高的吞吐量，但在大多数并发应用程序中的可预测性较差。
 *
 * <p>The optional capacity bound constructor argument serves as a
 * way to prevent excessive queue expansion. The capacity, if unspecified,
 * is equal to {@link Integer#MAX_VALUE}.  Linked nodes are
 * dynamically created upon each insertion unless this would bring the
 * queue above capacity.
 * 可选的容量绑定构造函数参数是一种防止队列过度扩展的方法。 
 * 如果未指定，容量等于 Integer.MAX_VALUE。 
 * 链接节点在每次插入时动态创建，除非这会使队列超过容量。
 *
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 * 此类及其迭代器实现了 Collection 和 Iterator 接口的所有可选方法。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    /*
     * A variant of the "two lock queue" algorithm.  The putLock gates
     * entry to put (and offer), and has an associated condition for
     * waiting puts.  Similarly for the takeLock.  The "count" field
     * that they both rely on is maintained as an atomic to avoid
     * needing to get both locks in most cases. Also, to minimize need
     * for puts to get takeLock and vice-versa, cascading notifies are
     * used. When a put notices that it has enabled at least one take,
     * it signals taker. That taker in turn signals others if more
     * items have been entered since the signal. And symmetrically for
     * takes signalling puts. Operations such as remove(Object) and
     * iterators acquire both locks.
     *
     * Visibility between writers and readers is provided as follows:
     *
     * Whenever an element is enqueued, the putLock is acquired and
     * count updated.  A subsequent reader guarantees visibility to the
     * enqueued Node by either acquiring the putLock (via fullyLock)
     * or by acquiring the takeLock, and then reading n = count.get();
     * this gives visibility to the first n items.
     *
     * To implement weakly consistent iterators, it appears we need to
     * keep all Nodes GC-reachable from a predecessor dequeued Node.
     * That would cause two problems:
     * - allow a rogue Iterator to cause unbounded memory retention
     * - cause cross-generational linking of old Nodes to new Nodes if
     *   a Node was tenured while live, which generational GCs have a
     *   hard time dealing with, causing repeated major collections.
     * However, only non-deleted Nodes need to be reachable from
     * dequeued Nodes, and reachability does not necessarily have to
     * be of the kind understood by the GC.  We use the trick of
     * linking a Node that has just been dequeued to itself.  Such a
     * self-link implicitly means to advance to head.next.
     */

    /**
     * Linked list node class
     * 链表的节点类
     */
    static class Node<E> {
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;

        Node(E x) { item = x; }
    }

    /** The capacity bound, or Integer.MAX_VALUE if none */
    //容量限制，如果没有，则为 Integer.MAX_VALUE
    private final int capacity;

    /** Current number of elements */
    //元素当前数量
    private final AtomicInteger count = new AtomicInteger();

    /**
     * Head of linked list.
     * Invariant: head.item == null
     * 链表的头。
     * 不变量：head.item == null
     */
    transient Node<E> head;

    /**
     * Tail of linked list.
     * Invariant: last.next == null
     * 链表的尾。
     * 不变量：last.next == null
     */
    private transient Node<E> last;

    /** Lock held by take, poll, etc */
    // take、poll等使用的锁
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    // take时如果为空，则在该condition上等待
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    // put、offer等使用的锁
    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    // put时如果容量已满，则在该condition上等待
    private final Condition notFull = putLock.newCondition();

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     * 给正在等待的take发出信号。 仅从 put/offer 调用（否则通常不会锁定 takeLock。）
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        //Condition的规范，signal之前一定要获取其关联的锁。
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * Signals a waiting put. Called only from take/poll.
     * 给正在等待的put发出信号。仅从 take/poll调用。
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        //Condition的规范，signal之前一定要获取其关联的锁。
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    /**
     * Links node at end of queue.
     * 将节点node添加到链表尾部
     * @param node the node
     */
    private void enqueue(Node<E> node) {
        // assert putLock.isHeldByCurrentThread();
        // assert last.next == null;
        //当前线程一定是持有putLock的，并且last.next==null
        // 因为是持有锁的，所以这两步操作是不存在线程竞争的，不会错乱，
        // 就比ConcurrentLinkedQueue逻辑简单太多
        last = last.next = node;
    }

    /**
     * Removes a node from head of queue.
     * 从链表的头部移除一个节点
     * @return the node
     */
    private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if {@code capacity} is not greater
     *         than zero
     */
    public LinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        //初始化时head和last都是同一个空Node
        last = head = new Node<E>(null);
    }
}
```

##二offer和poll逻辑
offer和poll也是Queue接口的定义。他两时不阻塞的。   
主要是对比下ConcurrentLinkedQueue这种不使用锁而是使用CAS保证线程安全的处理。
```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    /**
     * Inserts the specified element at the tail of this queue if it is
     * possible to do so immediately without exceeding the queue's capacity,
     * returning {@code true} upon success and {@code false} if this queue
     * is full.
     * When using a capacity-restricted queue, this method is generally
     * preferable to method {@link BlockingQueue#add add}, which can fail to
     * insert an element only by throwing an exception.
     * 如果可以在不超过队列容量的情况下立即在此队列的尾部插入指定的元素，
     * 则在成功时返回 true，如果队列已满则返回 false。 
     * 当使用容量受限的队列时，此方法通常比 add 方法更可取，
     * 后者仅通过抛出异常来表示无法插入元素。
     *
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        final AtomicInteger count = this.count;
        if (count.get() == capacity)
            //先判断当前容量已满，则直接返回false。
            // 这里的判断可以不准的，有可能马上又有其它线程给offer了几个进去，结果又满了。
            return false;
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        //上锁
        putLock.lock();
        try {
            //再次判断当前容量是否可用
            if (count.get() < capacity) {
                //入链 last=last.next=node
                enqueue(node);
                //count计数减1，注意c是老值，是先get在increment的。
                c = count.getAndIncrement();
                if (c + 1 < capacity)
                    //加入新节点后发现还有空间，则发出信号
                    // 其它线程在调用put这种带有阻塞性质的插入节点方法时，
                    // 如果容量已经满了就会等待在notFull这个Condition上，
                    // 这时就会将他们唤醒，让他们再次重新尝试插入操作。
                    //这里是直接调用，而不是调用signalNotFull方法（先获取锁，在notFull.signal())，
                    //因为这里是已经持有了putLock
                    notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            //表示加入之前队列是空的，那加入了一个节点后队列就不空了。
            //其它线程在调用take这种带有阻塞性质的删除节点方法时，
            //因为队列时空的，就会等待在notEmpty这个Condition上，
            //这时就会将他们唤醒，让他们再次重新尝试删除操作。
            //这里就不可以直接notEmpty.signal喽，因为没有持有takeLock锁。
            signalNotEmpty();
        return c >= 0;
    }

    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0)
            //如果空直接返回
            return null;
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        //上锁
        takeLock.lock();
        try {
            //再次判断是否为空
            if (count.get() > 0) {
                //删除节点
                x = dequeue();
                c = count.getAndDecrement();
                if (c > 1)
                    //删除一个节点后，还不为空，发出信号，通知阻塞线程可以删除。
                    notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            //删除前容量已满，删除后就不满了，发出信号通知阻塞线程可以添加
            signalNotFull();
        return x;
    }
}
```