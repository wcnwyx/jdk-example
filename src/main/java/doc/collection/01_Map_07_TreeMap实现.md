#TreeMap源码分析

##一： 类注释及内部变量预览  
```java
/**
 * A Red-Black tree based {@link NavigableMap} implementation.
 * The map is sorted according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} provided at map
 * creation time, depending on which constructor is used.
 * 基于红黑树的NavigableMap实现。 map根据其键的Comparable进行自然排序，
 * 或者由map创建时提供的Comparator进行排序，具体取决于使用的构造函数。
 *
 * <p>This implementation provides guaranteed log(n) time cost for the
 * {@code containsKey}, {@code get}, {@code put} and {@code remove}
 * operations.  Algorithms are adaptations of those in Cormen, Leiserson, and
 * Rivest's <em>Introduction to Algorithms</em>.
 * 此实现为containsKey、get、put和remove操作提供了保证的log(n)时间开销。
 *
 * <p>Note that the ordering maintained by a tree map, like any sorted map, and
 * whether or not an explicit comparator is provided, must be <em>consistent
 * with {@code equals}</em> if this sorted map is to correctly implement the
 * {@code Map} interface.  (See {@code Comparable} or {@code Comparator} for a
 * precise definition of <em>consistent with equals</em>.)  This is so because
 * the {@code Map} interface is defined in terms of the {@code equals}
 * operation, but a sorted map performs all key comparisons using its {@code
 * compareTo} (or {@code compare}) method, so two keys that are deemed equal by
 * this method are, from the standpoint of the sorted map, equal.  The behavior
 * of a sorted map <em>is</em> well-defined even if its ordering is
 * inconsistent with {@code equals}; it just fails to obey the general contract
 * of the {@code Map} interface.
 * 请注意，与任何SortedMap一样，如果此Sortedmap要正确实现Map接口，
 * TreeMap维护的顺序以及是否提供了显式比较器都必须与equals一致。
 * （参见Comparable或Comparator，了解与equals一致的的精确定义）
 * 这是因为Map接口是根据equals操作定义的，但是SortedMap使用其compareTo（或compare）方法执行所有键比较，
 * 因此从SortedMap的角度来看，该方法认为相等的两个键才是相等的。
 * SortedMap即使其顺序与equals不一致，这种行为也是定义良好的；
 * 它只是没有准守Map接口的正常约束。
 * 为什么这么说呢？因为Map只需要简单的判断相等或不相等即可，所以一个equals就够用了，
 * 但是一个SortedMap需要比较两个键的大小，所以只能用比较器，然后也就通过比较器来判断相等的情况。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a map concurrently, and at least one of the
 * threads modifies the map structurally, it <em>must</em> be synchronized
 * externally.  (A structural modification is any operation that adds or
 * deletes one or more mappings; merely changing the value associated
 * with an existing key is not a structural modification.)  This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedSortedMap Collections.synchronizedSortedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map: <pre>
 *   SortedMap m = Collections.synchronizedSortedMap(new TreeMap(...));</pre>
 * 注意，此实现不是同步的。
 * 如果多个线程同时访问一个哈希映射，并且至少有一个线程在结构上修改该映射，那么它必须在外部进行同步。
 * （结构修改是添加或删除一个或多个映射的任何操作；仅更改与实例已包含的键关联的值不是结构修改。）
 * 这通常是通过在自然封装的某个对象上进行同步来实现的。
 * 如果不存在这样的对象，则应使用Collections.synchronizedSortedMap方法“包装”map。
 * 最好在创建时执行此操作，以防止意外不同步地访问映射：
 *   SortedMap m = Collections.synchronizedSortedMap(new TreeMap(...));
 *
 * <p>The iterators returned by the {@code iterator} method of the collections
 * returned by all of this class's "collection view methods" are
 * <em>fail-fast</em>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * {@code remove} method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * 此类的所有“集合视图方法”返回的迭代器都是快速失败的：
 * 如果在创建迭代器后的任何时候，以迭代器自己的remove方法以外的任何方式对映射进行结构修改，
 * 迭代器将抛出ConcurrentModificationException。
 * 因此，在面对并发修改时，迭代器会快速、干净地失败，而不是在将来的不确定时间冒着任意、不确定行为的风险。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <em>the fail-fast behavior of iterators
 * should be used only to detect bugs.</em>
 * 请注意，无法保证迭代器的快速失效行为，因为一般来说，在存在非同步并发修改的情况下，不可能做出任何硬保证。
 * 快速失败迭代器以最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的正确性的程序是错误的：迭代器的快速失败行为应该只用于检测bug。
 *
 * <p>All {@code Map.Entry} pairs returned by methods in this class
 * and its views represent snapshots of mappings at the time they were
 * produced. They do <strong>not</strong> support the {@code Entry.setValue}
 * method. (Note however that it is possible to change mappings in the
 * associated map using {@code put}.)
 * 此类中的方法及其视图返回的所有Map.Entry对，表示生成时的映射快照。
 * 他们不支持Entry.setValue方法。（但是请注意，可以使用put更改关联map中的映射。）
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Josh Bloch and Doug Lea
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @since 1.2
 */

public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{
    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     * 用于维护此tree map中顺序的比较器，如果它使用其键的自然顺序，则为null。
     * @serial
     */
    private final Comparator<? super K> comparator;

    //树的根节点
    private transient Entry<K,V> root;

    /**
     * The number of entries in the tree
     * 树中的条目数量
     */
    private transient int size = 0;

    /**
     * The number of structural modifications to the tree.
     * 此tree map被结构修改的次数
     */
    private transient int modCount = 0;


    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;  //左孩节点
        Entry<K,V> right; //右孩节点
        Entry<K,V> parent;//父节点
        boolean color = BLACK; //红黑树中的颜色

        /**
         * Make a new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }
}
```
总结：
1. TreeMap实现了Map、SortedMap、NavigableMap接口，是一个有顺序的Map，并且提供了一些导航方法。
2. 内部采用红黑树结构。
    - 2.1 保证了put、get、remove等操作的log(n)时间开销。
    - 2.2 不同于Hashtable和HashMap，不存在数组的扩容、重建、负载因子这些，但是会有树的平衡操作。
    - 2.3 每一个键值对即为一个树节点。
3. key的比较，要么使用构造方法传入的Comparator，要么使用key的Comparable接口。
4. 不是同步的，线程不安全。

##二： put逻辑  
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     * 将指定的值和键放入到map。如果map中以前已经有了该键的映射，则老值将被替换。
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null) {
            //根节点为空，则将新的key-value作为根节点
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        
        //走到这里说明有根节点了，那就根据key比较大小，找到要放到哪个节点（parent)下面
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths 拆分comparator和comparable两种方式
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            //通过comparator来比较大小
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    //注意，如果直接找到了该key，直接修改value就返回了
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            //通过Key实现的Comparable接口来比较大小
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    //注意，如果直接找到了该key，直接修改value就返回了
                    return t.setValue(value);
            } while (t != null);
        }
        
        //根据和parent的比较结果，放到parent的left或right节点上
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        
        //上面的逻辑是将节点加到了树上，但是加过后可不一定符合红黑树的特性了，
        //fixAfterInsertion就是修正树，直到符合红黑树特性（变色、旋转这些操作），
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }

}
```
总结：  
1. 通过comparator或者comparable来比较大小，将新节点添加到树上。  
2. 添加之后需要通过方法fixAfterInsertion来调整树，已满足红黑树特性。

##三：get逻辑
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * 返回指定键映射到的值，如果此map不包含该键的映射，则返回null。
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key} compares
     * equal to {@code k} according to the map's ordering, then this
     * method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     * 更正规地说，如果这个map包含一个从键k到值v的映射，使得key根据map的排序比较等于k，
     * 那么这个方法返回v；否则返回null。（最多可以有一个这样的映射。）
     *
     * <p>A return value of {@code null} does not <em>necessarily</em>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     * 返回null不一定表示map不包含键的映射；也可能是map将密key射到了null（和Hashtable就不一样了）。
     * containsKey操作可用于区分这两种情况
     *
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    /**
     * Returns this map's entry for the given key, or {@code null} if the map
     * does not contain an entry for the key.
     * 返回给定键的此映射项，如果映射不包含该键的项，则返回null。
     *
     * @return this map's entry for the given key, or {@code null} if the map
     *         does not contain an entry for the key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            //比较器不为空，则通过比较器来比较，找到树中的key节点
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        //从根节点循环，通过Key实现的Comparable接口进行比较，找到该key的节点
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * Version of getEntry using comparator. Split off from getEntry
     * for performance. (This is not worth doing for most methods,
     * that are less dependent on comparator performance, but is
     * worthwhile here.)
     * 使用比较器（Comparator）的getEntry版本。从getEntry分离以获得性能。
     * （对于大多数不太依赖于比较器性能的方法来说，这是不值得的，但在这里是值得的。）
     */
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            //循环树，找到该key的节点。
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }
}
```
总结：
1. 通过comparator或者comparable来比较大小，找到树上的key节点。

##四： remove逻辑
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{
    /**
     * Removes the mapping for this key from this TreeMap if present.
     *
     * @param  key key for which mapping should be removed
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }

    /**
     * Delete node p, and then rebalance the tree.
     * 删除节点p，然后重新平衡树
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        //省略了树的操作代码
    }
}
```

##五： SortedMap、NavigableMap部分接口实现
因为是树型结构，所以很方便的实现排序、定位、裁剪这些操作。  
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{
    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     * 返回TreeMap中的第一个条目（根据TreeMap的键排序函数）。如果树映射为空，则返回null。
     */
    final Entry<K,V> getFirstEntry() {
        //从根节点循环，找到树的最小节点
        Entry<K,V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     * 返回TreeMap中的最后一个条目（根据TreeMap的键排序函数）。如果树映射为空，则返回null。
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

}
```