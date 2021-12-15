```java
/**
 * An unbounded thread-safe {@linkplain Queue queue} based on linked nodes.
 * This queue orders elements FIFO (first-in-first-out).
 * The <em>head</em> of the queue is that element that has been on the
 * queue the longest time.
 * The <em>tail</em> of the queue is that element that has been on the
 * queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * A {@code ConcurrentLinkedQueue} is an appropriate choice when
 * many threads will share access to a common collection.
 * Like most other concurrent collection implementations, this class
 * does not permit the use of {@code null} elements.
 * 基于链接节点的无界线程安全队列。 此队列对元素 FIFO（先进先出）进行排序。 
 * 队列的头部是在队列中停留时间最长的那个元素。 
 * 队列的尾部是在队列中停留时间最短的那个元素。 
 * 新元素插入队列尾部，队列检索操作获取队列头部元素。 
 * 当许多线程将共享对公共集合的访问时， ConcurrentLinkedQueue 是合适的选择。 
 * 与大多数其他并发集合实现一样，此类不允许使用null元素。
 *
 * <p>This implementation employs an efficient <em>non-blocking</em>
 * algorithm based on one described in <a
 * href="http://www.cs.rochester.edu/u/michael/PODC96.html"> Simple,
 * Fast, and Practical Non-Blocking and Blocking Concurrent Queue
 * Algorithms</a> by Maged M. Michael and Michael L. Scott.
 * 该实现采用了一种高效的非阻塞算法，
 * 该算法基于 Maged M. Michael 和 Michael L. Scott 
 * 在 Simple、Fast、Practical Non-Blocking and Blocking Concurrent Queue Algorithms 中描述的算法。
 *
 * <p>Iterators are <i>weakly consistent</i>, returning elements
 * reflecting the state of the queue at some point at or since the
 * creation of the iterator.  They do <em>not</em> throw {@link
 * java.util.ConcurrentModificationException}, and may proceed concurrently
 * with other operations.  Elements contained in the queue since the creation
 * of the iterator will be returned exactly once.
 * 迭代器是弱一致的，返回元素反映了在迭代器创建时或之后的某个时间点的队列状态。 
 * 它们不会抛出 ConcurrentModificationException，并且可以与其他操作同时进行。 
 * 自迭代器创建以来包含在队列中的元素将只返回一次。
 *
 * <p>Beware that, unlike in most collections, the {@code size} method
 * is <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these queues, determining the current number
 * of elements requires a traversal of the elements, and so may report
 * inaccurate results if this collection is modified during traversal.
 * Additionally, the bulk operations {@code addAll},
 * {@code removeAll}, {@code retainAll}, {@code containsAll},
 * {@code equals}, and {@code toArray} are <em>not</em> guaranteed
 * to be performed atomically. For example, an iterator operating
 * concurrently with an {@code addAll} operation might view only some
 * of the added elements.
 * 请注意，与大多数集合不同，size 方法不是恒定时间操作。 
 * 由于这些队列的异步特性，确定当前元素的数量需要遍历元素，
 * 因此如果在遍历期间修改此集合，则可能会报告不准确的结果。 
 * 此外，批量操作 addAll、removeAll、retainAll、containsAll、equals 
 * 和 toArray 不能保证以原子方式执行。 
 * 例如，与 addAll 操作同时运行的迭代器可能只查看一些已添加的元素。
 *
 * <p>This class and its iterator implement all of the <em>optional</em>
 * methods of the {@link Queue} and {@link Iterator} interfaces.
 * 此类及其迭代器实现了 Queue 和 Iterator 接口的所有可选方法。
 *
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code ConcurrentLinkedQueue}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code ConcurrentLinkedQueue} in another thread.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E>
        implements Queue<E>, java.io.Serializable {

    /*
     * This is a modification of the Michael & Scott algorithm,
     * adapted for a garbage-collected environment, with support for
     * interior node deletion (to support remove(Object)).  For
     * explanation, read the paper.
     * 这是 Michael & Scott 算法的修改，适用于垃圾收集环境，
     * 支持内部节点删除（以支持 remove(Object)）。 有关解释，请阅读论文。
     *
     * Note that like most non-blocking algorithms in this package,
     * this implementation relies on the fact that in garbage
     * collected systems, there is no possibility of ABA problems due
     * to recycled nodes, so there is no need to use "counted
     * pointers" or related techniques seen in versions used in
     * non-GC'ed settings.
     * 请注意，与此包中的大多数非阻塞算法一样，此实现依赖于这样一个事实：
     * 在垃圾收集系统中，不存在因回收节点而导致 ABA 问题的可能性，
     * 因此无需使用“计数指针”或相关技术 在非 GC 设置中使用的版本中看到。
     *
     * The fundamental invariants are:
     * 基本不变量是：
     * - There is exactly one (last) Node with a null next reference,
     *   which is CASed when enqueueing.  This last Node can be
     *   reached in O(1) time from tail, but tail is merely an
     *   optimization - it can always be reached in O(N) time from
     *   head as well.
     * 正好有一个（最后一个）节点的下一个引用为空，在入队时被 CASed。 
     * 最后一个节点可以在 O(1) 时间内从尾部到达，
     * 但尾部只是一种优化——它也总是可以在 O(N) 时间内从头部到达。
     * - The elements contained in the queue are the non-null items in
     *   Nodes that are reachable from head.  CASing the item
     *   reference of a Node to null atomically removes it from the
     *   queue.  Reachability of all elements from head must remain
     *   true even in the case of concurrent modifications that cause
     *   head to advance.  A dequeued Node may remain in use
     *   indefinitely due to creation of an Iterator or simply a
     *   poll() that has lost its time slice.
     * 队列中包含的元素是节点中可从头访问的非空项目。 
     * 将节点的项引用 CAS 为 null 会自动将其从队列中删除。 
     * 即使在导致 head 推进的并发修改的情况下，head 的所有元素的可达性也必须保持真实。 
     * 由于创建了一个迭代器或只是一个丢失了时间片的 poll()，
     * 出列的节点可能会无限期地继续使用。
     *
     * The above might appear to imply that all Nodes are GC-reachable
     * from a predecessor dequeued Node.  That would cause two problems:
     * 上面可能暗示所有节点都可以从前一个已排队的节点进行GC访问。这将导致两个问题：
     * - allow a rogue Iterator to cause unbounded memory retention
     *   允许恶意迭代器导致无限内存保留
     * - cause cross-generational linking of old Nodes to new Nodes if
     *   a Node was tenured while live, which generational GCs have a
     *   hard time dealing with, causing repeated major collections.
     *   如果某个节点在使用期间是长期存在的，则会导致旧节点与新节点的跨代链接，
     *   这一点各代GCs很难处理，从而导致重复的主要收集。
     * However, only non-deleted Nodes need to be reachable from
     * dequeued Nodes, and reachability does not necessarily have to
     * be of the kind understood by the GC.  We use the trick of
     * linking a Node that has just been dequeued to itself.  Such a
     * self-link implicitly means to advance to head.
     *
     * Both head and tail are permitted to lag.  In fact, failing to
     * update them every time one could is a significant optimization
     * (fewer CASes). As with LinkedTransferQueue (see the internal
     * documentation for that class), we use a slack threshold of two;
     * that is, we update head/tail when the current pointer appears
     * to be two or more steps away from the first/last node.
     *
     * Since head and tail are updated concurrently and independently,
     * it is possible for tail to lag behind head (why not)?
     *
     * CASing a Node's item reference to null atomically removes the
     * element from the queue.  Iterators skip over Nodes with null
     * items.  Prior implementations of this class had a race between
     * poll() and remove(Object) where the same element would appear
     * to be successfully removed by two concurrent operations.  The
     * method remove(Object) also lazily unlinks deleted Nodes, but
     * this is merely an optimization.
     *
     * When constructing a Node (before enqueuing it) we avoid paying
     * for a volatile write to item by using Unsafe.putObject instead
     * of a normal write.  This allows the cost of enqueue to be
     * "one-and-a-half" CASes.
     *
     * Both head and tail may or may not point to a Node with a
     * non-null item.  If the queue is empty, all items must of course
     * be null.  Upon creation, both head and tail refer to a dummy
     * Node with null item.  Both head and tail are only updated using
     * CAS, so they never regress, although again this is merely an
     * optimization.
     */

    private static class Node<E> {
        volatile E item;
        volatile Node<E> next;

        /**
         * Constructs a new node.  Uses relaxed write because item can
         * only be seen after publication via casNext.
         */
        Node(E item) {
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(Node<E> val) {
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(Node<E> cmp, Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        // Unsafe mechanics

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> k = Node.class;
                itemOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
}
```