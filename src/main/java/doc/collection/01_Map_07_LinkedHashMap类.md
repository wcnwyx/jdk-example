```java
/**
 * <p>Hash table and linked list implementation of the <tt>Map</tt> interface,
 * with predictable iteration order.  This implementation differs from
 * <tt>HashMap</tt> in that it maintains a doubly-linked list running through
 * all of its entries.  This linked list defines the iteration ordering,
 * which is normally the order in which keys were inserted into the map
 * (<i>insertion-order</i>).  Note that insertion order is not affected
 * if a key is <i>re-inserted</i> into the map.  (A key <tt>k</tt> is
 * reinserted into a map <tt>m</tt> if <tt>m.put(k, v)</tt> is invoked when
 * <tt>m.containsKey(k)</tt> would return <tt>true</tt> immediately prior to
 * the invocation.)
 * Map接口的哈希表和链表实现，具有可预测的迭代顺序。
 * 此实现与HashMap的不同之处在于，它维护一个贯穿其所有条目的双链接列表。
 * 此链表定义了迭代顺序，通常是键插入到映射中的顺序（插入顺序）。
 * 请注意，如果将键重新插入map中，则插入顺序不受影响。
 *
 * <p>This implementation spares its clients from the unspecified, generally
 * chaotic ordering provided by {@link HashMap} (and {@link Hashtable}),
 * without incurring the increased cost associated with {@link TreeMap}.  It
 * can be used to produce a copy of a map that has the same order as the
 * original, regardless of the original map's implementation:
 * 此实现使其客户端免于HashMap（和Hashtable）提供的未指定的、通常混乱的排序，
 * 而不会增加与TreeMap相关的成本。
 * 它可用于生成与原始map顺序相同的map副本，而不考虑原始map的实现：
 * <pre>
 *     void foo(Map m) {
 *         Map copy = new LinkedHashMap(m);
 *         ...
 *     }
 * </pre>
 * This technique is particularly useful if a module takes a map on input,
 * copies it, and later returns results whose order is determined by that of
 * the copy.  (Clients generally appreciate having things returned in the same
 * order they were presented.)
 * 如果模块在输入时获取map，复制它，然后返回由复制顺序决定的结果，则此技术特别有用。
 * （客户端通常喜欢按照相同的顺序归还物品。）
 *
 * <p>A special {@link #LinkedHashMap(int,float,boolean) constructor} is
 * provided to create a linked hash map whose order of iteration is the order
 * in which its entries were last accessed, from least-recently accessed to
 * most-recently (<i>access-order</i>).  This kind of map is well-suited to
 * building LRU caches.  Invoking the {@code put}, {@code putIfAbsent},
 * {@code get}, {@code getOrDefault}, {@code compute}, {@code computeIfAbsent},
 * {@code computeIfPresent}, or {@code merge} methods results
 * in an access to the corresponding entry (assuming it exists after the
 * invocation completes). The {@code replace} methods only result in an access
 * of the entry if the value is replaced.  The {@code putAll} method generates one
 * entry access for each mapping in the specified map, in the order that
 * key-value mappings are provided by the specified map's entry set iterator.
 * <i>No other methods generate entry accesses.</i>  In particular, operations
 * on collection-views do <i>not</i> affect the order of iteration of the
 * backing map.
 * 提供了一个特殊的{LinkedHashMap(int,float,boolean)构造函数}来创建一个LinkedHashMap，
 * 其迭代顺序是其条目最后访问的顺序，从least-recently访问到most-recently访问（访问顺序）。
 * 这种map非常适合构建LRU（Least Recently Used 最近最少使用）缓存。
 * 调用put、putIfAbsent、get、getOrDefault、compute、computeifapsent、computeIfPresent
 * 或merge方法会导致访问相应的条目（假设在调用完成后存在）。
 * replace方法仅在值被替换时才导致对条目的访问。
 * putAll方法为指定map中的每个映射生成一个条目访问，顺序为指定map的条目集迭代器提供键值映射顺序。
 * 没有其他方法生成条目访问。特别是，集合视图上的操作不会影响备份map的迭代顺序。
 *
 * <p>The {@link #removeEldestEntry(Map.Entry)} method may be overridden to
 * impose a policy for removing stale mappings automatically when new mappings
 * are added to the map.
 * 可以重写removeEldestEntry(Map.Entry)方法，以强制实施一个策略，
 * 以便在向map添加新映射时自动删除过时的映射。
 *
 * <p>This class provides all of the optional <tt>Map</tt> operations, and
 * permits null elements.  Like <tt>HashMap</tt>, it provides constant-time
 * performance for the basic operations (<tt>add</tt>, <tt>contains</tt> and
 * <tt>remove</tt>), assuming the hash function disperses elements
 * properly among the buckets.  Performance is likely to be just slightly
 * below that of <tt>HashMap</tt>, due to the added expense of maintaining the
 * linked list, with one exception: Iteration over the collection-views
 * of a <tt>LinkedHashMap</tt> requires time proportional to the <i>size</i>
 * of the map, regardless of its capacity.  Iteration over a <tt>HashMap</tt>
 * is likely to be more expensive, requiring time proportional to its
 * <i>capacity</i>.
 * 此类提供所有可选的Map操作，并允许空元素。
 * 与HashMap一样，它为基本操作（add、contains和remove）提供了恒定的时间性能，
 * 前提是hash函数在bucket之间正确地分散元素。
 * 由于维护链表的额外费用，性能可能略低于HashMap，只有一个例外：
 * 在LinkedHashMap的集合视图上进行迭代需要与映射大小成比例的时间，而不管其容量如何。
 * HashMap上的迭代可能会更加昂贵，需要与其容量成比例的时间。
 *
 * <p>A linked hash map has two parameters that affect its performance:
 * <i>initial capacity</i> and <i>load factor</i>.  They are defined precisely
 * as for <tt>HashMap</tt>.  Note, however, that the penalty for choosing an
 * excessively high value for initial capacity is less severe for this class
 * than for <tt>HashMap</tt>, as iteration times for this class are unaffected
 * by capacity.
 * LinkedHashMap有两个影响其性能的参数：初始容量和负载因子。
 * 它们的定义与HashMap的定义完全相同。
 * 但是，请注意，对于这个类来说，为初始容量选择过高值的惩罚
 * 要比为HashMap选择过高值的惩罚轻，因为这个类的迭代时间不受容量的影响。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a linked hash map concurrently, and at least
 * one of the threads modifies the map structurally, it <em>must</em> be
 * synchronized externally.  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 * 请注意，此实现是不同步的。
 * 如果多个线程同时访问一个LinkedHashMap，并且至少有一个线程在结构上修改该映射，
 * 那么它必须在外部进行同步。这通常是通过在自然封装的某个对象上进行同步来实现的。
 *
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new LinkedHashMap(...));</pre>
 * 如果不存在这样的对象，则应使用Collections.synchronizedMap方法“包装”map。
 * 最好在创建时执行此操作，以防止意外不同步地访问映射：
 *    Map m = Collections.synchronizedMap(new LinkedHashMap(...));
 *
 * A structural modification is any operation that adds or deletes one or more
 * mappings or, in the case of access-ordered linked hash maps, affects
 * iteration order.  In insertion-ordered linked hash maps, merely changing
 * the value associated with a key that is already contained in the map is not
 * a structural modification.  <strong>In access-ordered linked hash maps,
 * merely querying the map with <tt>get</tt> is a structural modification.
 * </strong>)
 * 结构修改是添加或删除一个或多个映射的任何操作，
 * 或者在访问LinkedHashMap的情况下，影响迭代顺序的任何操作。
 * 在 插入顺序 的LinkedHashMap中，仅更改与map中已包含的键相关联的值不是结构修改。
 * 在 访问顺序 的LinkedHashMap中，仅使用get查询映射是一种结构性修改。
 *
 * <p>The iterators returned by the <tt>iterator</tt> method of the collections
 * returned by all of this class's collection view methods are
 * <em>fail-fast</em>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * 该类的所有集合视图方法返回的集合中，iterator方法返回的迭代器是快速失败的：
 * 如果在创建迭代器后的任何时候，以迭代器自己的remove方法以外的任何方式对映射进行结构修改，
 * 迭代器将抛出ConcurrentModificationException。
 * 因此，在面对并发修改时，迭代器会快速、干净地失败，而不是在将来的不确定时间冒着任意、不确定行为的风险。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * 请注意，无法保证迭代器的快速失效行为，因为一般来说，在存在非同步并发修改的情况下，不可能做出任何硬保证。
 * 快速失败迭代器以最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的正确性的程序是错误的：迭代器的快速失败行为应该只用于检测bug。
 *
 * <p>The spliterators returned by the spliterator method of the collections
 * returned by all of this class's collection view methods are
 * <em><a href="Spliterator.html#binding">late-binding</a></em>,
 * <em>fail-fast</em>, and additionally report {@link Spliterator#ORDERED}.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @implNote
 * The spliterators returned by the spliterator method of the collections
 * returned by all of this class's collection view methods are created from
 * the iterators of the corresponding collections.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Josh Bloch
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     HashMap
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.4
 */
public class LinkedHashMap<K,V>
        extends HashMap<K,V>
        implements Map<K,V>
{

    /**
     * HashMap.Node subclass for normal LinkedHashMap entries.
     */
    static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    /**
     * The head (eldest) of the doubly linked list.
     * 双链接列表中的头（最年长的）。
     */
    transient LinkedHashMap.Entry<K,V> head;

    /**
     * The tail (youngest) of the doubly linked list.
     * 双链接列表的尾部（最年轻的）。
     */
    transient LinkedHashMap.Entry<K,V> tail;

    /**
     * The iteration ordering method for this linked hash map: <tt>true</tt>
     * for access-order, <tt>false</tt> for insertion-order.
     * 此链接哈希映射的迭代排序方法：true表示访问顺序，false表示插入顺序。
     *
     * @serial
     */
    final boolean accessOrder;

}
```

##put操作逻辑  
该类的put操作使用的是HashMap中的，未重写put，只是重写了newNode方法。   
```java
public class LinkedHashMap<K,V>
        extends HashMap<K,V>
        implements Map<K,V>
{
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        //HashMap.put方法中，创建新节点，此时创建的是LinkedHashMap.Entry
        LinkedHashMap.Entry<K,V> p =
                new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        //创建完成后加入到该类维护的双向链表中
        linkNodeLast(p);
        return p;
    }
    
    // link at the end of list
    //链接到队列的尾部
    private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            //如果尾部未null，表示一个node还没有，则将该node p也赋值给head
            head = p;
        else {
            //双向链表从尾部加入的逻辑
            // 新尾部节点p的before设置未老尾部节点last，老尾部节点last的after设置未新尾部节点p
            p.before = last;
            last.after = p;
        }
    }

    //该方法在HashMap的putVal、computeIfAbsent、compute、merge方法尾部调用，表示一个新节点加入了
    void afterNodeInsertion(boolean evict) { 
        // possibly remove eldest 可能移除最年长的
        // HashMap调用此方法是evict（驱逐）参数都是true
        LinkedHashMap.Entry<K,V> first;
        //此类的removeEldestEntry方法固定返回false，所以不会驱逐最早的一个节点，就是head头。
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            //如果自定义子类重写的removeEldestEntry方法会返回true
            //这里将调用HashMap的removeNode方法将最早的节点移除
            //这里就体现了注释里说的该类适合构建LRU缓存了
            removeNode(hash(key), key, null, false, true);
        }
    }

    /**
     * Returns <tt>true</tt> if this map should remove its eldest entry.
     * This method is invoked by <tt>put</tt> and <tt>putAll</tt> after
     * inserting a new entry into the map.  It provides the implementor
     * with the opportunity to remove the eldest entry each time a new one
     * is added.  This is useful if the map represents a cache: it allows
     * the map to reduce memory consumption by deleting stale entries.
     * 如果该map需要移除最旧的条目则返回true。
     * 在map中插入新条目后，该方法将被put和putAll方法调用。
     * 它为实现者提供了在每次添加新条目时删除最旧条目的机会。
     * 该map表示一个cache是这将很有用：它允许map通过删除过时的条目来减少内存消耗。
     *
     * <p>Sample use: this override will allow the map to grow up to 100
     * entries and then delete the eldest entry each time a new entry is
     * added, maintaining a steady state of 100 entries.
     * 使用示例：此重写将允许map最多增加到100个条目，
     * 然后在每次添加新条目时删除最老的条目，从而保持100个条目的稳定状态。
     * <pre>
     *     private static final int MAX_ENTRIES = 100;
     *
     *     protected boolean removeEldestEntry(Map.Entry eldest) {
     *        return size() &gt; MAX_ENTRIES;
     *     }
     * </pre>
     *
     * <p>This method typically does not modify the map in any way,
     * instead allowing the map to modify itself as directed by its
     * return value.  It <i>is</i> permitted for this method to modify
     * the map directly, but if it does so, it <i>must</i> return
     * <tt>false</tt> (indicating that the map should not attempt any
     * further modification).  The effects of returning <tt>true</tt>
     * after modifying the map from within this method are unspecified.
     * 此方法通常不会以任何方式修改map，而是允许map按照其返回值的指示修改自身。
     * 允许此方法直接修改map，但如果这样做，则必须返回false（表示map不应尝试进一步修改）。
     * 未指定在此方法中修改map后返回true的效果。
     *
     * <p>This implementation merely returns <tt>false</tt> (so that this
     * map acts like a normal map - the eldest element is never removed).
     * 此实现仅返回false（所以此map的行为类似于普通map-最老的元素永远不会被删除）。
     *
     * @param    eldest The least recently inserted entry in the map, or if
     *           this is an access-ordered map, the least recently accessed
     *           entry.  This is the entry that will be removed it this
     *           method returns <tt>true</tt>.  If the map was empty prior
     *           to the <tt>put</tt> or <tt>putAll</tt> invocation resulting
     *           in this invocation, this will be the entry that was just
     *           inserted; in other words, if the map contains a single
     *           entry, the eldest entry is also the newest.
     * @return   <tt>true</tt> if the eldest entry should be removed
     *           from the map; <tt>false</tt> if it should be retained.
     */
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }
}
```