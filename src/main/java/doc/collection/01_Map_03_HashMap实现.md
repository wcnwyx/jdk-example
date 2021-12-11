#HashMap源码分析  

##一： 类注释及内部变量预览
```java
/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 * 基于哈希表的Map接口实现。此实现提供所有可选映射操作，并允许null值和null键。
 * （HashMap类大致相当于Hashtable，只是它不同步并且允许空值。）
 * 此类不保证map的顺序；特别是，它不能保证顺序在一段时间内保持不变。
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 * 此实现为基本操作(get和put)提供了恒定的时间性能，前提是哈希函数将元素正确地分散在存储桶中。
 * 对集合视图的迭代需要与HashMap实例的"容量"（bucket数）加上其大小（键值映射数）成比例的时间。
 * 因此，如果迭代性能很重要，那么不要将初始容量设置得太高（或负载因子太低），这一点非常重要。
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * HashMap的实例有两个影响其性能的参数： 初始容量 和 负载因子。
 * 容量 是哈希表中的存储桶数，初始容量只是创建哈希表时的容量。
 * 负载因子是在自动增加哈希表容量之前允许哈希表达到的满度的度量。
 * 当哈希表中的条目数超过负载因子和当前容量的乘积时，哈希表将被重新设置（即，重建内部数据结构），
 * 以便哈希表的存储桶数大约是当前存储桶数的两倍。
 *
 * <p>As a general rule, the default load factor (.75) offers a good
 * tradeoff between time and space costs.  Higher values decrease the
 * space overhead but increase the lookup cost (reflected in most of
 * the operations of the <tt>HashMap</tt> class, including
 * <tt>get</tt> and <tt>put</tt>).  The expected number of entries in
 * the map and its load factor should be taken into account when
 * setting its initial capacity, so as to minimize the number of
 * rehash operations.  If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no rehash
 * operations will ever occur.
 * 作为一般规则，默认负载因子（.75）在时间和空间成本之间提供了良好的权衡。
 * 较高的值会减少空间开销，但会增加查找成本（反映在HashMap类的大多数操作中，包括get和put）。
 * 在设置初始容量时，应考虑map中的预期条目数及其负载因子，以尽量减少rehash次数。
 * 如果初始容量大于最大条目数除以加载因子，则不会发生rehash操作。
 *
 * <p>If many mappings are to be stored in a <tt>HashMap</tt>
 * instance, creating it with a sufficiently large capacity will allow
 * the mappings to be stored more efficiently than letting it perform
 * automatic rehashing as needed to grow the table.  Note that using
 * many keys with the same {@code hashCode()} is a sure way to slow
 * down performance of any hash table. To ameliorate impact, when keys
 * are {@link Comparable}, this class may use comparison order among
 * keys to help break ties.
 * 如果要在一个HashMap实例中存储多个映射，那么创建一个足够大的容量的实例将允许更高效地存储映射，
 * 而不是让它根据需要执行自动rehash以增加表。
 * 请注意，使用具有相同hashCode的多个键肯定会降低任何哈希表的性能。
 * 为了改善影响，当键为Comparable时，此类可以使用键之间的比较顺序来帮助打破联系。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 * 请注意，此实现是不同步的。
 * 如果多个线程同时访问一个哈希映射，并且至少有一个线程在结构上修改该映射，那么它必须在外部进行同步。
 * （结构修改是添加或删除一个或多个映射的任何操作；仅更改与实例已包含的键关联的值不是结构修改。）
 * 这通常是通过在自然封装Map的某个对象上进行同步来实现的。
 * 如果不存在这样的对象，则应使用Collections.synchronizedMap方法“包装”map。
 * 最好在创建时执行此操作，以防止意外不同步地访问映射：
 * Map m = Collections.synchronizedMap(new HashMap(...));
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 * 此类的所有“集合视图方法”返回的迭代器都是快速失败的：
 * 如果在创建迭代器后的任何时候，以迭代器自己的remove方法以外的任何方式对映射进行结构修改，
 * 迭代器将抛出ConcurrentModificationException。
 * 因此，在面对并发修改时，迭代器会快速、干净地失败，而不是在将来的不确定时间冒着任意、不确定行为的风险。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * 请注意，无法保证迭代器的快速失效行为，因为一般来说，在存在非同步并发修改的情况下，不可能做出任何硬保证。
 * 快速失败迭代器以最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的正确性的程序是错误的：迭代器的快速失败行为应该只用于检测bug。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    /*
     * Implementation notes.
     * 实现说明。
     *
     * This map usually acts as a binned (bucketed) hash table, but
     * when bins get too large, they are transformed into bins of
     * TreeNodes, each structured similarly to those in
     * java.util.TreeMap. Most methods try to use normal bins, but
     * relay to TreeNode methods when applicable (simply by checking
     * instanceof a node).  Bins of TreeNodes may be traversed and
     * used like any others, but additionally support faster lookup
     * when overpopulated. However, since the vast majority of bins in
     * normal use are not overpopulated, checking for existence of
     * tree bins may be delayed in the course of table methods.
     * 此映射通常充当一个装箱（bucketed）的哈希表，但当bins（容器）太大时，
     * 它们会转换为树节点（TreeNodes）的bins（容器），每个容器的结构与TreeMap中的类似。
     * 大多数方法都尝试使用普通的容器，但在适用的情况下会转换到树节点方法（只需检查节点的实例）。
     * 树节点的容器可以像其他任何容器一样被遍历和使用，但在节点过多时，还支持更快的查找。
     * 然而，由于正常使用的绝大多数容器并不是节点过多，在使用表方法的过程中，检查是否存在树容器可能会延迟。
     *
     * Tree bins (i.e., bins whose elements are all TreeNodes) are
     * ordered primarily by hashCode, but in the case of ties, if two
     * elements are of the same "class C implements Comparable<C>",
     * type then their compareTo method is used for ordering. (We
     * conservatively check generic types via reflection to validate
     * this -- see method comparableClassFor).  The added complexity
     * of tree bins is worthwhile in providing worst-case O(log n)
     * operations when keys either have distinct hashes or are
     * orderable, Thus, performance degrades gracefully under
     * accidental or malicious usages in which hashCode() methods
     * return values that are poorly distributed, as well as those in
     * which many keys share a hashCode, so long as they are also
     * Comparable. (If neither of these apply, we may waste about a
     * factor of two in time and space compared to taking no
     * precautions. But the only known cases stem from poor user
     * programming practices that are already so slow that this makes
     * little difference.)
     * 树型容器（即，其元素均为树节点的箱）主要通过hashCode进行排序，
     * 但在ties的情况下，如果两个元素属于相同的Comparable，则使用它们的compareTo方法进行排序。
     * （我们通过反射保守地检查泛型类型以验证这一点——请参见方法comparableClassFor）。
     * 在提供最坏情况下的O（log n）操作时，当keys具有不同的散列或可排序时，树容器的额外复杂性是值得的，
     * 因此，在偶然或恶意使用hashCode()方法返回分布不均匀的值以及许多键共享一个hashCode的值的情况下，
     * 只要它们具有可比性，性能就会下降。（如果两者都不适用，与不采取预防措施相比，我们可能在时间和空间上浪费大约两倍。
     * 但已知的唯一案例来自糟糕的用户编程实践，这些实践已经非常缓慢，几乎没有什么区别。）
     *
     * Because TreeNodes are about twice the size of regular nodes, we
     * use them only when bins contain enough nodes to warrant use
     * (see TREEIFY_THRESHOLD). And when they become too small (due to
     * removal or resizing) they are converted back to plain bins.  In
     * usages with well-distributed user hashCodes, tree bins are
     * rarely used.  Ideally, under random hashCodes, the frequency of
     * nodes in bins follows a Poisson distribution
     * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
     * parameter of about 0.5 on average for the default resizing
     * threshold of 0.75, although with a large variance because of
     * resizing granularity. Ignoring variance, the expected
     * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
     * factorial(k)). The first values are:
     * 由于TreeNode的大小大约是常规节点的两倍，
     * 因此我们仅在容器包含足够的节点以保证使用时才使用它们（请参见TREEIFY_THRESHOLD）。
     * 当它们变得太小时（由于移除或调整大小），它们会被转换回普通容器。
     * 在使用分布良好的用户哈希代码时，很少使用树状容器。
     * 理想情况下，在随机哈希码下，容器中节点的重复率遵循泊松分布(http://en.wikipedia.org/wiki/Poisson_distribution)
     * 对于默认的大小调整阈值0.75，平均参数约为0.5，但由于大小调整粒度，差异较大。
     *
     * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     * more: less than 1 in ten million 
     * 不到一千万分之一
     *
     * The root of a tree bin is normally its first node.  However,
     * sometimes (currently only upon Iterator.remove), the root might
     * be elsewhere, but can be recovered following parent links
     * (method TreeNode.root()).
     * 树容器的根通常是它的第一个节点。
     * 但是，有时（当前仅在Iterator.remove上），根可能位于其他位置，
     * 但可以通过父链接（方法TreeNode.root()）恢复
     *
     * All applicable internal methods accept a hash code as an
     * argument (as normally supplied from a public method), allowing
     * them to call each other without recomputing user hashCodes.
     * Most internal methods also accept a "tab" argument, that is
     * normally the current table, but may be a new or old one when
     * resizing or converting.
     * 所有适用的内部方法都接受哈希值作为参数（通常由公共方法提供），
     * 允许它们相互调用，而无需重新计算用户哈希值。
     * 大多数内部方法也接受“tab”参数，通常是当前表，但在调整大小或转换时可能是新的或旧的。
     *
     * When bin lists are treeified, split, or untreeified, we keep
     * them in the same relative access/traversal order (i.e., field
     * Node.next) to better preserve locality, and to slightly
     * simplify handling of splits and traversals that invoke
     * iterator.remove. When using comparators on insertion, to keep a
     * total ordering (or as close as is required here) across
     * rebalancings, we compare classes and identityHashCodes as
     * tie-breakers.
     * 当容器列表被树化、拆分或非树化时，我们将它们保持在相同的相对访问/遍历顺序（即field Node.next），
     * 以更好地保留局部性，并稍微简化对iterator.remove的拆分和遍历的处理。
     * 当在插入时使用比较器时，为了在重新平衡过程中保持总顺序（或尽可能接近要求），
     * 我们将类和identityHashCodes作为tie-breakers进行比较。
     *
     * The use and transitions among plain vs tree modes is
     * complicated by the existence of subclass LinkedHashMap. See
     * below for hook methods defined to be invoked upon insertion,
     * removal and access that allow LinkedHashMap internals to
     * otherwise remain independent of these mechanics. (This also
     * requires that a map instance be passed to some utility methods
     * that may create new nodes.)
     * 由于子类LinkedHashMap的存在，普通模式与树模式之间的使用和转换变得复杂。
     * 请参阅下文，了解定义为在插入、移除和访问时调用的钩子方法，
     * 这些钩子方法允许LinkedHashMap内部保持独立于这些机制。
     *
     * The concurrent-programming-like SSA-based coding style helps
     * avoid aliasing errors amid all of the twisty pointer operations.
     * 并行编程（如基于SSA的编码风格）有助于避免所有扭曲指针操作中的别名错误。
     */

    /**
     * The default initial capacity - MUST be a power of two.
     * 默认初始容量-必须是2的幂。
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     * 最大容量，在两个带参数的构造函数中的任何一个隐式指定了更高的值时使用。
     * 必须是2的幂并且小于等于1<<30。
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     * 构造函数中未指定时使用的负载因子。
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     * 使用树而不是列表的容器计数阈值。
     * 将元素添加到至少具有这么多节点的容器时，容器将转换为树。
     * 该值必须大于2，且应至少为8，以符合树移除中关于收缩后转换回普通容器的假设。
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     * 在调整大小操作期间 非树化（拆分）的容器计数阈值。应小于TREEIFY_THRESHOLD，
     * 最多6，移除时进行收缩检测
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     * 容器可树化的最小表容量。（否则，如果容器中的节点太多，则会调整表的大小。）
     * 应至少为4 * TREEIFY_THRESHOLD，以避免调整大小和树化阈值之间的冲突。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /* ---------------- Fields -------------- */

    /**
     * The table, initialized on first use, and resized as
     * necessary. When allocated, length is always a power of two.
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     * 该table，第一次使用时初始化，并根据需要调整大小。
     * 分配时，长度始终为2的幂。
     * （在某些操作中，我们还允许长度为零，以允许当前不需要的引导机制。）
     */
    transient Node<K,V>[] table;

    /**
     * Holds cached entrySet(). Note that AbstractMap fields are used
     * for keySet() and values().
     * 保存缓存的entrySet()。注意：AbstractMap字段用于keySet() and values().
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * The number of key-value mappings contained in this map.
     * 该map中包含的键值对数量。
     */
    transient int size;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     * 此HashMap在结构上被修改的次数，结构修改是指更改HashMap中映射的数量或以其他方式修改其内部结构（例如，重新设置）。
     * 此字段用于使哈希表集合视图上的迭代器快速失效。
     */
    transient int modCount;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 要调整大小的下一个大小值 (capacity * load factor)。
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     * 该哈希表的负载因子
     * @serial
     */
    final float loadFactor;
    

    /**
     * Basic hash bin node, used for most entries.  (See below for
     * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
     * hash的基容器节点。一个链表结构。
     * 就是键值对的封装类。
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     * 计算key.hashCode()并将hash值的高位（异或）扩展到低位。
     * 由于该表使用两个掩码的幂，因此仅在当前掩码上方的位上变化的哈希集将始终冲突。
     * （已知示例中有一组Float键，它们在小表格中保持连续整数。）
     * 因此，我们应用了一种变换，将高位的影响向下传播。
     * 在比特传播的速度、效用和质量之间存在一种折衷。
     * 因为许多常见的散列集已经合理地分布了（所以不要从散列中获益），
     * 因为我们使用树来处理容器中的大量碰撞，
     * 我们只是以尽可能便宜的方式对一些移位的位进行异或运算，以减少系统损失，
     * 并结合最高位的影响，否则，由于表边界，最高位将永远不会用于索引计算。
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    // Callbacks to allow LinkedHashMap post-actions
    //允许LinkedHashMap后置处理的回调方法（这里知道有这三个回调方法就好，LinkedHashMap中再细看）
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }
}
```
总结：  
1. HashMap是Hashtable的变体，基本的逻辑都一样，table数组、负载因子、扩容。
2. HashMap不是线程安全的，但是效率就高了。
3. HashMap允许key和value为空。
4. HashMap每个数组内部的容器，其数据结构不一定时列表了，有可能会转换成树。
5. 提供了三个钩子方法，以供LinkedHashMap使用

##二： put、resize逻辑  
```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     * 将指定的值与此map中的指定键相关联。如果map以前包含键的映射，则替换旧值。
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     *         与key关联的上一个value，如果键没有映射，则为null。
     *         （返回null还可以指示key之前关联的value是null，有这个key，但是value是null）
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     * 计算key.hashCode() 并将散列的高位（异或）扩展到低位。？？？
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Implements Map.put and related methods
     * 实现Map.put和相关方法
     * 
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     *                     如果true，不更改已有的值。
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            //第一次put时就走这里，给tab进行初始化。
            // resize就是扩容，但是第一次put也通过resize给个初始tab，后面再看。
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            //非第一次put了，但是根据key的哈希计算出来数组的位置还是空，那么直接new一个Node放进去。
            tab[i] = newNode(hash, key, value, null);
        else {
            //非第一次put了，并且key所在的数组位置已经有Node(p)了。
            Node<K,V> e; K k; //临时节点e，用于保存已存在和新加入的key相等的老节点。
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                //如果老Node p的key和新加入的键值对的key相等，则将p赋值给临时Node e以做记录备份。
                e = p;
            else if (p instanceof TreeNode)
                // 老Node p的key和新加入的键值对的key不相等，而且Node p是TreeNode，
                // 则将新的键值对加入的树的结构里（树的操作先不细看）。
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // 老Node p的key和新加入的键值对的key不相等，而且Node p不是TreeNode(就是普通节点，链表结构)，
                // 则将新的键值对加入到链表的结构里。
                // （虽然和Node p的key不相等，还要循环整个链表查看是否和链表中其它Node的key是否相等）
                for (int binCount = 0; ; ++binCount) {
                    //通过next属性一直循环链表
                    if ((e = p.next) == null) {
                        //p.next==null表示已经到链表的尾部节点了，将新的键值对作为普通节点添加到链表里。
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            //加过后发现大于树化的阈值，则将链表的容器bin转化成树的bin。
                            //binCount是之后才加1统计的，所以这时候要binCount+1>=TREEIFY_THRESHOLD。
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        //如果循环的中间发现链表中的某个Node的key和新加入的键值对的key相等，则跳出循环。
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                // e 不等于null表示该key已经存在。
                // 根据onlyIfAbsent参数和老的value值是否为空来判断是否进行替换。
                // 这里就能看出即使onlyIfAbsent为true，但是只要oldValue为null，照样替换。
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                //调用钩子方法，这里调用的是Access表示访问，而不是afterNodeInsertion表示插入哦（LinkedHashMap中会使用）
                afterNodeAccess(e);
                return oldValue;
            }
        }
        
        //走到这里说明新的key在map中不存在，已经封装成Node加入到map中了。
        ++modCount;
        if (++size > threshold) //如果当前键值对数量大于阈值，进行扩容。
            resize();
        afterNodeInsertion(evict);//调用钩子方法，通知新节点插入（LinkedHashMap中会使用）
        return null;
    }

    // Create a regular (non-tree) node
    // 创建一个常规的（非树的）节点。
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }
    
    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     * 将table大小初始化或加倍。如果为空，则根据字段阈值中保留的初始容量目标进行分配。
     * 否则，因为我们使用的是二次幂展开，所以每个容器中的元素必须保持在相同的索引中，
     * 或者在新表中以二次幂偏移量移动。
     *
     * @return the table
     */
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length; //老的容量，tab数组长度
        int oldThr = threshold; //老的阈值 （capacity * load factor）
        int newCap, newThr = 0;
        
        //计算新的容量和新的阈值
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                //老的容量已经大于最大限制（1 << 30），不扩容了，将tab原样返回。
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                //新容量=老容量<<1（即乘以2）
                //新容量 < 最大限制（1 << 30） && 老容量>=默认初始容量 1 << 4（16）
                //新阈值=老阈值<<1
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            //老容量没有，但是阈值有数据
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            // 第一次put时初始化
            newCap = DEFAULT_INITIAL_CAPACITY;//默认初始容量 1 << 4(16)
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);//默认阈值 （0.75 * 16）
        }
        if (newThr == 0) {
            //新的阈值计算一圈后发现是0，则重新计算
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        
        
        //将map中的所有Node重新在扩容后的tab中放置。
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            //循环oldTab数组
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    //如果数组该位置有节点，则将数组相应索引位置的Node保存与临时变量e，并将素组该位置置空。
                    oldTab[j] = null;
                    
                    if (e.next == null)
                        //该数组位置的节点只有一个，则直接放到新的tab数组对应位置。
                        newTab[e.hash & (newCap - 1)] = e;
                    
                    else if (e instanceof TreeNode)
                        //该数组位置的节点是TreeNode，则采用树节点的拆分逻辑（树的逻辑先不细看）。
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    
                    else { // preserve order 维持顺序
                        //维持顺序啥意思呢，比如说现在tab是这样的 tab[x]={1,2,3,4,5,6}，
                        // 则扩容后是tab[x]={1,3,5} tab[x+oldCap]={2,4,6},维持部分顺序不变。
                        
                        //这里就表示数组该位置的节点是普通节点，并且不止一个，是一个链表结构的。
                        //lo为低位的 hi为高位的，因为直接2倍扩容，原始的容量称为lo，新扩容的称为hi。
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        //循环该链表内部所有节点
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                // 这里&的是oldCap，不是oldCap-1，表示放到低位
                                if (loTail == null)
                                    //第一次添加，记录下head
                                    loHead = e;
                                else
                                    //依次往尾部添加节点组成新的链表
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                // 这些放到高位（新扩容出来的位置）
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        
                        //将从新组好的lo和hi放到新的newTab数组中。
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
}
```
总结：  
1. 这里不考虑树结构的操作（TreeMap再细看）。  
2. put的逻辑和HashTable的差不多，Hashtable将新节点添加到了链表的头，HashMap则是添加到了尾。
3. resize逻辑和HashTable也差不多，有趣的是维持顺序的逻辑，不像Hashtable那么直接暴力。


##三： get、remove逻辑
```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * 返回指定键映射到的值，如果此map不包含该键的映射，则返回null。
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * 更正规地说，如果这个map包含一个从键k到值v的映射，使得key==null ? k==null : key.equals(k)，
     * 那么这个方法返回v；否则返回null。（最多可以有一个这样的映射。）
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     * 返回null不一定表示map不包含键的映射；也可能是map将密key射到了null（和Hashtable就不一样了）。
     * containsKey操作可用于区分这两种情况
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods
     * 实现Map.get和相关方法
     * 
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & hash]) != null) {
            //first为该索引位置（链表或树）的第一个节点。
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))
                //如果第一个节点就是该key，则直接返回
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    //如果是树结构，通过树的方法查找该key
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        //循环链表找到该key即可
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }


    /**
     * Removes the mapping for the specified key from this map if present.
     * 从此map中删除指定键的映射（如果存在）。
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
                null : e.value;
    }

    /**
     * Implements Map.remove and related methods
     * 实现Map.remove和相关方法
     * 
     * @param hash hash for key
     * @param key the key
     * @param value the value to match if matchValue, else ignored
     *              如果matchValue为true，则value要匹配，否则忽略
     * @param matchValue if true only remove if value is equal
     *                   如果为true，则只有value相等的时候才删除
     * @param movable if false do not move other nodes while removing
     *                如果为false，则在删除时不要移动其他节点（树形结构使用）
     * @return the node, or null if none
     */
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            //p为该索引位置（链表或树）的第一个节点。
            Node<K,V> node = null, e; K k; V v;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                //第一个节点就是该key
                node = p;
            else if ((e = p.next) != null) {
                //第一个节点不是该key，并且该索引位置不止一个节点
                if (p instanceof TreeNode)
                    //如果为树结构，则采用树的方法找到该方法
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                else {
                    //链表结构，循环找到该key节点，这是p表示该key节点的上一个节点
                    do {
                        if (e.hash == hash &&
                                ((k = e.key) == key ||
                                        (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            if (node != null && (!matchValue || (v = node.value) == value ||
                    (value != null && value.equals(v)))) {
                //要到了要删除key的node，并且matchValue也匹配，则删除node
                if (node instanceof TreeNode)
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                ++modCount;//修改次数增加
                --size;//条目数量减少
                afterNodeRemoval(node);//调用钩子方法，通知节点删除了（LinkedHashMap中会使用）
                return node;
            }
        }
        return null;
    }
}
```
总结：
1. 这里不考虑树结构的操作（TreeMap再细看）。
2. get和remove都是先根据key定位到数组的索引位置，然后再操作链表或树进行查询或删除