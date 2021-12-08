#NavigableMap接口定义  

```java
/**
 * A {@link SortedMap} extended with navigation methods returning the
 * closest matches for given search targets. Methods
 * {@code lowerEntry}, {@code floorEntry}, {@code ceilingEntry},
 * and {@code higherEntry} return {@code Map.Entry} objects
 * associated with keys respectively less than, less than or equal,
 * greater than or equal, and greater than a given key, returning
 * {@code null} if there is no such key.  Similarly, methods
 * {@code lowerKey}, {@code floorKey}, {@code ceilingKey}, and
 * {@code higherKey} return only the associated keys. All of these
 * methods are designed for locating, not traversing entries.
 * 一个SortedMap，其扩展了导航(navigation)方法，返回给定搜索目标的最接近匹配项。
 * 方法lowerEntry、floorEntry、ceilingEntry和higherEntry返回Map.Entry对象，
 * 这些对象分别与小于、小于或等于、大于或等于和大于给定键的键相关联，
 * 如果没有此类键，则返回null。
 * 类似地，方法lowerKey、floorKey、ceilingKey和higherKey只返回关联的键。
 * 所有这些方法都是为定位而设计的，而不是遍历条目。
 *
 * <p>A {@code NavigableMap} may be accessed and traversed in either
 * ascending or descending key order.  The {@code descendingMap}
 * method returns a view of the map with the senses of all relational
 * and directional methods inverted. The performance of ascending
 * operations and views is likely to be faster than that of descending
 * ones.  Methods {@code subMap}, {@code headMap},
 * and {@code tailMap} differ from the like-named {@code
 * SortedMap} methods in accepting additional arguments describing
 * whether lower and upper bounds are inclusive versus exclusive.
 * Submaps of any {@code NavigableMap} must implement the {@code
 * NavigableMap} interface.
 * 一个NavigableMap可以按键的升序或降序进行访问和遍历。
 * descendingMap方法返回一个map视图，其中所有和方向性相关的方法都颠倒了。
 * 升序操作和视图的性能可能比降序操作和视图的性能更快。
 * 方法subMap、headMap和tailMap不同于SortedMap中类似命名的方法，
 * 它们接受额外的参数来描述上界和下界是包含的还是不包含的。
 * 任何NavigableMap的子map必须实现NavigableMap接口。
 *
 * <p>This interface additionally defines methods {@code firstEntry},
 * {@code pollFirstEntry}, {@code lastEntry}, and
 * {@code pollLastEntry} that return and/or remove the least and
 * greatest mappings, if any exist, else returning {@code null}.
 * 此接口还定义了方法firstEntry、pollFirstEntry、lastEntry和pollLastEntry，
 * 这些方法返回 和/或 删除最小和最大映射（如果存在），否则返回null。
 *
 * <p>Implementations of entry-returning methods are expected to
 * return {@code Map.Entry} pairs representing snapshots of mappings
 * at the time they were produced, and thus generally do <em>not</em>
 * support the optional {@code Entry.setValue} method. Note however
 * that it is possible to change mappings in the associated map using
 * method {@code put}.
 * 条目返回方法的实现预期将返回Map.entry对，这些对表示生成映射时的快照，
 * 因此通常不支持可选的entry.setValue方法。
 * 但是请注意，可以使用方法put更改关联map中的映射。
 *
 * <p>Methods
 * {@link #subMap(Object, Object) subMap(K, K)},
 * {@link #headMap(Object) headMap(K)}, and
 * {@link #tailMap(Object) tailMap(K)}
 * are specified to return {@code SortedMap} to allow existing
 * implementations of {@code SortedMap} to be compatibly retrofitted to
 * implement {@code NavigableMap}, but extensions and implementations
 * of this interface are encouraged to override these methods to return
 * {@code NavigableMap}.  Similarly,
 * {@link #keySet()} can be overriden to return {@code NavigableSet}.
 * 方法subMap(K, K)、headMap(K)和tailMap(K)被指定为返回SortedMap，
 * 为了允许对SortedMap的现有实现进行兼容的改装，以实现NavigableMap，
 * 但是鼓励该接口的扩展和实现重写这些方法以返回NavigableMap。
 * 类似地，可以重写keySet()以返回NavigableSet。
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author Doug Lea
 * @author Josh Bloch
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @since 1.6
 */
public interface NavigableMap<K,V> extends SortedMap<K,V> {
    /**
     * Returns a key-value mapping associated with the greatest key
     * strictly less than the given key, or {@code null} if there is
     * no such key.
	 * 返回与（小于给定键的最大键）关联的key-value映射，如果没有这样的键，则返回null。
     *
     * @param key the key
     * @return an entry with the greatest key less than {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    Map.Entry<K,V> lowerEntry(K key);

    /**
     * Returns the greatest key strictly less than the given key, or
     * {@code null} if there is no such key.
	 * 返回小于给定键的最大键，如果没有此类键，则返回null。
     *
     * @param key the key
     * @return the greatest key less than {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    K lowerKey(K key);

    /**
     * Returns a key-value mapping associated with the greatest key
     * less than or equal to the given key, or {@code null} if there
     * is no such key.
	 * 返回与（小于或等于给定键的最大键）关联的key-value映射，
	 * 如果没有此类键，则返回null。
     *
     * @param key the key
     * @return an entry with the greatest key less than or equal to
     *         {@code key}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    Map.Entry<K,V> floorEntry(K key);

    /**
     * Returns the greatest key less than or equal to the given key,
     * or {@code null} if there is no such key.
	 * 返回小于或等于给定键的最大键，如果没有此类键则返回null
     *
     * @param key the key
     * @return the greatest key less than or equal to {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    K floorKey(K key);

    /**
     * Returns a key-value mapping associated with the least key
     * greater than or equal to the given key, or {@code null} if
     * there is no such key.
	 * 返回与（大于或等于给定键的最小键）相关联的key-value映射，
	 * 如果没有这样的键，则返回null。
     *
     * @param key the key
     * @return an entry with the least key greater than or equal to
     *         {@code key}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    Map.Entry<K,V> ceilingEntry(K key);

    /**
     * Returns the least key greater than or equal to the given key,
     * or {@code null} if there is no such key.
	 * 返回大于或等于给定键的最小键，如果没有这样的键，则返回null。
     *
     * @param key the key
     * @return the least key greater than or equal to {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    K ceilingKey(K key);

    /**
     * Returns a key-value mapping associated with the least key
     * strictly greater than the given key, or {@code null} if there
     * is no such key.
	 * 返回与（大于给定键的最小键）相关联的key-value映射，
	 * 如果没有这样的键，则返回null。
     *
     * @param key the key
     * @return an entry with the least key greater than {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    Map.Entry<K,V> higherEntry(K key);

    /**
     * Returns the least key strictly greater than the given key, or
     * {@code null} if there is no such key.
	 * 返回大于给定键的最小键，如果没有这样的键，则返回null。
     *
     * @param key the key
     * @return the least key greater than {@code key},
     *         or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    K higherKey(K key);

    /**
     * Returns a key-value mapping associated with the least
     * key in this map, or {@code null} if the map is empty.
	 * 返回该map中与最小键关联的key-value映射。如无则返回null。
     *
     * @return an entry with the least key,
     *         or {@code null} if this map is empty
     */
    Map.Entry<K,V> firstEntry();

    /**
     * Returns a key-value mapping associated with the greatest
     * key in this map, or {@code null} if the map is empty.
	 * 返回该map中与最大键关联的key-value映射。如无则返回null。
     *
     * @return an entry with the greatest key,
     *         or {@code null} if this map is empty
     */
    Map.Entry<K,V> lastEntry();

    /**
     * Removes and returns a key-value mapping associated with
     * the least key in this map, or {@code null} if the map is empty.
	 * 移除并返回该map中与最小键关联的key-value映射。如无则返回null。
     *
     * @return the removed first entry of this map,
     *         or {@code null} if this map is empty
     */
    Map.Entry<K,V> pollFirstEntry();

    /**
     * Removes and returns a key-value mapping associated with
     * the greatest key in this map, or {@code null} if the map is empty.
	 * 移除并返回该map中与最大键关联的key-value映射。如无则返回null。
     *
     * @return the removed last entry of this map,
     *         or {@code null} if this map is empty
     */
    Map.Entry<K,V> pollLastEntry();

    /**
     * Returns a reverse order view of the mappings contained in this map.
     * The descending map is backed by this map, so changes to the map are
     * reflected in the descending map, and vice-versa.  If either map is
     * modified while an iteration over a collection view of either map
     * is in progress (except through the iterator's own {@code remove}
     * operation), the results of the iteration are undefined.
	 * 返回此map中包含的映射的降序视图。
	 * 该降序map由该map支持，因此修改此map将反映到降序map中，反之亦然。
	 * 如果在对任一map的集合视图进行迭代时修改了任一map
	 * （通过迭代器自己的remove操作除外），则迭代的结果是未定义的。
     *
     * <p>The returned map has an ordering equivalent to
     * <tt>{@link Collections#reverseOrder(Comparator) Collections.reverseOrder}(comparator())</tt>.
     * The expression {@code m.descendingMap().descendingMap()} returns a
     * view of {@code m} essentially equivalent to {@code m}.
     *
     * @return a reverse order view of this map
     */
    NavigableMap<K,V> descendingMap();

    /**
     * Returns a {@link NavigableSet} view of the keys contained in this map.
     * The set's iterator returns the keys in ascending order.
     * The set is backed by the map, so changes to the map are reflected in
     * the set, and vice-versa.  If the map is modified while an iteration
     * over the set is in progress (except through the iterator's own {@code
     * remove} operation), the results of the iteration are undefined.  The
     * set supports element removal, which removes the corresponding mapping
     * from the map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations.
     * It does not support the {@code add} or {@code addAll} operations.
	 * 返回此map中包含的key的一个NavigableSet视图。Set的迭代器按升序返回键。
	 * 该Set由该map支持，因此修改此map将反映到Set中，反之亦然。
	 * 如果在对Set进行迭代时修改map（通过迭代器自己的remove操作除外），
	 * 则迭代的结果是未定义的。
	 * 该Set支持元素移除，即通过Iterator.remove、Set.remove、removeAll、retainal
	 * 和clear操作从map中移除相应的映射。不支持add、addAll操作。
     *
     * @return a navigable set view of the keys in this map
     */
    NavigableSet<K> navigableKeySet();

    /**
     * Returns a reverse order {@link NavigableSet} view of the keys contained in this map.
     * The set's iterator returns the keys in descending order.
     * The set is backed by the map, so changes to the map are reflected in
     * the set, and vice-versa.  If the map is modified while an iteration
     * over the set is in progress (except through the iterator's own {@code
     * remove} operation), the results of the iteration are undefined.  The
     * set supports element removal, which removes the corresponding mapping
     * from the map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations.
     * It does not support the {@code add} or {@code addAll} operations.
	 * 返回此map中包含的key的降序NavigableSet视图。Set的迭代器按降序返回键。
	 * 该Set由该map支持，因此修改此map将反映到Set中，反之亦然。
	 * 如果在对Set进行迭代时修改map（通过迭代器自己的remove操作除外），
	 * 则迭代的结果是未定义的。
	 * 该Set支持元素移除，即通过Iterator.remove、Set.remove、removeAll、retainal
	 * 和clear操作从map中移除相应的映射。不支持add、addAll操作。
     *
     * @return a reverse order navigable set view of the keys in this map
     */
    NavigableSet<K> descendingKeySet();

    /**
     * Returns a view of the portion of this map whose keys range from
     * {@code fromKey} to {@code toKey}.  If {@code fromKey} and
     * {@code toKey} are equal, the returned map is empty unless
     * {@code fromInclusive} and {@code toInclusive} are both true.  The
     * returned map is backed by this map, so changes in the returned map are
     * reflected in this map, and vice-versa.  The returned map supports all
     * optional map operations that this map supports.
	 * 返回此map中键范围从fromKey到toKey的部分的视图。
	 * 如果fromKey和toKey相等，则返回的map为空，除非fromInclusive和toInclusive都为true。
	 * 该返回的map由该map支持，因此对该返回的map进行的更改也会反映到该map中，反之亦然。
	 * 返回的map支持此map支持的所有可选映射操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside of its range, or to construct a
     * submap either of whose endpoints lie outside its range.
	 * 当尝试往返回的map中插入一个不在其范围的key时，
	 * 或构造端点位于其范围之外的子map时，会抛出IllegalArgumentException。
     *
     * @param fromKey low endpoint of the keys in the returned map
     * @param fromInclusive {@code true} if the low endpoint
     *        is to be included in the returned view
     * @param toKey high endpoint of the keys in the returned map
     * @param toInclusive {@code true} if the high endpoint
     *        is to be included in the returned view
     * @return a view of the portion of this map whose keys range from
     *         {@code fromKey} to {@code toKey}
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
    NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                             K toKey,   boolean toInclusive);

    /**
     * Returns a view of the portion of this map whose keys are less than (or
     * equal to, if {@code inclusive} is true) {@code toKey}.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
	 * 返回此map中键小于（或等于，如果inclusive为true）toKey的部分的视图。
	 * 该返回的map由该map支持，因此对该返回的map进行的更改也会反映到该map中，反之亦然。
	 * 返回的map支持此map支持的所有可选映射操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
	 * 当尝试往返回的map中插入一个不在其范围的key时，会抛出IllegalArgumentException。
     *
     * @param toKey high endpoint of the keys in the returned map
     * @param inclusive {@code true} if the high endpoint
     *        is to be included in the returned view
     * @return a view of the portion of this map whose keys are less than
     *         (or equal to, if {@code inclusive} is true) {@code toKey}
     * @throws ClassCastException if {@code toKey} is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if {@code toKey} does not implement {@link Comparable}).
     *         Implementations may, but are not required to, throw this
     *         exception if {@code toKey} cannot be compared to keys
     *         currently in the map.
     * @throws NullPointerException if {@code toKey} is null
     *         and this map does not permit null keys
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and {@code toKey} lies outside the
     *         bounds of the range
     */
    NavigableMap<K,V> headMap(K toKey, boolean inclusive);

    /**
     * Returns a view of the portion of this map whose keys are greater than (or
     * equal to, if {@code inclusive} is true) {@code fromKey}.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
	 * 返回此map中键大于（或等于，如果inclusive为true）fromKey的部分的视图。
	 * 该返回的map由该map支持，因此对该返回的map进行的更改也会反映到该map中，反之亦然。
	 * 返回的map支持此map支持的所有可选映射操作。
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
	 * 当尝试往返回的map中插入一个不在其范围的key时，会抛出IllegalArgumentException。
     *
     * @param fromKey low endpoint of the keys in the returned map
     * @param inclusive {@code true} if the low endpoint
     *        is to be included in the returned view
     * @return a view of the portion of this map whose keys are greater than
     *         (or equal to, if {@code inclusive} is true) {@code fromKey}
     * @throws ClassCastException if {@code fromKey} is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if {@code fromKey} does not implement {@link Comparable}).
     *         Implementations may, but are not required to, throw this
     *         exception if {@code fromKey} cannot be compared to keys
     *         currently in the map.
     * @throws NullPointerException if {@code fromKey} is null
     *         and this map does not permit null keys
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and {@code fromKey} lies outside the
     *         bounds of the range
     */
    NavigableMap<K,V> tailMap(K fromKey, boolean inclusive);

    /**
     * {@inheritDoc}
     *
     * <p>Equivalent to {@code subMap(fromKey, true, toKey, false)}.
     *
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    SortedMap<K,V> subMap(K fromKey, K toKey);

    /**
     * {@inheritDoc}
     *
     * <p>Equivalent to {@code headMap(toKey, false)}.
     *
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    SortedMap<K,V> headMap(K toKey);

    /**
     * {@inheritDoc}
     *
     * <p>Equivalent to {@code tailMap(fromKey, true)}.
     *
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    SortedMap<K,V> tailMap(K fromKey);
}
```