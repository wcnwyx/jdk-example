```java
/**
 * An ordered collection (also known as a <i>sequence</i>).  The user of this
 * interface has precise control over where in the list each element is
 * inserted.  The user can access elements by their integer index (position in
 * the list), and search for elements in the list.<p>
 * 有序集合（也称为序列）。用户使用此接口可以精确控制每个元素在列表中的插入位置。
 * 用户可以通过其整数索引（列表中的位置）访问元素，并在列表中搜索元素。
 *
 * Unlike sets, lists typically allow duplicate elements.  More formally,
 * lists typically allow pairs of elements <tt>e1</tt> and <tt>e2</tt>
 * such that <tt>e1.equals(e2)</tt>, and they typically allow multiple
 * null elements if they allow null elements at all.  It is not inconceivable
 * that someone might wish to implement a list that prohibits duplicates, by
 * throwing runtime exceptions when the user attempts to insert them, but we
 * expect this usage to be rare.<p>
 * 与集合不同，List通常允许重复元素。更正式地说，列表通常允许成对的元素e1和e2(e1.equals(e2))，
 * 如果它们允许空元素，则通常允许多个空元素。
 * 有人可能希望通过在用户尝试插入时抛出运行时异常来实现禁止重复的列表，
 * 这并不是不可想象的，但我们希望这种用法很少出现。
 *
 * The <tt>List</tt> interface places additional stipulations, beyond those
 * specified in the <tt>Collection</tt> interface, on the contracts of the
 * <tt>iterator</tt>, <tt>add</tt>, <tt>remove</tt>, <tt>equals</tt>, and
 * <tt>hashCode</tt> methods.  Declarations for other inherited methods are
 * also included here for convenience.<p>
 * List接口在iterator、add、remove、equals和hashCode方法的约定上
 * 放置了除Collection接口中指定的之外的附加规定。
 * 为了方便起见，这里还包括其他继承方法的声明。
 *
 * The <tt>List</tt> interface provides four methods for positional (indexed)
 * access to list elements.  Lists (like Java arrays) are zero based.  Note
 * that these operations may execute in time proportional to the index value
 * for some implementations (the <tt>LinkedList</tt> class, for
 * example). Thus, iterating over the elements in a list is typically
 * preferable to indexing through it if the caller does not know the
 * implementation.<p>
 * List接口提供了四种对列表元素进行位置（索引）访问的方法。列表（如Java数组）是基于零的。
 * 请注意，对于某些实现（例如LinkedList类），这些操作的执行时间可能与索引值成比例。
 * 因此，如果调用方不知道实现，那么在列表中的元素上进行迭代通常比通过列表进行索引更可取。
 *
 * The <tt>List</tt> interface provides a special iterator, called a
 * <tt>ListIterator</tt>, that allows element insertion and replacement, and
 * bidirectional access in addition to the normal operations that the
 * <tt>Iterator</tt> interface provides.  A method is provided to obtain a
 * list iterator that starts at a specified position in the list.<p>
 * List接口提供了一个称为ListIterator的特殊迭代器，
 * 除了迭代器接口提供的正常操作之外，它还允许元素插入和替换以及双向访问。
 * 提供了一种方法来获取从列表中指定位置开始的列表迭代器。
 *
 * The <tt>List</tt> interface provides two methods to search for a specified
 * object.  From a performance standpoint, these methods should be used with
 * caution.  In many implementations they will perform costly linear
 * searches.<p>
 * List接口提供了两种方法来搜索指定的对象。从性能角度来看，应谨慎使用这些方法。
 * 在许多实现中，它们将执行代价高昂的线性搜索。
 *
 * The <tt>List</tt> interface provides two methods to efficiently insert and
 * remove multiple elements at an arbitrary point in the list.<p>
 * List接口提供了两种方法来有效地在列表中的任意点插入和删除多个元素。
 *
 * Note: While it is permissible for lists to contain themselves as elements,
 * extreme caution is advised: the <tt>equals</tt> and <tt>hashCode</tt>
 * methods are no longer well defined on such a list.
 * 注意：虽然允许列表将自身包含为元素，但还是要特别小心：
 * equals和hashCode方法在这样的列表中不再有很好的定义。
 *
 * <p>Some list implementations have restrictions on the elements that
 * they may contain.  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the list may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 * 一些列表实现对它们可能包含的元素有限制。
 * 例如，有些实现禁止空元素，有些实现对其元素的类型有限制。
 * 尝试添加不合格的元素会引发未经检查的异常，通常为NullPointerException或ClassCastException。
 * 试图查询不合格的元素是否存在可能会引发异常，或者只返回false；
 * 有些实现将展示前一种行为，有些实现将展示后一种行为。
 * 更一般地说，在不合格元素上尝试操作，如果该操作的完成不会导致将不合格元素插入列表中，
 * 则可能引发异常，或者可能成功，具体取决于实现。此类异常在该接口规范中标记为“可选”。
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements in this list
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Collection
 * @see Set
 * @see ArrayList
 * @see LinkedList
 * @see Vector
 * @see Arrays#asList(Object[])
 * @see Collections#nCopies(int, Object)
 * @see Collections#EMPTY_LIST
 * @see AbstractList
 * @see AbstractSequentialList
 * @since 1.2
 */

public interface List<E> extends Collection<E> {
    // Query Operations
    // 查询操作

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 返回此list中的元素数。如果此列表包含的元素数大于Integer.MAX_VALUE，则返回Integer.MAX_VALUE。
     *
     * @return the number of elements in this list
     */
    int size();

    /**
     * Returns <tt>true</tt> if this list contains no elements.
	 * 如果此list中不包含任何元素，则返回true。
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 如果该list中包含指定的元素则返回true。
	 * 更正式地说，当且仅当此列表包含至少一个元素e时(o==null ? e==null : o.equals(e))返回true。
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this list
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * Returns an iterator over the elements in this list in proper sequence.
	 * 按正确的顺序返回此列表中元素的迭代器。
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    Iterator<E> iterator();

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
	 * 返回一个数组，该数组按正确顺序（从第一个元素到最后一个元素）包含此列表中的所有元素。
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
	 * 返回的数组将是“安全的”，因为此列表不维护对它的引用。
	 * （换句话说，即使此列表由数组支持，此方法也必须分配新数组）。
	 * 因此，调用者可以自由修改返回的数组。
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
	 * 此方法充当基于素组和基于集合的API之间的桥梁。
     *
     * @return an array containing all of the elements in this list in proper
     *         sequence
     * @see Arrays#asList(Object[])
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
	 * 返回一个数组，该数组按正确顺序（从第一个元素到最后一个元素）包含此列表中的所有元素。
	 * 返回数组的运行时类型是指定数组的类型。如果列表适合指定的数组，则返回其中。
	 * 否则，将使用指定数组的运行时类型和此列表的大小分配一个新数组。
     *
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to <tt>null</tt>.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
	 * 如果列表适合指定的数组，且有空闲空间（即数组中的元素多于列表），
	 * 则紧跟在列表末尾的数组中的元素将设置为null。
	 * （只有在调用者知道列表不包含任何空元素时，这才有助于确定列表的长度。）
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
	 * 像toArray()方法一样，此方法充当基于素组和基于集合的API之间的桥梁。
	 * 此外，此方法允许对输出数组的运行时类型进行精确控制，
	 * 并且在某些情况下可用于节省分配成本。
     *
     * <p>Suppose <tt>x</tt> is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of <tt>String</tt>:
	 * 假设x是已知仅包含字符串的列表。以下代码可用于将列表转储到新分配的字符串数组中：
     *
     * <pre>{@code
     *     String[] y = x.toArray(new String[0]);
     * }</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
	 * 注意：toArray(new Object[0])在功能上和toArray()相同。
     *
     * @param a the array into which the elements of this list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    <T> T[] toArray(T[] a);


    // Modification Operations
	// 修改操作

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
	 * 将指定的元素追加到此列表的末尾（可选操作）。
     *
     * <p>Lists that support this operation may place limitations on what
     * elements may be added to this list.  In particular, some
     * lists will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added.  List
     * classes should clearly specify in their documentation any restrictions
     * on what elements may be added.
	 * 支持此操作的列表可能会限制哪些元素可以添加到此列表中。
	 * 特别是，一些列表将拒绝添加空元素，而其他列表将对可能添加的元素类型施加限制。
	 * 列表类应在其文档中明确指定对可能添加的元素的任何限制。
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this list
     */
    boolean add(E e);

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present (optional operation).  If this list does not contain
     * the element, it is unchanged.  More formally, removes the element with
     * the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list changed
     * as a result of the call).
	 * 从该列表中删除指定元素的第一个匹配项（如果存在）（可选操作）。
	 * 如果此列表不包含该元素，则它将保持不变。
	 * 更正式地说，移除具有最低索引i的元素，
	 * 以便 (o==null ? get(i)==null : o.equals(get(i)))（如果存在这样的元素）。
	 * 如果此列表包含指定的元素，则返回true（如果此列表因调用而更改，则返回等效值）。
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this list
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this list
     */
    boolean remove(Object o);


    // Bulk Modification Operations
	// 批量修改操作

    /**
     * Returns <tt>true</tt> if this list contains all of the elements of the
     * specified collection.
	 * 如果此列表包含指定集合的所有元素，则返回true。
     *
     * @param  c collection to be checked for containment in this list
     * @return <tt>true</tt> if this list contains all of the elements of the
     *         specified collection
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         list
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this list does not permit null
     *         elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator (optional operation).  The behavior of this
     * operation is undefined if the specified collection is modified while
     * the operation is in progress.  (Note that this will occur if the
     * specified collection is this list, and it's nonempty.)
	 * 将指定集合中的所有元素按照其迭代器返回的顺序追加到此列表的末尾（可选操作）。
	 * 如果在操作进行过程中修改了指定的集合，则此操作的行为未定义。
	 * （请注意，如果指定的集合是此列表，并且它是非空的，则会发生这种情况。）
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this list
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this list does not permit null
     *         elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this list
     * @see #add(Object)
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Inserts all of the elements in the specified collection into this
     * list at the specified position (optional operation).  Shifts the
     * element currently at that position (if any) and any subsequent
     * elements to the right (increases their indices).  The new elements
     * will appear in this list in the order that they are returned by the
     * specified collection's iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the
     * operation is in progress.  (Note that this will occur if the specified
     * collection is this list, and it's nonempty.)
	 * 在指定位置将指定集合中的所有元素插入此列表（可选操作）。
	 * 将当前位于该位置的元素（如果有）和任何后续元素向右移动（增加其索引）。
	 * 新元素将按指定集合的迭代器返回的顺序显示在此列表中。
	 * 如果在操作进行过程中修改了指定的集合，则此操作的行为未定义。
	 * （请注意，如果指定的集合是此列表，并且它是非空的，则会发生这种情况。）
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this list
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this list does not permit null
     *         elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    boolean addAll(int index, Collection<? extends E> c);

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection (optional operation).
	 * 从此列表中删除指定集合中包含的所有元素（可选操作）。
     *
     * @param c collection containing elements to be removed from this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean removeAll(Collection<?> c);

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this list all of its elements that are not contained in the
     * specified collection.
	 * 仅保留此列表中包含在指定集合中的元素（可选操作）。
	 * 换句话说，从该列表中删除指定集合中不包含的所有元素。
     *
     * @param c collection containing elements to be retained in this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean retainAll(Collection<?> c);

    /**
     * Replaces each element of this list with the result of applying the
     * operator to that element.  Errors or runtime exceptions thrown by
     * the operator are relayed to the caller.
	 * 使用对该元素应用运算符的结果替换此列表中的每个元素。
	 * 操作员引发的错误或运行时异常将转发给调用者。
     *
     * @implSpec
     * The default implementation is equivalent to, for this {@code list}:
	 * 对于此列表，默认实现相当于：
	 * 
     * <pre>{@code
     *     final ListIterator<E> li = list.listIterator();
     *     while (li.hasNext()) {
     *         li.set(operator.apply(li.next()));
     *     }
     * }</pre>
     *
     * If the list's list-iterator does not support the {@code set} operation
     * then an {@code UnsupportedOperationException} will be thrown when
     * replacing the first element.
	 * 如果列表的list-iterator不支持set操作，则在替换第一个元素时将引发UnsupportedOperationException。
     *
     * @param operator the operator to apply to each element
     * @throws UnsupportedOperationException if this list is unmodifiable.
     *         Implementations may throw this exception if an element
     *         cannot be replaced or if, in general, modification is not
     *         supported
     * @throws NullPointerException if the specified operator is null or
     *         if the operator result is a null value and this list does
     *         not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     * @since 1.8
     */
    default void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final ListIterator<E> li = this.listIterator();
        while (li.hasNext()) {
            li.set(operator.apply(li.next()));
        }
    }

    /**
     * Sorts this list according to the order induced by the specified
     * {@link Comparator}.
	 * 根据指定比较器产生的顺序对此列表进行排序。
     *
     * <p>All elements in this list must be <i>mutually comparable</i> using the
     * specified comparator (that is, {@code c.compare(e1, e2)} must not throw
     * a {@code ClassCastException} for any elements {@code e1} and {@code e2}
     * in the list).
	 * 此列表中的所有元素必须使用指定的比较器相互比较（即，c.compare(e1, e2)
	 * 不得为列表中的任何元素e1和e2抛出ClassCastException）。
     *
     * <p>If the specified comparator is {@code null} then all elements in this
     * list must implement the {@link Comparable} interface and the elements'
     * {@linkplain Comparable natural ordering} should be used.
	 * 如果指定的比较器为空，则此列表中的所有元素都必须实现Comparable接口，
	 * 并且应使用元素的自然顺序。
     *
     * <p>This list must be modifiable, but need not be resizable.
	 * 此列表必须可修改，但无需调整大小。
     *
     * @implSpec
     * The default implementation obtains an array containing all elements in
     * this list, sorts the array, and iterates over this list resetting each
     * element from the corresponding position in the array. (This avoids the
     * n<sup>2</sup> log(n) performance that would result from attempting
     * to sort a linked list in place.)
	 * 默认实现获取一个包含此列表中所有元素的数组，对数组进行排序，
	 * 并在此列表上迭代，从数组中的相应位置重置每个元素。
	 * （这避免了因尝试对链接列表进行适当排序而导致的n2次方 log(n)性能。）
     *
     * @implNote
     * This implementation is a stable, adaptive, iterative mergesort that
     * requires far fewer than n lg(n) comparisons when the input array is
     * partially sorted, while offering the performance of a traditional
     * mergesort when the input array is randomly ordered.  If the input array
     * is nearly sorted, the implementation requires approximately n
     * comparisons.  Temporary storage requirements vary from a small constant
     * for nearly sorted input arrays to n/2 object references for randomly
     * ordered input arrays.
     *
     * <p>The implementation takes equal advantage of ascending and
     * descending order in its input array, and can take advantage of
     * ascending and descending order in different parts of the same
     * input array.  It is well-suited to merging two or more sorted arrays:
     * simply concatenate the arrays and sort the resulting array.
     *
     * <p>The implementation was adapted from Tim Peters's list sort for Python
     * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
     * TimSort</a>).  It uses techniques from Peter McIlroy's "Optimistic
     * Sorting and Information Theoretic Complexity", in Proceedings of the
     * Fourth Annual ACM-SIAM Symposium on Discrete Algorithms, pp 467-474,
     * January 1993.
     *
     * @param c the {@code Comparator} used to compare list elements.
     *          A {@code null} value indicates that the elements'
     *          {@linkplain Comparable natural ordering} should be used
     * @throws ClassCastException if the list contains elements that are not
     *         <i>mutually comparable</i> using the specified comparator
     * @throws UnsupportedOperationException if the list's list-iterator does
     *         not support the {@code set} operation
     * @throws IllegalArgumentException
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     *         if the comparator is found to violate the {@link Comparator}
     *         contract
     * @since 1.8
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }

    /**
     * Removes all of the elements from this list (optional operation).
     * The list will be empty after this call returns.
	 * 从此列表中删除所有元素（可选操作）。此调用返回后，列表将为空。
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *         is not supported by this list
     */
    void clear();


    // Comparison and hashing
	// 比较和散列

    /**
     * Compares the specified object with this list for equality.  Returns
     * <tt>true</tt> if and only if the specified object is also a list, both
     * lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.  (Two elements <tt>e1</tt> and
     * <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
     * e1.equals(e2))</tt>.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.  This
     * definition ensures that the equals method works properly across
     * different implementations of the <tt>List</tt> interface.
	 * 比较指定对象与此列表是否相等。当且仅当指定对象也是列表，
	 * 两个列表的大小相同且两个列表中所有对应的元素对相等时，返回true。
	 * （如果(e1==null ? e2==null : e1.equals(e2))，则两个元素e1和e2相等。）
	 * 换句话说，如果两个列表包含相同顺序的相同元素，则它们被定义为相等。
	 * 此定义确保equals方法在列表接口的不同实现中正常工作。
     *
     * @param o the object to be compared for equality with this list
     * @return <tt>true</tt> if the specified object is equal to this list
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this list.  The hash code of a list
     * is defined to be the result of the following calculation:
	 * 返回此列表的哈希值。列表的哈希值定义为以下计算的结果：
     * <pre>{@code
     *     int hashCode = 1;
     *     for (E e : list)
     *         hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
     * }</pre>
     * This ensures that <tt>list1.equals(list2)</tt> implies that
     * <tt>list1.hashCode()==list2.hashCode()</tt> for any two lists,
     * <tt>list1</tt> and <tt>list2</tt>, as required by the general
     * contract of {@link Object#hashCode}.
     *
     * @return the hash code value for this list
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();


    // Positional Access Operations
	// 位置访问操作

    /**
     * Returns the element at the specified position in this list.
	 * 返回此列表中指定位置的元素。
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    E get(int index);

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).
	 * 用指定的元素替换此列表中指定位置的元素（可选操作）。
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>set</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and
     *         this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    E set(int index, E element);

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
	 * 在此列表中的指定位置插入指定元素（可选操作）。
	 * 将当前位于该位置的元素（如果有）和任何后续元素向右移动（将一个元素添加到其索引中）。
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and
     *         this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    void add(int index, E element);

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
	 * 删除此列表中指定位置的元素（可选操作）。
	 * 将任何后续元素向左移动（从其索引中减去一个）。返回从列表中删除的元素。
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    E remove(int index);


    // Search Operations
	// 搜索操作

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
	 * 返回此列表中指定元素第一次出现的索引，如果此列表不包含该元素，则返回-1。
	 * 更正式地说，返回最低的索引i (o==null ? get(i)==null : o.equals(get(i)))，
	 * 或者如果没有这样的索引，返回-1。
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this list
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    int indexOf(Object o);

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
	 * 返回此列表中指定元素最后一次出现的索引，如果此列表不包含该元素，则返回-1。
	 * 更正式地说，返回最高的索引i (o==null ? get(i)==null : o.equals(get(i)))，
	 * 或者如果没有这样的索引，则返回-1。
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this list
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    int lastIndexOf(Object o);


    // List Iterators

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
	 * 返回此列表中元素的ListIterator（按正确顺序）。
     *
     * @return a list iterator over the elements in this list (in proper
     *         sequence)
     */
    ListIterator<E> listIterator();

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
	 * 返回此列表中元素的列表迭代器（按正确顺序），从列表中的指定位置开始。
	 * 初始调用ListIterator.next返回的第一个元素就是指定的索引指示的元素。
	 * 对ListIterator.previous的初始调用将返回指定索引减1的元素。
     *
     * @param index index of the first element to be returned from the
     *        list iterator (by a call to {@link ListIterator#next next})
     * @return a list iterator over the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     */
    ListIterator<E> listIterator(int index);

    // View

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.<p>
	 * 返回此列表中指定的fromIndex（包含）和toIndex（不包含）之间部分的视图。
	 * （如果fromIndex和toIndex相等，则返回的列表为空。）
	 * 返回的列表由该列表支持，因此返回列表中的非结构性更改将反映在此列表中，反之亦然。
	 * 返回的列表支持此列表支持的所有可选列表操作。
     *
     * This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
	 * 此方法消除了显式范围操作的需要（通常存在于数组中）。
	 * 通过传递子列表视图而不是整个列表，任何需要列表的操作都可以用作范围操作。
	 * 例如，以下习惯用法从列表中删除一系列元素：
     * <pre>{@code
     *      list.subList(from, to).clear();
     * }</pre>
     * Similar idioms may be constructed for <tt>indexOf</tt> and
     * <tt>lastIndexOf</tt>, and all of the algorithms in the
     * <tt>Collections</tt> class can be applied to a subList.<p>
	 * 可以为indexOf和lastIndexOf构造类似的习惯用法，
	 * 并且Collections类中的所有算法都可以应用于子列表。
     *
     * The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
	 * 如果支持列表（即，此列表）以任何方式（而不是通过返回的列表）进行结构修改，
	 * 则此方法返回的列表的语义将变得未定义。（结构修改是指更改此列表的大小，
	 * 或以其他方式干扰列表，使正在进行的迭代可能产生不正确的结果。）
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *         (<tt>fromIndex &lt; 0 || toIndex &gt; size ||
     *         fromIndex &gt; toIndex</tt>)
     */
    List<E> subList(int fromIndex, int toIndex);

    /**
     * Creates a {@link Spliterator} over the elements in this list.
	 * 在此列表中的元素上创建拆分器。
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}.  Implementations should document the
     * reporting of additional characteristic values.
     *
     * @implSpec
     * The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the list's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the list's iterator.
     *
     * @implNote
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }
```