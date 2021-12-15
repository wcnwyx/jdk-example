#LinkedList源码分析

##一： 类注释及内部变量预览
```java
/**
 * Doubly-linked list implementation of the {@code List} and {@code Deque}
 * interfaces.  Implements all optional list operations, and permits all
 * elements (including {@code null}).
 * List和Deque接口的双链接列表（双向链表）实现。实现所有可选的列表操作，并允许所有元素（包括null）。
 *
 * <p>All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the beginning or the end, whichever is closer to the specified index.
 * 所有操作的执行都与双链接列表的预期一样。
 * 索引到列表中的操作将从开始或结束遍历列表，以更接近指定索引的为准。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a linked list concurrently, and at least
 * one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more elements; merely setting the value of
 * an element is not a structural modification.)  This is typically
 * accomplished by synchronizing on some object that naturally
 * encapsulates the list.
 * 请注意，此实现是不同步的。
 * 如果多个线程同时访问一个链表，并且至少有一个线程在结构上修改链表，则必须在外部对其进行同步。
 * （结构修改是添加或删除一个或多个元素的任何操作；仅设置元素的值不是结构修改。）
 * 这通常通过在自然封装列表的某个对象上进行同步来实现。
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new LinkedList(...));</pre>
 * 如果不存在这样的对象，则应使用Collections.synchronizedList方法“包装”列表。
 * 最好在创建时执行此操作，以防止意外不同步地访问列表：
 *
 * <p>The iterators returned by this class's {@code iterator} and
 * {@code listIterator} methods are <i>fail-fast</i>: if the list is
 * structurally modified at any time after the iterator is created, in
 * any way except through the Iterator's own {@code remove} or
 * {@code add} methods, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than
 * risking arbitrary, non-deterministic behavior at an undetermined
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
 * exception for its correctness:   <i>the fail-fast behavior of iterators
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
 * @see     List
 * @see     ArrayList
 * @since 1.2
 * @param <E> the type of elements held in this collection
 */

public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    transient int size = 0;

    /**
     * Pointer to first node. 
     * 指向第一个节点
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * 指向最后一个节点。
     */
    transient Node<E> last;

    //节点类Node，有prev和next，双向链表的节点
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
```
总结：
1. LinkedList是List和Deque接口的实现，是基于双向链表的实现。
2. 非同步的。
3. 允许null。
4. iterator是快速失败的。

##二： 双向链表数据结构的一些操作
```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    /**
     * Links e as first element.
     * 从前面上链
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }
    
    /**
     * Links e as last element.
     * 从后面上链
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    /**
     * Unlinks non-null node x.
     * 将非空节点x下链
     */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }
}
```
都是一些标准的双向链表的操作。上链和下链都会导致size和modeCount修改。

##三： list接口的add、get、set、remove逻辑
```java
public class LinkedList<E>
        extends AbstractSequentialList<E>
        implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    /**
     * Appends the specified element to the end of this list.
     * 将指定元素添加到该列表尾部。
     *
     * <p>This method is equivalent to {@link #addLast}.
     * 该方法等同于addLast（Deque接口的方法，添加到尾部）
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * Returns the element at the specified position in this list.
     * 返回该list中指定位置的元素
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
    
    /**
     * Returns the (non-null) Node at the specified element index.
     * 返回指定元素索引的Node
     */
    Node<E> node(int index) {
        // assert isElementIndex(index);

        //因为是双向链表，可以从前往后循环，也可以从后往前循环。
        //如果索引位置在前一半则从前往后循环，否则从后往前循环
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * 使用指定的元素替换该list中指定位置的元素。
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        checkElementIndex(index);
        //找到index位置的节点
        Node<E> x = node(index);
        E oldVal = x.item;
        //替换值
        x.item = element;
        return oldVal;
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     * 删除该list中指定位置的元素。
     * 将任何后续元素向左移动（从其索引中减去一个）。返回从列表中删除的元素。
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        checkElementIndex(index);
        //找到Node，然后unlink下链
        return unlink(node(index));
    }
}
```
list接口的实现都是基于链表的处理。