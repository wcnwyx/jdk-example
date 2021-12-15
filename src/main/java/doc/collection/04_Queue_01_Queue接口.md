```java
/**
 * A collection designed for holding elements prior to processing.
 * Besides basic {@link java.util.Collection Collection} operations,
 * queues provide additional insertion, extraction, and inspection
 * operations.  Each of these methods exists in two forms: one throws
 * an exception if the operation fails, the other returns a special
 * value (either {@code null} or {@code false}, depending on the
 * operation).  The latter form of the insert operation is designed
 * specifically for use with capacity-restricted {@code Queue}
 * implementations; in most implementations, insert operations cannot
 * fail.
 * 设计用于在处理前保存元素的集合。除了基本的Collection操作外，队列还提供附加的插入、提取和检查操作。
 * 这些方法都有两种形式：一种是在操作失败时抛出异常，另一种是返回特殊值（null或false，具体取决于操作）。
 * 插入操作的后一种形式专门设计用于容量受限的Queue实现；在大多数实现中，插入操作不会失败。
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>Summary of Queue methods</caption>
 *  <tr>
 *    <td></td>
 *    <td ALIGN=CENTER><em>Throws exception</em></td>
 *    <td ALIGN=CENTER><em>Returns special value</em></td>
 *  </tr>
 *  <tr>
 *    <td><b>Insert</b></td>
 *    <td>{@link Queue#add add(e)}</td>
 *    <td>{@link Queue#offer offer(e)}</td>
 *  </tr>
 *  <tr>
 *    <td><b>Remove</b></td>
 *    <td>{@link Queue#remove remove()}</td>
 *    <td>{@link Queue#poll poll()}</td>
 *  </tr>
 *  <tr>
 *    <td><b>Examine</b></td>
 *    <td>{@link Queue#element element()}</td>
 *    <td>{@link Queue#peek peek()}</td>
 *  </tr>
 * </table>
 * 抛出异常的操作为：add(e)、remove()、element()
 * 返回固定值的操作为：offer(e)、poll()、peek()
 *
 * <p>Queues typically, but do not necessarily, order elements in a
 * FIFO (first-in-first-out) manner.  Among the exceptions are
 * priority queues, which order elements according to a supplied
 * comparator, or the elements' natural ordering, and LIFO queues (or
 * stacks) which order the elements LIFO (last-in-first-out).
 * Whatever the ordering used, the <em>head</em> of the queue is that
 * element which would be removed by a call to {@link #remove() } or
 * {@link #poll()}.  In a FIFO queue, all new elements are inserted at
 * the <em>tail</em> of the queue. Other kinds of queues may use
 * different placement rules.  Every {@code Queue} implementation
 * must specify its ordering properties.
 * 队列通常（但不一定）以FIFO（先进先出）的方式对元素进行排序。
 * 例外情况包括优先级队列（根据提供的比较器或元素的自然顺序对元素排序）和LIFO（后进先出）队列（或堆栈）。
 * 无论使用何种顺序，队列的头部都是通过调用remove()或poll()删除的元素。
 * 在FIFO队列中，所有新元素都插入到队列的尾部。其他类型的队列可能使用不同的放置规则。
 * 每个队列实现都必须指定其排序属性。
 *
 * <p>The {@link #offer offer} method inserts an element if possible,
 * otherwise returning {@code false}.  This differs from the {@link
 * java.util.Collection#add Collection.add} method, which can fail to
 * add an element only by throwing an unchecked exception.  The
 * {@code offer} method is designed for use when failure is a normal,
 * rather than exceptional occurrence, for example, in fixed-capacity
 * (or &quot;bounded&quot;) queues.
 * 如果可能，offer方法将插入一个元素，否则返回false。
 * 这与 Collection.add 方法不同，后者只能通过抛出未经检查的异常来添加元素失败。 
 * offer 方法设计用于当故障是正常情况而非异常情况时使用，例如，在固定容量（或“有界”）队列中。
 *
 * <p>The {@link #remove()} and {@link #poll()} methods remove and
 * return the head of the queue.
 * Exactly which element is removed from the queue is a
 * function of the queue's ordering policy, which differs from
 * implementation to implementation. The {@code remove()} and
 * {@code poll()} methods differ only in their behavior when the
 * queue is empty: the {@code remove()} method throws an exception,
 * while the {@code poll()} method returns {@code null}.
 * remove() 和 poll() 方法移除并返回队列的头部。 
 * 从队列中移除哪个元素是队列排序策略的函数，它因实现而异。 
 * remove() 和 poll() 方法仅在队列为空时的行为不同：
 * remove() 方法抛出异常，而 poll() 方法返回 null。
 *
 * <p>The {@link #element()} and {@link #peek()} methods return, but do
 * not remove, the head of the queue.
 * element() 和 peek() 方法返回但不移除队列的头部。
 *
 * <p>The {@code Queue} interface does not define the <i>blocking queue
 * methods</i>, which are common in concurrent programming.  These methods,
 * which wait for elements to appear or for space to become available, are
 * defined in the {@link java.util.concurrent.BlockingQueue} interface, which
 * extends this interface.
 * Queue 接口没有定义阻塞队列方法，这些方法在并发编程中很常见。 
 * 这些等待元素出现或空间可用的方法在 BlockingQueue 接口中定义，该接口扩展了该接口。
 *
 * <p>{@code Queue} implementations generally do not allow insertion
 * of {@code null} elements, although some implementations, such as
 * {@link LinkedList}, do not prohibit insertion of {@code null}.
 * Even in the implementations that permit it, {@code null} should
 * not be inserted into a {@code Queue}, as {@code null} is also
 * used as a special return value by the {@code poll} method to
 * indicate that the queue contains no elements.
 * Queue实现通常不允许插入空元素，尽管某些实现（例如 LinkedList）不禁止插入空元素。 
 * 即使在允许它的实现中，也不应将 null 插入到队列中，
 * 因为 null 也被 poll 方法用作特殊的返回值，以指示队列不包含任何元素。
 *
 * <p>{@code Queue} implementations generally do not define
 * element-based versions of methods {@code equals} and
 * {@code hashCode} but instead inherit the identity based versions
 * from class {@code Object}, because element-based equality is not
 * always well-defined for queues with the same elements but different
 * ordering properties.
 * 队列实现通常不定义方法 equals 和 hashCode 的基于元素的版本，
 * 而是从类 Object 继承基于身份的版本，因为对于具有相同元素但不同排序属性的队列，
 * 基于元素的相等性并不总是很好定义。
 *
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @see java.util.Collection
 * @see LinkedList
 * @see PriorityQueue
 * @see java.util.concurrent.LinkedBlockingQueue
 * @see java.util.concurrent.BlockingQueue
 * @see java.util.concurrent.ArrayBlockingQueue
 * @see java.util.concurrent.LinkedBlockingQueue
 * @see java.util.concurrent.PriorityBlockingQueue
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public interface Queue<E> extends Collection<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * {@code true} upon success and throwing an {@code IllegalStateException}
     * if no space is currently available.
     * 如果可以在不违反容量限制的情况下立即将指定元素插入此队列，则在成功时返回 true，
     * 如果当前没有可用空间则抛出 IllegalStateException。
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
    boolean add(E e);

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions.
     * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
     * 如果可以在不违反容量限制的情况下立即将指定元素插入此队列。 
     * 当使用容量受限的队列时，这种方法通常比 add(E) 更可取，add只能通过抛出异常来插入元素失败。
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
    boolean offer(E e);

    /**
     * Retrieves and removes the head of this queue.  This method differs
     * from {@link #poll poll} only in that it throws an exception if this
     * queue is empty.
     * 检索并删除此队列的头部。 此方法与 poll 的不同之处仅在于，如果此队列为空，它会引发异常。
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    E remove();

    /**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     * 检索并移除此队列的头部，如果此队列为空，则返回 null。
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    E poll();

    /**
     * Retrieves, but does not remove, the head of this queue.  This method
     * differs from {@link #peek peek} only in that it throws an exception
     * if this queue is empty.
     * 检索但不删除此队列的头部。 此方法与 peek 的不同之处仅在于，如果此队列为空，它会引发异常。
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    E element();

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     * 检索但不删除此队列的头部，如果此队列为空，则返回 null。
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    E peek();
}
```