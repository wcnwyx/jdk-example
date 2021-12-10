/**
* A collection that contains no duplicate elements.  More formally, sets
* contain no pair of elements <code>e1</code> and <code>e2</code> such that
* <code>e1.equals(e2)</code>, and at most one null element.  As implied by
* its name, this interface models the mathematical <i>set</i> abstraction.
* 不包含重复元素的集合。更正式的说，集合不包含这样一对元素e1、e2(e1.equals(e2)),
* 并且最多一个null元素。正如它的名字所暗示的，这个接口为数学set抽象建模。
*
* <p>The <tt>Set</tt> interface places additional stipulations, beyond those
* inherited from the <tt>Collection</tt> interface, on the contracts of all
* constructors and on the contracts of the <tt>add</tt>, <tt>equals</tt> and
* <tt>hashCode</tt> methods.  Declarations for other inherited methods are
* also included here for convenience.  (The specifications accompanying these
* declarations have been tailored to the <tt>Set</tt> interface, but they do
* not contain any additional stipulations.)
* Set接口对所有构造函数的约定以及add、equals和hashCode方法的约定进行了额外的规定，
* 这些规定超出了从集合接口继承的规定。为了方便起见，这里还包括其他继承方法的声明。
* （这些声明随附的规范已针对Set接口进行了调整，但不包含任何附加规定。）
*
* <p>The additional stipulation on constructors is, not surprisingly,
* that all constructors must create a set that contains no duplicate elements
* (as defined above).
* 构造函数的附加规定是，所有构造函数都必须创建一个不包含重复元素的集合（如上所述）。
*
* <p>Note: Great care must be exercised if mutable objects are used as set
* elements.  The behavior of a set is not specified if the value of an object
* is changed in a manner that affects <tt>equals</tt> comparisons while the
* object is an element in the set.  A special case of this prohibition is
* that it is not permissible for a set to contain itself as an element.
* 注意：如果将可变对象用作集合元素，则必须非常小心。
* 当对象是集合中的一个元素时，如果对象的值以影响equals比较的方式更改，则不指定集合的行为。
* 这种禁止的一种特殊情况是，不允许集合本身包含为元素。
*
* <p>Some set implementations have restrictions on the elements that
* they may contain.  For example, some implementations prohibit null elements,
* and some have restrictions on the types of their elements.  Attempting to
* add an ineligible element throws an unchecked exception, typically
* <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
* to query the presence of an ineligible element may throw an exception,
* or it may simply return false; some implementations will exhibit the former
* behavior and some will exhibit the latter.  More generally, attempting an
* operation on an ineligible element whose completion would not result in
* the insertion of an ineligible element into the set may throw an
* exception or it may succeed, at the option of the implementation.
* Such exceptions are marked as "optional" in the specification for this
* interface.
* 一些set实现对它们可能包含的元素有限制。
* 例如，有些实现禁止空元素，有些实现对其元素的类型有限制。
* 尝试添加不合格的元素会引发未经检查的异常，通常为NullPointerException或ClassCastException。
* 试图查询不合格元素的存在可能会引发异常，或者只返回false；
* 有些实现将展示前一种行为，有些实现将展示后一种行为。
* 更一般地说，在一个不合格的元素上尝试一个操作，
* 如果该操作的完成不会导致将一个不合格的元素插入到集合中，
* 则可能会抛出一个异常，或者它可能会成功，这取决于实现的选择。
* 此类异常在该接口规范中标记为“可选”。
*
* <p>This interface is a member of the
* <a href="{@docRoot}/../technotes/guides/collections/index.html">
* Java Collections Framework</a>.
*
* @param <E> the type of elements maintained by this set
*
* @author  Josh Bloch
* @author  Neal Gafter
* @see Collection
* @see List
* @see SortedSet
* @see HashSet
* @see TreeSet
* @see AbstractSet
* @see Collections#singleton(java.lang.Object)
* @see Collections#EMPTY_SET
* @since 1.2
  */

public interface Set<E> extends Collection<E> {
// Query Operations
// 查询操作

    /**
     * Returns the number of elements in this set (its cardinality).  If this
     * set contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
	 * 返回此集合中的元素数（其基数）。
	 * 如果此集合包含的元素大于Integer.MAX_VALUE，则返回Integer.MAX_VALUE。
     *
     * @return the number of elements in this set (its cardinality)
     */
    int size();

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     * 如果该set不包含任何元素，则返回true。
	 * 
     * @return <tt>true</tt> if this set contains no elements
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this set
     * contains an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 如果该set包含指定的元素则返回true。
	 * 更正式地说，当且仅当该集合包含一个元素e时(o==null ? e==null : o.equals(e))返回true。
     *
     * @param o element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this set
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         set does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * Returns an iterator over the elements in this set.  The elements are
     * returned in no particular order (unless this set is an instance of some
     * class that provides a guarantee).
	 * 返回此Set中元素的迭代器。元素不按特定顺序返回（除非此Set是提供保证的某个类的实例）。
     *
     * @return an iterator over the elements in this set
     */
    Iterator<E> iterator();

    /**
     * Returns an array containing all of the elements in this set.
     * If this set makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the
     * elements in the same order.
	 * 返回包含此集合中所有元素的数组。如果这个集合保证迭代器返回元素的顺序，
	 * 那么这个方法必须以相同的顺序返回元素。
     *
     * <p>The returned array will be "safe" in that no references to it
     * are maintained by this set.  (In other words, this method must
     * allocate a new array even if this set is backed by an array).
     * The caller is thus free to modify the returned array.
	 * 返回的数组将是“安全的”，因为此set不维护对它的引用。
	 * (换句话说，此方法必须分配一个新数组，即使此集合由数组支持)。
	 * 因此，调用者可以自由修改返回的数组。
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
	 * 此方法充当基于数组和基于集合的API之间的桥梁。
     *
     * @return an array containing all the elements in this set
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this set; the
     * runtime type of the returned array is that of the specified array.
     * If the set fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this set.
	 * 返回包含此set中所有元素的数组。返回数组的运行时类型是指定数组的运行时类型。
	 * 如果set适合指定的数组，则返回其中。
	 * 否则，将使用指定数组的运行时类型和此set的大小分配一个新数组。
     *
     * <p>If this set fits in the specified array with room to spare
     * (i.e., the array has more elements than this set), the element in
     * the array immediately following the end of the set is set to
     * <tt>null</tt>.  (This is useful in determining the length of this
     * set <i>only</i> if the caller knows that this set does not contain
     * any null elements.)
	 * 如果此集合适合指定的数组，且有空闲空间（即数组的元素数大于此集合），
	 * 数组中紧跟在集合末尾的元素被设置为null。
	 * 只有在调用者知道该集合不包含任何空元素时，这才有助于确定该集合的长度。
     *
     * <p>If this set makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements
     * in the same order.
	 * 如果这个集合保证迭代器返回元素的顺序，那么这个方法必须以相同的顺序返回元素。
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
	 * 和方法toArray()一样，此方法充当基于数组和基于集合的API之间的桥梁。
	 * 此外，此方法允许对输出数组的运行时类型进行精确控制，并且在某些情况下可用于节省分配成本。
     *
     * <p>Suppose <tt>x</tt> is a set known to contain only strings.
     * The following code can be used to dump the set into a newly allocated
     * array of <tt>String</tt>:
	 * 假设x是已知的只包含字符串的集合。以下代码可用于将集合转储到新分配的字符串数组中：
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
	 * 请注意，toArray(new Object[0])在功能上与toArray()相同。
     *
     * @param a the array into which the elements of this set are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return an array containing all the elements in this set
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in this
     *         set
     * @throws NullPointerException if the specified array is null
     */
    <T> T[] toArray(T[] a);


    // Modification Operations
	// 更改操作

    /**
     * Adds the specified element to this set if it is not already present
     * (optional operation).  More formally, adds the specified element
     * <tt>e</tt> to this set if the set contains no element <tt>e2</tt>
     * such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>.  In combination with the
     * restriction on constructors, this ensures that sets never contain
     * duplicate elements.
	 * 如果指定的元素尚未存在，则将其添加到此集合（可选操作）。
	 * 更正式地说，如果集合中不包含元素e2，
	 * 则将指定的元素e添加到此集合中 (e==null ? e2==null : e.equals(e2))。
	 * 如果此集合已经包含该元素，则调用将保持集合不变并返回false。
	 * 结合对构造函数的限制，这可以确保集合中不包含重复的元素。
     *
     * <p>The stipulation above does not imply that sets must accept all
     * elements; sets may refuse to add any particular element, including
     * <tt>null</tt>, and throw an exception, as described in the
     * specification for {@link Collection#add Collection.add}.
     * Individual set implementations should clearly document any
     * restrictions on the elements that they may contain.
	 * 上述规定并不意味着集合必须接受所有元素；
	 * 集合可以拒绝添加任何特定元素（包括null），并抛出异常，如Collection.add规范中所述。
	 * 单个集合实现应该清楚地记录它们可能包含的元素的任何限制。
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     *         element
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by this set
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this set
     * @throws NullPointerException if the specified element is null and this
     *         set does not permit null elements
     * @throws IllegalArgumentException if some property of the specified element
     *         prevents it from being added to this set
     */
    boolean add(E e);


    /**
     * Removes the specified element from this set if it is present
     * (optional operation).  More formally, removes an element <tt>e</tt>
     * such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if
     * this set contains such an element.  Returns <tt>true</tt> if this set
     * contained the element (or equivalently, if this set changed as a
     * result of the call).  (This set will not contain the element once the
     * call returns.)
	 * 从该集合中删除指定的元素（如果存在）（可选操作）。
	 * 更正式地说，如果这个集合包含这样一个元素，
	 * 那么移除一个元素e，使得(o==null ? e==null : o.equals(e))。
	 * 如果此集合包含元素，则返回true（如果此集合因调用而更改，则返回等效值）。
	 * （一旦调用返回，此集合将不包含元素。）
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this set
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         set does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this set
     */
    boolean remove(Object o);


    // Bulk Operations
	// 批量操作

    /**
     * Returns <tt>true</tt> if this set contains all of the elements of the
     * specified collection.  If the specified collection is also a set, this
     * method returns <tt>true</tt> if it is a <i>subset</i> of this set.
	 * 如果此set包含指定collection的所有元素，则返回true。
	 * 如果指定的collection也是一个set，则如果该collection是该set的子集，则该方法返回true。
     *
     * @param  c collection to be checked for containment in this set
     * @return <tt>true</tt> if this set contains all of the elements of the
     *         specified collection
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         set
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this set does not permit null
     *         elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see    #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Adds all of the elements in the specified collection to this set if
     * they're not already present (optional operation).  If the specified
     * collection is also a set, the <tt>addAll</tt> operation effectively
     * modifies this set so that its value is the <i>union</i> of the two
     * sets.  The behavior of this operation is undefined if the specified
     * collection is modified while the operation is in progress.
	 * 如果指定collection中的所有元素尚未存在，则将它们添加到此set（可选操作）。
	 * 如果指定的collection也是一个set，addAll操作将有效地修改该set，使其值为两个集合的并集。
	 * 如果在操作进行过程中修改了指定的collection，则此操作的行为未定义。
     *
     * @param  c collection containing elements to be added to this set
     * @return <tt>true</tt> if this set changed as a result of the call
     *
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this set
     * @throws ClassCastException if the class of an element of the
     *         specified collection prevents it from being added to this set
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this set does not permit null
     *         elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this set
     * @see #add(Object)
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Retains only the elements in this set that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this set all of its elements that are not contained in the
     * specified collection.  If the specified collection is also a set, this
     * operation effectively modifies this set so that its value is the
     * <i>intersection</i> of the two sets.
	 * 仅保留此集中包含在指定集合中的元素（可选操作）。
	 * 换句话说，从该set中删除指定collection中不包含的所有元素。
	 * 如果指定的collection也是一个set，则此操作会有效地修改该set，使其值为两个集合的交点。
     *
     * @param  c collection containing elements to be retained in this set
     * @return <tt>true</tt> if this set changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *         is not supported by this set
     * @throws ClassCastException if the class of an element of this set
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this set contains a null element and the
     *         specified collection does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     */
    boolean retainAll(Collection<?> c);

    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection (optional operation).  If the specified
     * collection is also a set, this operation effectively modifies this
     * set so that its value is the <i>asymmetric set difference</i> of
     * the two sets.
     *
     * @param  c collection containing elements to be removed from this set
     * @return <tt>true</tt> if this set changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation
     *         is not supported by this set
     * @throws ClassCastException if the class of an element of this set
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this set contains a null element and the
     *         specified collection does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean removeAll(Collection<?> c);

    /**
     * Removes all of the elements from this set (optional operation).
     * The set will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> method
     *         is not supported by this set
     */
    void clear();


    // Comparison and hashing

    /**
     * Compares the specified object with this set for equality.  Returns
     * <tt>true</tt> if the specified object is also a set, the two sets
     * have the same size, and every member of the specified set is
     * contained in this set (or equivalently, every member of this set is
     * contained in the specified set).  This definition ensures that the
     * equals method works properly across different implementations of the
     * set interface.
     *
     * @param o object to be compared for equality with this set
     * @return <tt>true</tt> if the specified object is equal to this set
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this set.  The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set,
     * where the hash code of a <tt>null</tt> element is defined to be zero.
     * This ensures that <tt>s1.equals(s2)</tt> implies that
     * <tt>s1.hashCode()==s2.hashCode()</tt> for any two sets <tt>s1</tt>
     * and <tt>s2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this set
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    int hashCode();

    /**
     * Creates a {@code Spliterator} over the elements in this set.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#DISTINCT}.
     * Implementations should document the reporting of additional
     * characteristic values.
     *
     * @implSpec
     * The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the set's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the set's iterator.
     * <p>
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SIZED}.
     *
     * @implNote
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     *
     * @return a {@code Spliterator} over the elements in this set
     * @since 1.8
     */
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT);
    }
}
