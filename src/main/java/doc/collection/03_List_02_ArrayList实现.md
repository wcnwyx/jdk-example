```java
/**
 * Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.  In addition to implementing the <tt>List</tt> interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * <tt>Vector</tt>, except that it is unsynchronized.)
 * List接口的可调整大小的数组实现。实现所有可选的列表操作，并允许所有元素，包括null。
 * 除了实现列表接口外，此类还提供了一些方法来操作内部用于存储列表的数组的大小。
 * （该类大致相当于Vector，只是不同步。）
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the <tt>LinkedList</tt> implementation.
 * size、isEmpty、get、set、iterator和listIterator操作以固定时间运行。
 * add操作在amortized（摊余）固定时间内运行，即添加n个元素需要O(n)的时间。
 * 所有其他操作都在线性时间内运行（粗略地说）。与LinkedList实现相比，常数因子较低。
 *
 * <p>Each <tt>ArrayList</tt> instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 * 每个ArrayList实例都有一个容量。容量是用于存储列表中元素的数组的大小。
 * 它始终至少与列表大小一样大。当元素添加到ArrayList时，其容量会自动增长。
 * 除了添加要素具有恒定的amortized（摊余）时间成本这一事实之外，没有详细说明增长政策。
 *
 * <p>An application can increase the capacity of an <tt>ArrayList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation.  This may reduce the amount of incremental reallocation.
 * 应用程序可以使用ensureCapacity操作在添加大量元素之前增加ArrayList实例的容量。
 * 这可能会减少增量重新分配的数量。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an <tt>ArrayList</tt> instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 * 请注意，此实现是不同步的。
 * 如果多个线程同时访问ArrayList实例，并且至少有一个线程在结构上修改该列表，则必须在外部对其进行同步。
 * （结构修改是添加或删除一个或多个元素，或显式调整支持数组大小的任何操作；仅设置元素的值不是结构修改。）
 * 这通常通过在自然封装列表的某个对象上进行同步来实现。
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 * 如果不存在这样的对象，则应使用Collections.synchronizedList“包装”列表。
 * 最好在创建时执行此操作，以防止意外不同步地访问列表：
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre>
 *
 * <p><a name="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:</a>
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 * 这个类的迭代器和listIterator方法返回的迭代器是快速失效的：
 * 如果在迭代器创建后的任何时候，列表在结构上被修改，除了通过迭代器自己的remove或add方法，
 * 迭代器将抛出ConcurrentModificationException。因此，在面对并发修改时，
 * 迭代器会快速、干净地失败，而不是在将来的不确定时间冒着任意、不确定行为的风险。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * 请注意，无法保证迭代器的快速失效行为，因为一般来说，在存在非同步并发修改的情况下，
 * 不可能做出任何硬保证。快速失败迭代器会尽最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的正确性的程序是错误的：迭代器的快速失败行为应该只用于检测bug。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Collection
 * @see     List
 * @see     LinkedList
 * @see     Vector
 * @since   1.2
 */

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    
}
```