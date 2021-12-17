#LinkedList源码分析

##一： 类注释及内部变量预览
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
     *   正好有一个（最后一个）节点的下一个引用为空，在入队时被 CASed。 
     *   最后一个节点可以在 O(1) 时间内从尾部到达，
     *   但尾部只是一种优化——它也总是可以在 O(N) 时间内从头部到达。
     * - The elements contained in the queue are the non-null items in
     *   Nodes that are reachable from head.  CASing the item
     *   reference of a Node to null atomically removes it from the
     *   queue.  Reachability of all elements from head must remain
     *   true even in the case of concurrent modifications that cause
     *   head to advance.  A dequeued Node may remain in use
     *   indefinitely due to creation of an Iterator or simply a
     *   poll() that has lost its time slice.
     *   队列中包含的元素是节点中可从头访问的非空项目。 
     *   将节点的项引用 CAS 为 null 会自动将其从队列中删除。 
     *   即使在导致 head 推进的并发修改的情况下，head 的所有元素的可达性也必须保持真实。 
     *   由于创建了一个迭代器或只是一个丢失了时间片的 poll()，
     *   出列的节点可能会无限期地继续使用。
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
     * 然而，只有未删除的节点才需要能够从出列的节点访问，
     * 并且访问性不一定必须是GC所理解的那种。
     * 我们使用的技巧是将刚出列的节点链接到自身。
     * 这样的自我链接隐含着向头部前进的意思。
     *
     * Both head and tail are permitted to lag.  In fact, failing to
     * update them every time one could is a significant optimization
     * (fewer CASes). As with LinkedTransferQueue (see the internal
     * documentation for that class), we use a slack threshold of two;
     * that is, we update head/tail when the current pointer appears
     * to be two or more steps away from the first/last node.
     * 头部和尾部都允许滞后。事实上，不每次都更新它们是一个显著的优化（少量的CASes）。
     * 与LinkedTransferQueue（参见该类的内部文档）一样，我们使用两个松弛阈值；
     * 也就是说，当当前指针距离第一个/最后一个节点两步或更多步时，我们更新head/tail。
     *
     * Since head and tail are updated concurrently and independently,
     * it is possible for tail to lag behind head (why not)?
     * 由于head和tail是同时独立更新的，tail有可能落后于head（为什么不）？
     *
     * CASing a Node's item reference to null atomically removes the
     * element from the queue.  Iterators skip over Nodes with null
     * items.  Prior implementations of this class had a race between
     * poll() and remove(Object) where the same element would appear
     * to be successfully removed by two concurrent operations.  The
     * method remove(Object) also lazily unlinks deleted Nodes, but
     * this is merely an optimization.
     * 将节点的项引用封装为null会自动从队列中删除元素。迭代器跳过包含空项的节点。
     * 该类以前的实现在poll()和remove(Object) 之间存在竞争，
     * 相同的元素似乎可以通过两个并发操作成功删除。
     * 方法remove(Object) 也会延迟地取消已删除节点的链接，但这只是一种优化。
     *
     * When constructing a Node (before enqueuing it) we avoid paying
     * for a volatile write to item by using Unsafe.putObject instead
     * of a normal write.  This allows the cost of enqueue to be
     * "one-and-a-half" CASes.
     * 在构造节点时（在排队之前），我们通过使用不安全的方法避免为易变的写入项付费。
     * putObject不是普通写入。这使得排队的成本为“一个半”CASes。
     *
     * Both head and tail may or may not point to a Node with a
     * non-null item.  If the queue is empty, all items must of course
     * be null.  Upon creation, both head and tail refer to a dummy
     * Node with null item.  Both head and tail are only updated using
     * CAS, so they never regress, although again this is merely an
     * optimization.
     * 头部和尾部都可能指向或不指向具有非空项的节点。如果队列为空，则所有项目当然必须为空。
     * 创建时，head和tail都引用带有null项的虚拟节点。
     * 头部和尾部都只使用CAS进行更新，所以它们永远不会回归，尽管这只是一个优化。
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

    /**
     * A node from which the first live (non-deleted) node (if any)
     * can be reached in O(1) time.
     * Invariants:
     * - all live nodes are reachable from head via succ()
     * - head != null
     * - (tmp = head).next != tmp || tmp != head
     * Non-invariants:
     * - head.item may or may not be null.
     * - it is permitted for tail to lag behind head, that is, for tail
     *   to not be reachable from head!
     */
    private transient volatile Node<E> head;

    /**
     * A node from which the last node on list (that is, the unique
     * node with node.next == null) can be reached in O(1) time.
     * Invariants:
     * - the last node is always reachable from tail via succ()
     * - tail != null
     * Non-invariants:
     * - tail.item may or may not be null.
     * - it is permitted for tail to lag behind head, that is, for tail
     *   to not be reachable from head!
     * - tail.next may or may not be self-pointing to tail.
     */
    private transient volatile Node<E> tail;

    /**
     * Creates a {@code ConcurrentLinkedQueue} that is initially empty.
     * head和tail都指向了一个空节点。
     */
    public ConcurrentLinkedQueue() {
        head = tail = new Node<E>(null);
    }

    //CAS操作更新tail
    private boolean casTail(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    //CAS操作更新head
    private boolean casHead(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

}
```
总结：
1. 基于双向链表实现的。
2. 线程安全的，非阻塞算法（通过CAS操作实现）。
3. 不允许为null。
4. 迭代器不会抛出ConcurrentModificationException。
5. 批量操作addAll、removeAll、retainAll等不能保证以原子方式执行。

实现注意点（先有这个概念，后面源码就方便看了）：
1. tail和head的cas更新操作允许滞后。
    - 1.1 比如说：不是每次offer都会更新tail节点。
    - 1.2 tail指针当前位置距离最后一个节点相隔两个或以上节点时，才更新tail（head也是类似）。
    - 1.3 cas的更细操作是消耗性能的，这样子可以少几次cas操作，是一种优化吧。
2. 因为1的存在，所以tail并不代表着真实的最后一个节点。
    - 2.1 如果tail.next为空，则tail是最后一个节点。
    - 2.2 如果tail.next不为空，则tail不是最后一个节点。
    - 2.3 只有一个节点的next为空，则该节点才是最后一个节点。
3. node.item为null，则表示该node已从链表中取下。
4. node.next==node，则表示该node已从链表中取下。


##二 offer逻辑
做以下思考再看源码会很容易理解。  
双向链表的入链逻辑无非两步，如果上锁，直接执行这两步就好：
1. tail.setNext(newNode) 将尾节点tail的next属性设置为newNode节点。
2. setTail(newNode) 将tail指向新节点newNode。

但是因为此类的特殊情况，导致情况就复杂了：
1. tail并不表示真正的最后一个节点。只有node.next==null 才表示是最后一个节点。
   所以要从tail往后偏移循环，找到真正的最后一个节点。偏移的过程中就可能会出现异常情况：
   比如说，偏移到了一个已经被下链的节点（node.next=node，此类的特点，节点的next指向自身表示下链），
   一路next循环获取节点，结果发现某个节点不再链表中了。
2. 此类是非阻塞的，但是线程安全，通过cas操作来处理的，多线程情况下，tail.casNext(null,newNode)是可能失败的

```java
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E>
        implements Queue<E>, java.io.Serializable {
    
    /**
     * Inserts the specified element at the tail of this queue.
     * As the queue is unbounded, this method will never return {@code false}.
     * 将指定的元素插入到queue的尾部。因为queue是无界的，此方法永远不会返回false。
     *
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        checkNotNull(e);
        final Node<E> newNode = new Node<E>(e);

        for (Node<E> t = tail, p = t;;) {
            //t固定为开始循环时的tail指向的一个Node，过程中tail指针可能会被其他线程更新为其它节点
            //p初始是tail指针位置，后续往next偏移或者根据不同的情况直接偏移到新的tail或者是head。
            //q固定为p的next节点。
            Node<E> q = p.next;
            if (q == null) {
                // p is last node
                // 一个节点的next为null，则表示是最后一个节点了。将新节点cas更新到p.next
                if (p.casNext(null, newNode)) {
                    // Successful CAS is the linearization point
                    // for e to become an element of this queue,
                    // and for newNode to become "live".
                    //新节点成功cas更新到了p.next上
                    
                    if (p != t) // hop two nodes at a time
                        // 先反过来想p=t表示什么，p初始定义是p=t，是相等的，不相等了说明p往next偏移了
                        // tail不是最后一个节点的时候（tail.next!=null），就需要往后偏移。
                        // 所以这里就会导致一次更新tail，一个不更新tail。体现了tail的更新是滞后的。
                        casTail(t, newNode);  // Failure is OK. 为啥失败也无所谓呢？因为tail本身就不表示最后一个节点
                    return true;
                }
                // Lost CAS race to another thread; re-read next
                // 如果走到了这里，说明两个线程再同时执行p.casNext(null, newNode)，
                //一个线程成功，另一个失败，则失败的线程就重新循环，重新读取 q=p.next
            }
            else if (p == q)
                // We have fallen off list.  If tail is unchanged, it
                // will also be off-list, in which case we need to
                // jump to head, from which all live nodes are always
                // reachable.  Else the new tail is a better bet.
                // p==q表示自己的next指向了自己，也就表示该节点已从链表中取下（poll中涉及到，后面再看）。
                // 但是这时候也需要将新节点加入到链表，所以一种方法是从tail重新偏移处理，一种是从头head开始偏移处理。
                // 1. 如果重新获取tail（t=tail）和循环之前的t不一样，说明有了新的tail，则从新的tail开始偏移处理。
                //    那为什么不怕新的tail也会被下链呢？无所谓，那样会再次走到这里再次处理。
                // 2. 如果说一样，则表示tail节点也可能被下链了，则从头head开始偏移，因为任何节点都可以从头head一路next访问到。
                p = (t != (t = tail)) ? t : head;
            else
                // Check for tail updates after two hops.
                // 这里说明tail.next不为空，那么p就往后偏移。但是偏移有两种可能：
                // 1：偏移到自身的next节点q。
                // 2: 直接偏移到新的tail指针位置。为啥要这种呢？因为当前的p节点离目前真实的tail相差太远了，
                // 如果每次next一个一个偏移也可以偏移过去，但是不如一次性偏移到新的tail来得快。
                p = (p != t && t != (t = tail)) ? t : q;
        }
    }
}
```

offer实现步骤：  
1. 从tail开始**循环**向后偏移，寻找链表中**真正的**最后一个节点（node.next==null）。
2. 循环的过程中发现某一个节点的next指向了自身（node.next==node）,说明该node被下链了。
   - 2.1 t!=(t=tail)表示目前真实的tail指向的节点和开始循环时的tail节点不相等，表示tail已被更新，则从新的tail再次偏移循环。
   - 2.2 t=(t=tail)表示开始循环是的tail节点也可能下链了，则从头head再开始偏移循环。
3. 循环的过程中发现t!=(t=tail)，则直接从新的tail开始循环（依次循环next也可以达到，但是可能会多循环好多次）。
4. 找到了**真正的**最后一个节点后，cas更新入链，node.casNext(null, newNode)。
5. casTail更新tail指针，隔一次更新一次，并不每次offer都更新，少一次cas更新，性能会更好一点。



##三：poll逻辑
通过offer考虑到这些相同的情况，poll的逻辑也很类似。  
```java
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E>
        implements Queue<E>, java.io.Serializable {
   
    public E poll() {
      restartFromHead:
      //双层循环，因为内部的循环可能也会发现偏移到了已下链的节点，需要再次从头head开始循环
      for (;;) {
         //从头head节点开始循环，找到真实的第一个节点。本类的特性（head允许滞后更新，head不一定表示真实的第一个节点）
         for (Node<E> h = head, p = h, q;;) {
            E item = p.item;
            
            // item!=null 表示是真实的第一个节点，然后将其item cas更新为null表示下链。
            if (item != null && p.casItem(item, null)) {
               // Successful CAS is the linearization point
               // for item to be removed from this queue.
               if (p != h) // hop two nodes at a time
                   //隔一次更新依次head，也体现出了head的更新是滞后的。
                   // p.next==null 表示已经是最后一个节点了，所以将头更新为p，不能将头更新为null。
                  updateHead(h, ((q = p.next) != null) ? q : p);
               return item;
            }
            else if ((q = p.next) == null) {
                //p.next==null 说明该节点是最后一个节点了，到最后了。
               updateHead(h, p);
               return null;
            }
            else if (p == q)
                //节点的next指向了自身，表示该节点已经被下链，则重新从头head开始循环。
               continue restartFromHead;
            else
                //将p向后偏移到自己的next节点，然后再次循环
               p = q;
         }
      }
   }

   /**
    * Tries to CAS head to p. If successful, repoint old head to itself
    * as sentinel for succ(), below.
    */
   final void updateHead(Node<E> h, Node<E> p) {
      if (h != p && casHead(h, p))
         h.lazySetNext(h);
   }
}
```