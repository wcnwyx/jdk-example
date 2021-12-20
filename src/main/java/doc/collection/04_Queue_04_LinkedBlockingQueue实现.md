#LinkedBlockingList源码分析

##一： 类注释及内部变量预览
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
总结：
1. 基于单向链表实现的阻塞队列（BlockingQueue）。
2. 阻塞是采用Condition的await相关方法来实现的。
3. 链表的头head表示插入时间最久的元素，last表示最新插入的元素。

##二： offer和poll逻辑
offer和poll也是Queue接口的定义。他两是不阻塞的，条件不满足就直接返回false了。   
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

    //整体和offer的逻辑差不多
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
总结：  
1. 相对于ConcurrentLinkedQueue的offer和poll逻辑简单太多，主要是使用了Lock。
2. 其实这两个方法也不能说是不阻塞的，只是说offer在队列满的时候、或者poll在队列空的时候会立即返回，不会等待在Condition上。
3. 严格来说，里面使用了ReentrantLock.lock()，多线程抢锁的时候也会阻塞一下，尤其是remove这种方法可能会导致锁获取阻塞时间过长。

##三： put和take逻辑
阻塞，但是可以中断的。   
```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary for space to become available.
     * 在该队列的尾部插入指定的元素，如有必要，等待空间变为可用。
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        //这里为什么使用lockInterruptibly呢？offer用的是lock()，怕获取锁时间过长？
        putLock.lockInterruptibly();
        try {
            //容量满了，就在Condition上await，
            // 这里要使用while循环哦，因为Condition可能会出现虚假唤醒，接口注释里特别说明的。
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
    }

    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }
}
```
1. put和take在不满足容量条件的情况下，会await在Condition上，等带别的线程再次添加或删除元素后发送信号通知。
2. await时要使用while循环判断，因为Condition是会出现虚假唤醒的情况。
3. 这里为什么要用**lockInterruptibly**呢？而offer方法使用的是**lock()**? 看过后面remove就大概知道了。

##四： remove
注释里解释说该方法不高效，不建议高频率使用。  
```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    public boolean remove(Object o) {
        if (o == null) return false;
        // 这里使用的是fullLock，将putLock和takeLock全部锁住，队列无法进行添加和删除
        fullyLock();
        try {
            //链表循环，从头到尾循环找，效率可想而知
            for (Node<E> trail = head, p = trail.next;
                 p != null;
                 trail = p, p = p.next) {
                if (o.equals(p.item)) {
                    unlink(p, trail);
                    return true;
                }
            }
            return false;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * Locks to prevent both puts and takes.
     */
    void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }

    /**
     * Unlocks to allow both puts and takes.
     */
    void fullyUnlock() {
        takeLock.unlock();
        putLock.unlock();
    }
}
```
总结：  
1. 其实不止remove方法会fullLock，contains、toArray、toString、clear都会fullLock，所以使用的时候要注意。
2. remove、contains方法内部涉及到链表搜索逻辑，如果队列中数据量很大，效率也是很慢的。
3. 看到这里，就会知道为什么offer必须使用lock()，而put必须使用lockInterruptibly()了，因为remove这种方法会持有锁过长时间。
    - 3.1 offer的定义就是要一直等待的，不能中断不允许抛出**InterruptedException**，所以即使是阻塞在获取锁的过程中，
      而不是await在Condition上时（因为队列满），也不能响应中断，所以只能使用lock()。
    - 3.2 put的方法注释是明确支持中断的，同样的如果说是阻塞在获取锁的过程中，lock就不能中断了，只能使用lockInterruptibly()。
    - 3.3 其实即使没有remove这种持有锁时间过长的方法，如果严格按照put、offer方法的定义也是必须要这么使用的，只是会感觉既然不会
   长时间阻塞到锁的获取上，put也是可以使用lock()的，只是可能出现中断的响应稍微不及时点。

##五：其它
1. 带超时时间的offer和poll也是一样的逻辑，只是使用的awaitNanos(long nanosTimeout)带超时的等待方法。
2. 接口里描述的addAll这种批量操作不保证原子性，因为本类并没有实现addAll，是使用的AbstractQueue的addAll方法，
循环调用的offer方法，所以可能其中一个元素报错导致后续都不再添加。

