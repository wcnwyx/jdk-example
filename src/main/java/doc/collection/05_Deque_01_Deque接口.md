```java
/**
 * A linear collection that supports element insertion and removal at
 * both ends.  The name <i>deque</i> is short for "double ended queue"
 * and is usually pronounced "deck".  Most {@code Deque}
 * implementations place no fixed limits on the number of elements
 * they may contain, but this interface supports capacity-restricted
 * deques as well as those with no fixed size limit.
 * 支持两端元素插入和移除的线性集合。
 * 名称deque是"double ended queue（双端队列）"的缩写，通常发音为"deck"。 
 * 大多数Deque实现对它们可能包含的元素数量没有固定限制，
 * 但此接口支持容量受限的双端队列以及没有固定大小限制的双端队列。
 *
 * <p>This interface defines methods to access the elements at both
 * ends of the deque.  Methods are provided to insert, remove, and
 * examine the element.  Each of these methods exists in two forms:
 * one throws an exception if the operation fails, the other returns a
 * special value (either {@code null} or {@code false}, depending on
 * the operation).  The latter form of the insert operation is
 * designed specifically for use with capacity-restricted
 * {@code Deque} implementations; in most implementations, insert
 * operations cannot fail.
 * 该接口定义了访问双端队列两端元素的方法。 提供了插入、删除和检查元素的方法。 
 * 这些方法中的每一个都以两种形式存在：
 * 一种在操作失败时抛出异常，另一种返回一个特殊值（null或flase，取决于操作）。 
 * 后一种形式的插入操作是专门为与容量受限的 Deque 实现一起使用而设计的； 
 * 在大多数实现中，插入操作不会失败。
 *
 * <p>The twelve methods described above are summarized in the
 * following table:
 * 上述十二种方法总结在下表中：
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>Summary of Deque methods</caption>
 *  <tr>
 *    <td></td>
 *    <td ALIGN=CENTER COLSPAN = 2> <b>First Element (Head)</b></td>
 *    <td ALIGN=CENTER COLSPAN = 2> <b>Last Element (Tail)</b></td>
 *  </tr>
 *  <tr>
 *    <td></td>
 *    <td ALIGN=CENTER><em>Throws exception</em></td>
 *    <td ALIGN=CENTER><em>Special value</em></td>
 *    <td ALIGN=CENTER><em>Throws exception</em></td>
 *    <td ALIGN=CENTER><em>Special value</em></td>
 *  </tr>
 *  <tr>
 *    <td><b>Insert</b></td>
 *    <td>{@link Deque#addFirst addFirst(e)}</td>
 *    <td>{@link Deque#offerFirst offerFirst(e)}</td>
 *    <td>{@link Deque#addLast addLast(e)}</td>
 *    <td>{@link Deque#offerLast offerLast(e)}</td>
 *  </tr>
 *  <tr>
 *    <td><b>Remove</b></td>
 *    <td>{@link Deque#removeFirst removeFirst()}</td>
 *    <td>{@link Deque#pollFirst pollFirst()}</td>
 *    <td>{@link Deque#removeLast removeLast()}</td>
 *    <td>{@link Deque#pollLast pollLast()}</td>
 *  </tr>
 *  <tr>
 *    <td><b>Examine</b></td>
 *    <td>{@link Deque#getFirst getFirst()}</td>
 *    <td>{@link Deque#peekFirst peekFirst()}</td>
 *    <td>{@link Deque#getLast getLast()}</td>
 *    <td>{@link Deque#peekLast peekLast()}</td>
 *  </tr>
 * </table>
 *
 * <p>This interface extends the {@link Queue} interface.  When a deque is
 * used as a queue, FIFO (First-In-First-Out) behavior results.  Elements are
 * added at the end of the deque and removed from the beginning.  The methods
 * inherited from the {@code Queue} interface are precisely equivalent to
 * {@code Deque} methods as indicated in the following table:
 * 该接口扩展了 Queue 接口。 当deque用作queue时，会产生 FIFO（先进先出）行为。 
 * 元素添加在双端队列的末尾并从开头删除。 
 * 从 Queue 接口继承的方法与 Deque 方法完全等效，如下表所示：
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>Comparison of Queue and Deque methods</caption>
 *  <tr>
 *    <td ALIGN=CENTER> <b>{@code Queue} Method</b></td>
 *    <td ALIGN=CENTER> <b>Equivalent {@code Deque} Method</b></td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#add add(e)}</td>
 *    <td>{@link #addLast addLast(e)}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#offer offer(e)}</td>
 *    <td>{@link #offerLast offerLast(e)}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#remove remove()}</td>
 *    <td>{@link #removeFirst removeFirst()}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#poll poll()}</td>
 *    <td>{@link #pollFirst pollFirst()}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#element element()}</td>
 *    <td>{@link #getFirst getFirst()}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link java.util.Queue#peek peek()}</td>
 *    <td>{@link #peek peekFirst()}</td>
 *  </tr>
 * </table>
 *
 * <p>Deques can also be used as LIFO (Last-In-First-Out) stacks.  This
 * interface should be used in preference to the legacy {@link Stack} class.
 * When a deque is used as a stack, elements are pushed and popped from the
 * beginning of the deque.  Stack methods are precisely equivalent to
 * {@code Deque} methods as indicated in the table below:
 * 双端队列也可以用作 LIFO（后进先出）堆栈。 应优先使用此接口而不是遗留 Stack 类。 
 * 当双端队列用作堆栈时，元素从双端队列的开头被压入和弹出。 
 * 堆栈方法与 Deque 方法完全等效，如下表所示：
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>Comparison of Stack and Deque methods</caption>
 *  <tr>
 *    <td ALIGN=CENTER> <b>Stack Method</b></td>
 *    <td ALIGN=CENTER> <b>Equivalent {@code Deque} Method</b></td>
 *  </tr>
 *  <tr>
 *    <td>{@link #push push(e)}</td>
 *    <td>{@link #addFirst addFirst(e)}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link #pop pop()}</td>
 *    <td>{@link #removeFirst removeFirst()}</td>
 *  </tr>
 *  <tr>
 *    <td>{@link #peek peek()}</td>
 *    <td>{@link #peekFirst peekFirst()}</td>
 *  </tr>
 * </table>
 *
 * <p>Note that the {@link #peek peek} method works equally well when
 * a deque is used as a queue or a stack; in either case, elements are
 * drawn from the beginning of the deque.
 * 请注意，当deque用作queue或stack时，peek 方法同样有效； 
 * 在任何一种情况下，元素都是从deque的开头绘制的。
 *
 * <p>This interface provides two methods to remove interior
 * elements, {@link #removeFirstOccurrence removeFirstOccurrence} and
 * {@link #removeLastOccurrence removeLastOccurrence}.
 * 该接口提供了两种移除内部元素的方法，removeFirstOccurrence 和 removeLastOccurrence。
 *
 * <p>Unlike the {@link List} interface, this interface does not
 * provide support for indexed access to elements.
 * 与 List 接口不同，此接口不支持对元素进行索引访问。
 *
 * <p>While {@code Deque} implementations are not strictly required
 * to prohibit the insertion of null elements, they are strongly
 * encouraged to do so.  Users of any {@code Deque} implementations
 * that do allow null elements are strongly encouraged <i>not</i> to
 * take advantage of the ability to insert nulls.  This is so because
 * {@code null} is used as a special return value by various methods
 * to indicated that the deque is empty.
 * 虽然 Deque 实现并没有严格要求禁止插入 null 元素，但强烈鼓励他们这样做。 
 * 强烈建议任何允许空元素的 Deque 实现的用户不要利用插入空值的能力。 
 * 之所以如此，是因为 null 被各种方法用作特殊的返回值，以指示双端队列为空。
 *
 * <p>{@code Deque} implementations generally do not define
 * element-based versions of the {@code equals} and {@code hashCode}
 * methods, but instead inherit the identity-based versions from class
 * {@code Object}.
 * Deque 实现通常不定义 equals 和 hashCode 方法的基于元素的版本，
 * 而是从类 Object 继承基于身份的版本。
 *
 * <p>This interface is a member of the <a
 * href="{@docRoot}/../technotes/guides/collections/index.html"> Java Collections
 * Framework</a>.
 *
 * @author Doug Lea
 * @author Josh Bloch
 * @since  1.6
 * @param <E> the type of elements held in this collection
 */
public interface Deque<E> extends Queue<E> {
    /**
     * Inserts the specified element at the front of this deque if it is
     * possible to do so immediately without violating capacity restrictions,
     * throwing an {@code IllegalStateException} if no space is currently
     * available.  When using a capacity-restricted deque, it is generally
     * preferable to use method {@link #offerFirst}.
     * 如果可以在不违反容量限制的情况下立即插入指定的元素，则在此双端队列的前面插入指定的元素，
     * 如果当前没有可用空间，则抛出 IllegalStateException。 
     * 当使用容量受限的双端队列时，通常最好使用方法 offerFirst(E)。
     *
     * @param e the element to add
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this deque
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    void addFirst(E e);

    //类似于addFirst， 从后面插入元素
    void addLast(E e);

    /**
     * Inserts the specified element at the front of this deque unless it would
     * violate capacity restrictions.  When using a capacity-restricted deque,
     * this method is generally preferable to the {@link #addFirst} method,
     * which can fail to insert an element only by throwing an exception.
     * 在此双端队列的前面插入指定的元素，除非它违反容量限制。 
     * 当使用容量受限的双端队列时，此方法通常比 addFirst(E) 方法更可取，
     * 后者可能仅通过抛出异常而无法插入元素。
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this deque, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this deque
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    boolean offerFirst(E e);

    //类似于offerFirst，从后面插入元素
    boolean offerLast(E e);

    /**
     * Retrieves and removes the first element of this deque.  This method
     * differs from {@link #pollFirst pollFirst} only in that it throws an
     * exception if this deque is empty.
     * 检索并删除此双端队列的第一个元素。 
     * 此方法与 pollFirst 的不同之处仅在于，如果此双端队列为空，它会引发异常。
     *
     * @return the head of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    E removeFirst();

    E removeLast();

    /**
     * Retrieves and removes the first element of this deque,
     * or returns {@code null} if this deque is empty.
     * 检索并删除此双端队列的第一个元素，如果此双端队列为空，则返回 null。
     *
     * @return the head of this deque, or {@code null} if this deque is empty
     */
    E pollFirst();

    E pollLast();

    /**
     * Retrieves, but does not remove, the first element of this deque.
     * 检索但不删除此双端队列的第一个元素。 
     *
     * This method differs from {@link #peekFirst peekFirst} only in that it
     * throws an exception if this deque is empty.
     * 此方法与 peekFirst 的不同之处仅在于，如果此双端队列为空，它会引发异常。
     *
     * @return the head of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    E getFirst();

    E getLast();

    /**
     * Retrieves, but does not remove, the first element of this deque,
     * or returns {@code null} if this deque is empty.
     * 检索但不删除此双端队列的第一个元素，如果队列为空则返回null。
     *
     * @return the head of this deque, or {@code null} if this deque is empty
     */
    E peekFirst();

    E peekLast();

    /**
     * Removes the first occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>
     * (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     * 从此双端队列中删除第一次出现的指定元素。 如果 deque 不包含该元素，则它保持不变。 
     * 更正式地，删除第一个元素 e 使得 (o==null ? e==null : o.equals(e)) （如果这样的元素存在）。 
     * 如果此双端队列包含指定的元素（或等效地，如果此双端队列因调用而更改），则返回 true。
     *
     * @param o element to be removed from this deque, if present
     * @return {@code true} if an element was removed as a result of this call
     * @throws ClassCastException if the class of the specified element
     *         is incompatible with this deque
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean removeFirstOccurrence(Object o);

    boolean removeLastOccurrence(Object o);

    // *** Queue methods ***
    boolean add(E e);
    boolean offer(E e);
    E remove();
    E poll();
    E element();
    E peek();

    // *** Stack methods ***
    void push(E e);
    E pop();

    // *** Collection methods ***
    boolean remove(Object o);
    boolean contains(Object o);
    public int size();
    Iterator<E> iterator();
    Iterator<E> descendingIterator();
}
```
1. 因为是双端队列，所以相应的添加删除都需要制定从前还是从后，所以都有两套方法，例如addFirst、addLast。
2. 双端队列也可以打瘸了用作queue和stack。只不过需要选择相应的进出方向而已。
3. 前面看过的List接口实现类LinkedList也是Deque的实现，基于双线链表的实现。
4. Deque也有对应的子接口BlockingDeque，和Queue-BlockingQueue一样的关系。
5. Deque的实现类和Queue的也类似：
    - 5.1： ConcurrentLinkedDeque（类似于ConcurrentLinkedQueue）
    - 5.2: LinkedBlockingDeque(类似于LinkedBlockingQueue)
    - 5.3: ArrayDeque基于数组的实现