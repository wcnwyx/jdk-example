##一 Vector向量（不推荐使用）
```java
/**
 * The {@code Vector} class implements a growable array of
 * objects. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * {@code Vector} can grow or shrink as needed to accommodate
 * adding and removing items after the {@code Vector} has been created.
 * Vector类实现了一个可扩展的对象数组。与数组一样，它包含可以使用整数索引访问的组件。
 * 但是，Vector的大小可以根据需要增大或缩小，以适应在创建Vector后添加和删除项目。
 *
 * <p>Each vector tries to optimize storage management by maintaining a
 * {@code capacity} and a {@code capacityIncrement}. The
 * {@code capacity} is always at least as large as the vector
 * size; it is usually larger because as components are added to the
 * vector, the vector's storage increases in chunks the size of
 * {@code capacityIncrement}. An application can increase the
 * capacity of a vector before inserting a large number of
 * components; this reduces the amount of incremental reallocation.
 * 每个向量都试图通过保持capacity和capacityIncrement来优化存储管理。
 * capacity一般至少和vector的size一样大；
 * 它通常更大，因为当组件添加到vector时，vector的存储量会成片增加，即capacityIncrement的大小。
 * 应用程序可以在插入大量组件之前增加vector的容量；这减少了增量重新分配的数量。
 *
 * Iterator快速失败的描述。
 *
 * <p>As of the Java 2 platform v1.2, this class was retrofitted to
 * implement the {@link List} interface, making it a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.  Unlike the new collection
 * implementations, {@code Vector} is synchronized.  If a thread-safe
 * implementation is not needed, it is recommended to use {@link
 * ArrayList} in place of {@code Vector}.
 * 从Java2平台v1.2开始，对该类进行了改造以实现List接口，使其成为Java集合框架的成员。
 * 与新的集合实现不同，Vector是同步的。如果不需要线程安全实现，建议使用ArrayList代替Vector。
 *
 * @author  Lee Boynton
 * @author  Jonathan Payne
 * @see Collection
 * @see LinkedList
 * @since   JDK1.0
 */
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    /**
     * The array buffer into which the components of the vector are
     * stored. The capacity of the vector is the length of this array buffer,
     * and is at least large enough to contain all the vector's elements.
     * 存储vector组件的数组缓冲区。vector的容量是该数组缓冲区的长度，
     * 并且至少足够大以包含向量的所有元素。
     *
     * <p>Any array elements following the last element in the Vector are null.
     * 向量中最后一个元素后面的任何数组元素都为null。
     * @serial
     */
    protected Object[] elementData;

    /**
     * The number of valid components in this {@code Vector} object.
     * Components {@code elementData[0]} through
     * {@code elementData[elementCount-1]} are the actual items.
     * 此{@code Vector}对象中的有效组件数。
     * 组件elementData[0]到elementData[elementCount-1]是实际的项。
     *
     * @serial
     */
    protected int elementCount;

    /**
     * The amount by which the capacity of the vector is automatically
     * incremented when its size becomes greater than its capacity.  If
     * the capacity increment is less than or equal to zero, the capacity
     * of the vector is doubled each time it needs to grow.
     * 向量的容量在其大小大于其容量时自动增加的量。
     * 如果容量增量小于或等于零，则向量的容量将在每次需要增长时加倍。
     * @serial
     */
    protected int capacityIncrement;

    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param e element to be appended to this Vector
     * @return {@code true} (as specified by {@link Collection#add})
     * @since 1.2
     */
    public synchronized boolean add(E e) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = e;
        return true;
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        //如果没有指定capacityIncrement，则将容量增长一倍。
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                capacityIncrement : oldCapacity);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
}
```
总结： 
1. 和ArrayList一样，内部维护了一个数组来存储元素，数组大小可以自动调整，都是数组操作。
2. 每次新增的大小可以通过capacityIncrement设置，如不设置则成倍增长。
3. 允许存入null。
4. 同步的。
5. 迭代器是快速失败的，采用的和map一样的modCount计数策略。

##二： Stack堆栈（不推荐使用）
```java
/**
 * The <code>Stack</code> class represents a last-in-first-out
 * (LIFO) stack of objects. It extends class <tt>Vector</tt> with five
 * operations that allow a vector to be treated as a stack. The usual
 * <tt>push</tt> and <tt>pop</tt> operations are provided, as well as a
 * method to <tt>peek</tt> at the top item on the stack, a method to test
 * for whether the stack is <tt>empty</tt>, and a method to <tt>search</tt>
 * the stack for an item and discover how far it is from the top.
 * <p>
 * Stack类表示后进先出（LIFO）对象堆栈。它通过五个操作扩展了类Vector，允许将向量视为堆栈。
 * 提供了常用的推送push和弹出pop操作，以及一种查看堆栈顶部项目的方法peek、
 * 一种测试堆栈是否为空的方法empty，以及一种在堆栈中搜索search项目并发现其距离顶部有多远的方法。
 *     
 * When a stack is first created, it contains no items.
 * 首次创建堆栈时，它不包含任何项。
 *
 * <p>A more complete and consistent set of LIFO stack operations is
 * provided by the {@link Deque} interface and its implementations, which
 * should be used in preference to this class.  For example:
 * Deque接口及其实现提供了一组更完整、更一致的后进先出堆栈操作，应该优先于此类使用。例如：
 * <pre>   {@code
 *   Deque<Integer> stack = new ArrayDeque<Integer>();}</pre>
 * 
 * @author  Jonathan Payne
 * @since   JDK1.0
 */
public
class Stack<E> extends Vector<E> {
    /**
     * Creates an empty Stack.
     */
    public Stack() {
    }

    /**
     * Pushes an item onto the top of this stack. This has exactly
     * the same effect as:
     * 将项目推到此堆栈的顶部。这与以下效果完全相同：
     * <blockquote><pre>
     * addElement(item)</pre></blockquote>
     *
     * @param   item   the item to be pushed onto this stack.
     * @return  the <code>item</code> argument.
     * @see     java.util.Vector#addElement
     */
    public E push(E item) {
        //和list.add效果一样
        addElement(item);

        return item;
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     * 移除此堆栈顶部的对象，并将该对象作为此函数的值返回。
     *
     * @return  The object at the top of this stack (the last item
     *          of the <tt>Vector</tt> object).
     * @throws  EmptyStackException  if this stack is empty.
     */
    public synchronized E pop() {
        E       obj;
        int     len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     * 查看此堆栈顶部的对象，而不将其从堆栈中移除。
     *
     * @return  the object at the top of this stack (the last item
     *          of the <tt>Vector</tt> object).
     * @throws  EmptyStackException  if this stack is empty.
     */
    public synchronized E peek() {
        int     len = size();

        if (len == 0)
            throw new EmptyStackException();
        //和List.get(index)效果一样
        return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     * 测试该堆栈是否为空
     *
     * @return  <code>true</code> if and only if this stack contains
     *          no items; <code>false</code> otherwise.
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * Returns the 1-based position where an object is on this stack.
     * If the object <tt>o</tt> occurs as an item in this stack, this
     * method returns the distance from the top of the stack of the
     * occurrence nearest the top of the stack; the topmost item on the
     * stack is considered to be at distance <tt>1</tt>. The <tt>equals</tt>
     * method is used to compare <tt>o</tt> to the
     * items in this stack.
     * 返回对象在此堆栈上的基于1的位置。如果对象o作为该堆栈中的一个项出现，
     * 则此方法返回从最接近堆栈顶部的引用堆栈顶部的距离；堆栈上最顶端的项被认为位于距离1处。
     * equals方法用于将o与此堆栈中的项进行比较。
     *
     * @param   o   the desired object.
     * @return  the 1-based position from the top of the stack where
     *          the object is located; the return value <code>-1</code>
     *          indicates that the object is not on the stack.
     */
    public synchronized int search(Object o) {
        int i = lastIndexOf(o);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}

```
基于Vector提供了堆栈的相关方法（push、pop、peek等）