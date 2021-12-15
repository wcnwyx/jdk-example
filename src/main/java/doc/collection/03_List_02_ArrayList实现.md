#ArrayList源码分析

##一： 类注释及内部变量预览
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

    /**
     * Default initial capacity.
     * 默认初始容量。
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     * 用于空实例的共享空数组实例。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     * 用于默认大小的空实例的共享空数组实例。
     * 我们将其与EMPTY_ELEMENTDATA 区分开来，以了解添加第一个元素时要膨胀多少。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     * 存储ArrayList元素的数组缓冲区。ArrayList的容量是此数组缓冲区的长度。
     * 添加第一个元素时，任何空ArrayList(elementData==DEFAULTCAPACITY_EMPTY_ELEMENTDATA)都将扩展为DEFAULT_CAPACITY。
     */
    transient Object[] elementData; // non-private to simplify nested class access 非私有以简化内部嵌套类访问

    /**
     * The size of the ArrayList (the number of elements it contains).
     * ArrayList的大小（它包含的元素数）。
     * @serial
     */
    private int size;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     * 可以分配的最大数组大小。
     * 有些虚拟机在数组中保留一些头。
     * 尝试分配较大的数组可能会导致OutOfMemoryError：请求的数组大小超过VM限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The number of times this list has been <i>structurally modified</i>.
     * Structural modifications are those that change the size of the
     * list, or otherwise perturb it in such a fashion that iterations in
     * progress may yield incorrect results.
     * 此列表在结构上被修改的次数。结构修改是指那些改变列表大小的修改，或者以某种方式干扰列表，
     * 使得正在进行的迭代可能产生不正确的结果。
     *
     * <p>This field is used by the iterator and list iterator implementation
     * returned by the {@code iterator} and {@code listIterator} methods.
     * If the value of this field changes unexpectedly, the iterator (or list
     * iterator) will throw a {@code ConcurrentModificationException} in
     * response to the {@code next}, {@code remove}, {@code previous},
     * {@code set} or {@code add} operations.  This provides
     * <i>fail-fast</i> behavior, rather than non-deterministic behavior in
     * the face of concurrent modification during iteration.
     * 该字段由iterator和listIterator方法返回的迭代器和列表迭代器实现使用。
     * 如果此字段的值意外更改，iterator（或list iterator）将抛出ConcurrentModificationException，
     * 以响应next、remove、previous、set或add操作。
     * 这提供了快速失效行为，而不是在迭代过程中面对并发修改时的非确定性行为。
     *
     * <p><b>Use of this field by subclasses is optional.</b> If a subclass
     * wishes to provide fail-fast iterators (and list iterators), then it
     * merely has to increment this field in its {@code add(int, E)} and
     * {@code remove(int)} methods (and any other methods that it overrides
     * that result in structural modifications to the list).  A single call to
     * {@code add(int, E)} or {@code remove(int)} must add no more than
     * one to this field, or the iterators (and list iterators) will throw
     * bogus {@code ConcurrentModificationExceptions}.  If an implementation
     * does not wish to provide fail-fast iterators, this field may be
     * ignored.
     * 子类使用此字段是可选的。如果子类希望提供快速失效的iterators（和list iterators），
     * 那么它只需在其add(int, E)和remove(int)方法
     * （以及它重写的任何其他方法，这些方法会导致对列表的结构修改）中让该字段增加。
     * 对add(int, E)或 remove(int)的单次调用只能让该字段加1，
     * 否则iterators（和list iterators）将抛出假的ConcurrentModificationExceptions。
     */
    protected transient int modCount = 0;
}
```
总结：
1. 内部维护了一个数组来存储元素，数组大小可以自动调整，所以内部都是数组操作。
2. 允许存入null。
3. 非同步的。synchronized修饰方法。
4. 迭代器是快速失败的，采用的和map一样的modCount计数策略。

##二： add、set逻辑
```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final int DEFAULT_CAPACITY = 10;
    transient Object[] elementData;
    
    /**
     * Appends the specified element to the end of this list.
     * 将指定元素添加到list的末尾。
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        //elementData素组容量增加、modCount增加
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            //首次扩容，设置为DEFAULT_CAPACITY（10）
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        //minCapacity为实际元素个数size再加1，elementData这个数组的大小不一定和size一样
        //这里不一定会触发grow的。
        //第一次add前 size=0 elementDate.length=0 add后 size=1 elementDate.length=10
        //第二次add前 size=1 elementDate.length=10 不扩容
        //......
        //第11次add前 size=10 elementDate.length=10 add后 size=11 elementDate.length=15
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     * 增加容量，以确保它至少可以容纳最小容量参数指定的元素数。
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        //这里主要是看下新的容量是老的容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     * 将指定的元素插入到该list的指定位置。
     * 将当前位于该位置的元素（如果有）和任何后续元素向右移动（将一个元素添加到其索引中）
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        //检查index是否合法
        rangeCheckForAdd(index);

        //elementData素组容量增加、modCount增加
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        
        //将index及其后面的元素全部右移一位
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        //index位置放新的element
        elementData[index] = element;
        size++;
    }

    /**
     * A version of rangeCheck used by add and addAll.
     * add 和 addAll 用来检查区间的版本
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     * 将该list的指定位置的元素替换为指定元素
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        //检查index
        rangeCheck(index);

        //获取该位置的原始元素，用于返回
        E oldValue = elementData(index);
        //替换index位置的元素
        elementData[index] = element;
        return oldValue;
    }
}
```
总结：
1. 数组容量首次扩容到DEFAULT_CAPACITY（10），后续size>=数组容量后，扩容到当前容量的1.5倍。
2. 扩容、按索引添加，都会存在着数组拷贝。

##三： get、remove
```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{

    /**
     * Returns the element at the specified position in this list.
     * 返回该list中指定位置的元素。
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     * 检查给定的index是否在区间中。如果不在则抛出适当的运行时异常。
     * 该方法不检查负数：它总是在数组访问之前使用，如果索引为负，则会抛出ArrayIndexOutOfBoundsException。
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    E elementData(int index) {
        return (E) elementData[index];
    }
    
    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     * 删除该list中指定位置的元素。将任何后续元素向左移动（从其索引中减去一个）。
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        //检查index是否合法
        rangeCheck(index);

        //增加结构性修改次数
        modCount++;

        //获取到index的元素用于返回
        E oldValue = elementData(index);

        //numMoved表示需要移动的元素个数，如果说remove的是最后一个，则不需要移动
        int numMoved = size - index - 1;
        if (numMoved > 0)
            //index后面的元素全部往前移动一位
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        //最后一位置空
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }

}
```
总结：
1. get的效率很高，直接数组索引访问。
2. remove就又涉及到数组拷贝的动作了。