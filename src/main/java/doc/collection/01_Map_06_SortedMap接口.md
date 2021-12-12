#SortedMap接口定义  

```java
/**
 * A {@link Map} that further provides a <em>total ordering</em> on its keys.
 * The map is ordered according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} typically
 * provided at sorted map creation time.  This order is reflected when
 * iterating over the sorted map's collection views (returned by the
 * {@code entrySet}, {@code keySet} and {@code values} methods).
 * Several additional operations are provided to take advantage of the
 * ordering.  (This interface is the map analogue of {@link SortedSet}.)
 * 一种Map，它进一步在其键上提供总排序。
 * 映射根据其键的Comparable排序(key实现了Comparable接口)，
 * 或者由通常在SortedMap创建时提供的Comparator排序。
 * 迭代SortedMap的集合视图（entrySet、keySet、values方法的返回）时，会反映此顺序。
 * 提供了几个附加操作以利于排序。（该接口是SortedSet的类似接口）
 *
 * <p>All keys inserted into a sorted map must implement the {@code Comparable}
 * interface (or be accepted by the specified comparator).  Furthermore, all
 * such keys must be <em>mutually comparable</em>: {@code k1.compareTo(k2)} (or
 * {@code comparator.compare(k1, k2)}) must not throw a
 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in
 * the sorted map.  Attempts to violate this restriction will cause the
 * offending method or constructor invocation to throw a
 * {@code ClassCastException}.
 * 插入到排序映射中的所有键都必须实现Comparable接口（或被指定的比较器接受）。
 * 此外，所有这些key必须相互可比较：
 * sortedMap中的任何两个键调用 k1.compareTo(k2) 或comparator.compare(k1, k2) 必须不能抛出ClassCastException。
 * 违反此限制将导致有问题的方法或构造函数调用抛出ClassCastException。
 *
 * <p>Note that the ordering maintained by a sorted map (whether or not an
 * explicit comparator is provided) must be <em>consistent with equals</em> if
 * the sorted map is to correctly implement the {@code Map} interface.  (See
 * the {@code Comparable} interface or {@code Comparator} interface for a
 * precise definition of <em>consistent with equals</em>.)  This is so because
 * the {@code Map} interface is defined in terms of the {@code equals}
 * operation, but a sorted map performs all key comparisons using its
 * {@code compareTo} (or {@code compare}) method, so two keys that are
 * deemed equal by this method are, from the standpoint of the sorted map,
 * equal.  The behavior of a tree map <em>is</em> well-defined even if its
 * ordering is inconsistent with equals; it just fails to obey the general
 * contract of the {@code Map} interface.
 * 请注意，如果SortedMap要正确实现Map接口，则SortedMap维护的顺序（无论是否提供显式比较器）必须与equals一致。
 * （参见Comparable接口或Comparator接口，了解与equals一致的的精确定义）
 * 这是因为Map接口是根据equals操作定义的，但SortedMap使用其compareTo（或compare）方法执行所有key的比较，
 * 因此，从SortedMap的角度来看，通过这种方法认为两个key是相等的。
 * 树映射的行为是定义良好的，即使其顺序与equals不一致；它只是没有遵守Map接口的正常约束。
 *
 * <p>All general-purpose sorted map implementation classes should provide four
 * "standard" constructors. It is not possible to enforce this recommendation
 * though as required constructors cannot be specified by interfaces. The
 * expected "standard" constructors for all sorted map implementations are:
 * 所有通用的SortedMap实现类都应该提供四个“标准”构造函数。
 * 因为接口无法指定所需的构造函数，所以不可能强制执行此建议。
 * 所有SortedMap指定实现的“标准”构造函数如下：
 * 
 * <ol>
 *   <li>A void (no arguments) constructor, which creates an empty sorted map
 *   sorted according to the natural ordering of its keys.</li>
 *   一个空（无参数）构造函数，它创建一个根据键的自然顺序排序的空SortedMap。
 *   
 *   <li>A constructor with a single argument of type {@code Comparator}, which
 *   creates an empty sorted map sorted according to the specified comparator.</li>
 *   具有Comparator类型的单个参数的构造函数，它创建一个根据指定的比较器排序的空SortedMap。
 *   
 *   <li>A constructor with a single argument of type {@code Map}, which creates
 *   a new map with the same key-value mappings as its argument, sorted
 *   according to the keys' natural ordering.</li>
 *   具有Map类型的单个参数的构造函数，该构造函数创建一个新的map，
 *   该map具有与其参数相同的键值映射，并根据键的自然顺序进行排序。
 *   
 *   <li>A constructor with a single argument of type {@code SortedMap}, which
 *   creates a new sorted map with the same key-value mappings and the same
 *   ordering as the input sorted map.</li>
 *   具有SortedMap类型的单个参数的构造函数，它创建一个新的SortedMap，
 *   该映射具有与输入的SortedMap相同的键值映射和顺序。
 * </ol>
 *
 * <p><strong>Note</strong>: several methods return submaps with restricted key
 * ranges. Such ranges are <em>half-open</em>, that is, they include their low
 * endpoint but not their high endpoint (where applicable).  If you need a
 * <em>closed range</em> (which includes both endpoints), and the key type
 * allows for calculation of the successor of a given key, merely request
 * the subrange from {@code lowEndpoint} to
 * {@code successor(highEndpoint)}.  For example, suppose that {@code m}
 * is a map whose keys are strings.  The following idiom obtains a view
 * containing all of the key-value mappings in {@code m} whose keys are
 * between {@code low} and {@code high}, inclusive:<pre>
 *   SortedMap&lt;String, V&gt; sub = m.subMap(low, high+"\0");</pre>
 * 注意：有几种方法返回有限键范围的子map。
 * 这些范围是半开放的，也就是说，它们包括其低端，但不包括其高端。
 * 如果您需要一个闭合范围（包括两个端点），并且键类型允许计算给定键的后续项，
 * 只需请求从lowEndpoint到succession(highEndpoint)的子范围。
 * 例如，假设m是一个键为字符串的映射。下面的习惯用法获得一个视图，其中包含m中的所有键值映射，
 * 这些键值映射的键值介于low和high之间（包括这两个键值）: sub = m.subMap(low, high+"\0");
 *
 * A similar technique can be used to generate an <em>open range</em>
 * (which contains neither endpoint).  The following idiom obtains a
 * view containing all of the key-value mappings in {@code m} whose keys
 * are between {@code low} and {@code high}, exclusive:<pre>
 *   SortedMap&lt;String, V&gt; sub = m.subMap(low+"\0", high);</pre>
 * 类似的技术可用于生成开放范围（其中两个端点都不包含）。
 * 下面的习惯用法获得一个视图，其中包含m中的所有键值映射，
 * 这些键值映射的键值介于low和high之间（包括这两个键值）: m.subMap(low+"\0", high);
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Josh Bloch
 * @see Map
 * @see TreeMap
 * @see SortedSet
 * @see Comparator
 * @see Comparable
 * @see Collection
 * @see ClassCastException
 * @since 1.2
 */

public interface SortedMap<K,V> extends Map<K,V> {
    /**
     * Returns the comparator used to order the keys in this map, or
     * {@code null} if this map uses the {@linkplain Comparable
     * natural ordering} of its keys.
     * 返回用于对此映射中的键进行排序的比较器，
     * 如果此映射使用其键的Comparable自然排序，则返回null。
     *
     * @return the comparator used to order the keys in this map,
     *         or {@code null} if this map uses the natural ordering
     *         of its keys
     */
    Comparator<? super K> comparator();

    /**
     * Returns a view of the portion of this map whose keys range from
     * {@code fromKey}, inclusive, to {@code toKey}, exclusive.  (If
     * {@code fromKey} and {@code toKey} are equal, the returned map
     * is empty.)  The returned map is backed by this map, so changes
     * in the returned map are reflected in this map, and vice-versa.
     * The returned map supports all optional map operations that this
     * map supports.
     * 返回此map部分的视图，其键的范围从fromKey（包含）到toKey（不包含）。
     * （如果fromKey等于toKey，则返回的map是空的）
     * 返回的map由该map支持，因此返回map中的更改将反映在该map中，反之亦然。
     * 返回的map支持此map支持的所有可选map操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
     * 尝试在返回的map中插入不在其区间的键时，将抛出IllegalArgumentException。
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned map
     * @param toKey high endpoint (exclusive) of the keys in the returned map
     * @return a view of the portion of this map whose keys range from
     *         {@code fromKey}, inclusive, to {@code toKey}, exclusive
     * @throws ClassCastException if {@code fromKey} and {@code toKey}
     *         cannot be compared to one another using this map's comparator
     *         (or, if the map has no comparator, using natural ordering).
     *         Implementations may, but are not required to, throw this
     *         exception if {@code fromKey} or {@code toKey}
     *         cannot be compared to keys currently in the map.
     * @throws NullPointerException if {@code fromKey} or {@code toKey}
     *         is null and this map does not permit null keys
     * @throws IllegalArgumentException if {@code fromKey} is greater than
     *         {@code toKey}; or if this map itself has a restricted
     *         range, and {@code fromKey} or {@code toKey} lies
     *         outside the bounds of the range
     */
    SortedMap<K,V> subMap(K fromKey, K toKey);

    /**
     * Returns a view of the portion of this map whose keys are
     * strictly less than {@code toKey}.  The returned map is backed
     * by this map, so changes in the returned map are reflected in
     * this map, and vice-versa.  The returned map supports all
     * optional map operations that this map supports.
     * 返回此map中键严格小于toKey的部分的视图。
     * 返回的map由该map支持，因此返回map中的更改将反映在该map中，反之亦然。
     * 返回的map支持此map支持的所有可选map操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
     * 尝试在返回的map中插入不在其区间的键时，将抛出IllegalArgumentException。
     *
     * @param toKey high endpoint (exclusive) of the keys in the returned map
     *              返回的map中不包含该高端点。
     * @return a view of the portion of this map whose keys are strictly
     *         less than {@code toKey}
     * @throws ClassCastException if {@code toKey} is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if {@code toKey} does not implement {@link Comparable}).
     *         Implementations may, but are not required to, throw this
     *         exception if {@code toKey} cannot be compared to keys
     *         currently in the map.
     * @throws NullPointerException if {@code toKey} is null and
     *         this map does not permit null keys
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and {@code toKey} lies outside the
     *         bounds of the range
     */
    SortedMap<K,V> headMap(K toKey);

    /**
     * Returns a view of the portion of this map whose keys are
     * greater than or equal to {@code fromKey}.  The returned map is
     * backed by this map, so changes in the returned map are
     * reflected in this map, and vice-versa.  The returned map
     * supports all optional map operations that this map supports.
     * 返回此map中键大于等于fromKey的部分的视图。
     * 返回的map由该map支持，因此返回map中的更改将反映在该map中，反之亦然。
     * 返回的map支持此map支持的所有可选map操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
     * 尝试在返回的map中插入不在其区间的键时，将抛出IllegalArgumentException。
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned map
     *                返回的map中包括低端点。
     * @return a view of the portion of this map whose keys are greater
     *         than or equal to {@code fromKey}
     * @throws ClassCastException if {@code fromKey} is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if {@code fromKey} does not implement {@link Comparable}).
     *         Implementations may, but are not required to, throw this
     *         exception if {@code fromKey} cannot be compared to keys
     *         currently in the map.
     * @throws NullPointerException if {@code fromKey} is null and
     *         this map does not permit null keys
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and {@code fromKey} lies outside the
     *         bounds of the range
     */
    SortedMap<K,V> tailMap(K fromKey);

    /**
     * Returns the first (lowest) key currently in this map.
     * 返回此map中第一个（最低）的key。
     *
     * @return the first (lowest) key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    K firstKey();

    /**
     * Returns the last (highest) key currently in this map.
     * 返回此map中最后一个（最高）key。
     *
     * @return the last (highest) key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    K lastKey();

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set's iterator returns the keys in ascending order.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     * 返回该map中包含的key的一个Set视图。Set的迭代器按升序返回键。
     * 该set由该map支持，因此map中的修改将反映到set中，反之亦然。
     * 如果在对set进行迭代时修改map（通过迭代器自己的remove操作除外），则迭代的结果是未定义的。
     * 该set支持元素移除，即通过Iterator.remove、Set.remove、removeAll、retainAll
     * 和clear操作，将映射从map中移除。不支持add或者addAll操作。
     * 
     * @return a set view of the keys contained in this map, sorted in
     *         ascending order
     */
    Set<K> keySet();

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection's iterator returns the values in ascending order
     * of the corresponding keys.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     * 返回一个该map中包含的值的Collection视图。Collection的迭代器按相应键的升序返回值。
     * 该Collection由该map支持，因此map中的修改将反映到Collection中，反之亦然。
     * 如果在对Collection进行迭代时修改map（通过迭代器自己的remove操作除外），则迭代的结果是未定义的。
     * 该Collection支持元素移除，即通过Iterator.remove、Collection.remove、removeAll、retainAll
     * 和clear操作，将映射从map中移除。不支持add或者addAll操作。
     *
     * @return a collection view of the values contained in this map,
     *         sorted in ascending key order
     */
    Collection<V> values();

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set's iterator returns the entries in ascending key order.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     * 返回该map中包含的映射的一个Set视图。Set的迭代器以键的升序顺序返回条目。
     * 该set由该map支持，因此map中的修改将反映到set中，反之亦然。
     * 如果在对set进行迭代时修改map（通过迭代器自己的remove操作除外，
     * 或者对迭代器返回的映射项执行setValue操作），则迭代的结果是未定义的。
     * 该set支持元素移除，即通过Iterator.remove、Set.remove、removeAll、retainAll
     * 和clear操作，将映射从map中移除。不支持add或者addAll操作。
     *
     * @return a set view of the mappings contained in this map,
     *         sorted in ascending key order
     */
    Set<Map.Entry<K, V>> entrySet();
}
```
总结：
1. SortedMap是Map的一种特殊实现，键是有序的。
2. 有序性就体现在其返回的集合视图（entrySet、keySet、values）是有序的。
3. 因为要排序，所以key必须实现Comparable接口。
4. 提供了一些返回该map部分视图的方法:
    - 4.1 subMap(K fromKey, K toKey) 返回 fromKey<=key<toKey 改区间的所有条目
    - 4.2 headMap(K toKey) 返回 <toKey的所有条目
    - 4.3 tailMap(K fromKey) 返回 >=fromKey的所有条目