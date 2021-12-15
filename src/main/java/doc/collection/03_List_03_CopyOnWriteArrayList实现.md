#CopyOnWriteArrayList源码分析

##一： 类注释及内部变量预览
```java
/**
 * A thread-safe variant of {@link java.util.ArrayList} in which all mutative
 * operations ({@code add}, {@code set}, and so on) are implemented by
 * making a fresh copy of the underlying array.
 * ArrayList的一种线程安全变体，
 * 其中所有可变操作（add,set等）都是通过创建基础数组的新副本来实现的。
 *
 * <p>This is ordinarily too costly, but may be <em>more</em> efficient
 * than alternatives when traversal operations vastly outnumber
 * mutations, and is useful when you cannot or don't want to
 * synchronize traversals, yet need to preclude interference among
 * concurrent threads.  The "snapshot" style iterator method uses a
 * reference to the state of the array at the point that the iterator
 * was created. This array never changes during the lifetime of the
 * iterator, so interference is impossible and the iterator is
 * guaranteed not to throw {@code ConcurrentModificationException}.
 * The iterator will not reflect additions, removals, or changes to
 * the list since the iterator was created.  Element-changing
 * operations on iterators themselves ({@code remove}, {@code set}, and
 * {@code add}) are not supported. These methods throw
 * {@code UnsupportedOperationException}.
 * 这通常成本太高，但在遍历操作的数量远远超过变更操作时（查询数量大于变更数量），
 * 可能比其他方法更有效，并且在您不能或不想同步遍历，但需要排除并发线程之间的干扰时非常有用。
 * “快照”样式的迭代器方法使用对创建迭代器时数组状态的引用。
 * 该数组在迭代器的生命周期内不会更改，因此不可能发生干扰，
 * 并且迭代器保证不会抛出ConcurrentModificationException。
 * 自创建迭代器以来，迭代器不会反映对列表的添加、删除或更改。
 * 不支持迭代器本身的元素更改操作（remove、set和add）。
 * 这些方法引发UnsupportedOperationException。
 *
 * <p>All elements are permitted, including {@code null}.
 * 所有元素都允许，包括null。
 *
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code CopyOnWriteArrayList}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code CopyOnWriteArrayList} in another thread.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    // 该array变量只能通过getArray/setArray方法访问
    private transient volatile Object[] array;

}
```
总结：
1. 也是基于数组的实现，其结构性修改的操作每次是新拷贝出了一份数组来操作，然后替换老的数组。
   1.1 导致变更操作成本很高，适用于读多改少的情况。
   1.2 迭代器中的数组就是一个快照了，不会受原始list结构性修改操作的影响。
2. 线程安全的，内部使用了ReentrantLock。
3. 允许元素为null。
4. 迭代器不支持remove、set、add，不会抛出ConcurrentModificationException。

##二： add、set逻辑
```java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    /**
     * Appends the specified element to the end of this list.
     * 将指定元素添加到该list尾部。
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        //先lock，所以是线程安全的
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            //将原来的数组array拷贝了一份，并且容量+1
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            //将新数组重新set到array变量
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     * 将指定的元素插入到该list的指定位置。
     * 将当前位于该位置的元素（如果有）和任何后续元素向右移动（将一个元素添加到其索引中）
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        final ReentrantLock lock = this.lock;
        //先lock
        lock.lock();
        try {
            //获取老的数组array
            Object[] elements = getArray();
            int len = elements.length;
            //检查index
            if (index > len || index < 0)
                throw new IndexOutOfBoundsException("Index: "+index+
                        ", Size: "+len);
            
            //拷贝出新数组
            Object[] newElements;
            int numMoved = len - index;
            if (numMoved == 0)
                newElements = Arrays.copyOf(elements, len + 1);
            else {
                newElements = new Object[len + 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index, newElements, index + 1,
                        numMoved);
            }
            
            //新元素放入到新数组的index位置
            newElements[index] = element;
            //新数组设置回去
            setArray(newElements);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        final ReentrantLock lock = this.lock;
        //先上锁
        lock.lock();
        try {
            //获取老的数组和index位置老的元素
            Object[] elements = getArray();
            E oldValue = get(elements, index);

            if (oldValue != element) {
                int len = elements.length;
                //拷贝数组
                Object[] newElements = Arrays.copyOf(elements, len);
                //替换元素
                newElements[index] = element;
                //设置新的数组
                setArray(newElements);
            } else {
                // 新元素和老元素一样，不拷贝替换了，但是还是调用一次setArray方法。
                // Not quite a no-op; ensures volatile write semantics
                // 不是完全没有行动，确保volatile write语义
                setArray(elements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }
}
```
总结：  
1. 这些结构性修改操作都是会上锁的，所以线程安全。
2. 都是先拷贝出一个新数组，然后再新数组上变更，再将新数组set进去。

##三： iterator逻辑（不会抛出ConcurrentModificationException）
```java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    public Iterator<E> iterator() {
        //创建COWIterator，其实不是说在此处拷贝了一份数组，只是说其它结构性修改方法每次都会复制，
        //他们复制了一份新的，这一份就是老的快照了。
        return new COWIterator<E>(getArray(), 0);
    }
    
    //这个Iterator是没有modCount的，不会抛出ConcurrentModificationException。
    // 因为它内部是数组的快照，不存在于原始List变更影响到快照
    static final class COWIterator<E> implements ListIterator<E> {
        /** Snapshot of the array */
        private final Object[] snapshot;
        /** Index of element to be returned by subsequent call to next.  */
        private int cursor;

        private COWIterator(Object[] elements, int initialCursor) {
            cursor = initialCursor;
            snapshot = elements;
        }

        public boolean hasNext() {
            return cursor < snapshot.length;
        }

        //不会抛出ConcurrentModificationException
        public E next() {
            if (! hasNext())
                throw new NoSuchElementException();
            return (E) snapshot[cursor++];
        }

        public int nextIndex() {
            return cursor;
        }

        //remove、set、add 都是不支持的
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }
    
}
```
总结： 
1. 这个Iterator是没有modCount的，不会抛出ConcurrentModificationException
2. remove、set、add方法不支持