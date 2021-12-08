```java
/**
 * A hash table supporting full concurrency of retrievals and
 * high expected concurrency for updates. This class obeys the
 * same functional specification as {@link java.util.Hashtable}, and
 * includes versions of methods corresponding to each method of
 * {@code Hashtable}. However, even though all operations are
 * thread-safe, retrieval operations do <em>not</em> entail locking,
 * and there is <em>not</em> any support for locking the entire table
 * in a way that prevents all access.  This class is fully
 * interoperable with {@code Hashtable} in programs that rely on its
 * thread safety but not on its synchronization details.
 * 一个哈希表，支持检索的完全并发性和更新的高预期并发性。
 * 此类遵循与Hashtable相同的功能规范，并包含与Hashtable的每个方法对应的方法版本。
 * 但是，即使所有操作都是线程安全的，检索操作也不需要锁定，
 * 并且不支持以阻止所有访问的方式锁定整个表。
 * 该类在依赖其线程安全性而不依赖其同步细节的程序中可与Hashtable完全互操作。
 *
 * <p>Retrieval operations (including {@code get}) generally do not
 * block, so may overlap with update operations (including {@code put}
 * and {@code remove}). Retrievals reflect the results of the most
 * recently <em>completed</em> update operations holding upon their
 * onset. (More formally, an update operation for a given key bears a
 * <em>happens-before</em> relation with any (non-null) retrieval for
 * that key reporting the updated value.)  For aggregate operations
 * such as {@code putAll} and {@code clear}, concurrent retrievals may
 * reflect insertion or removal of only some entries.  Similarly,
 * Iterators, Spliterators and Enumerations return elements reflecting the
 * state of the hash table at some point at or since the creation of the
 * iterator/enumeration.  They do <em>not</em> throw {@link
 * java.util.ConcurrentModificationException ConcurrentModificationException}.
 * However, iterators are designed to be used by only one thread at a time.
 * Bear in mind that the results of aggregate status methods including
 * {@code size}, {@code isEmpty}, and {@code containsValue} are typically
 * useful only when a map is not undergoing concurrent updates in other threads.
 * Otherwise the results of these methods reflect transient states
 * that may be adequate for monitoring or estimation purposes, but not
 * for program control.
 * 检索操作（包括get）通常不会阻塞，因此可能与更新操作（包括put和remove）重叠。
 * 检索反映了最近完成的更新操作在开始时的结果。
 * （更正式地说，给定键的更新操作 与 报告更新值的该键的任何（非null）检索具有happens-before关系。）
 * 对于putAll和clear等聚合操作，并发检索可能只反映插入或删除某些条目。
 * 类似地，Iterators, Spliterators 和 Enumerations返回元素，
 * 这些元素反映了在iterator/enumeration创建时或创建之后某个点的哈希表状态。
 * 它们不会抛出ConcurrentModificationException。
 * 但是，迭代器设计为一次只能由一个线程使用。
 * 请记住，聚合状态方法（包括size、isEmpty和containsValue）的结果
 * 通常只有在其他线程中没有对map进行并发更新时才有用。
 * 除此之外，这些方法的结果反映的瞬态可能足以用于监测或估计目的，但不适用于程序控制。
 *
 * <p>The table is dynamically expanded when there are too many
 * collisions (i.e., keys that have distinct hash codes but fall into
 * the same slot modulo the table size), with the expected average
 * effect of maintaining roughly two bins per mapping (corresponding
 * to a 0.75 load factor threshold for resizing). There may be much
 * variance around this average as mappings are added and removed, but
 * overall, this maintains a commonly accepted time/space tradeoff for
 * hash tables.  However, resizing this or any other kind of hash
 * table may be a relatively slow operation. When possible, it is a
 * good idea to provide a size estimate as an optional {@code
 * initialCapacity} constructor argument. An additional optional
 * {@code loadFactor} constructor argument provides a further means of
 * customizing initial table capacity by specifying the table density
 * to be used in calculating the amount of space to allocate for the
 * given number of elements.  Also, for compatibility with previous
 * versions of this class, constructors may optionally specify an
 * expected {@code concurrencyLevel} as an additional hint for
 * internal sizing.  Note that using many keys with exactly the same
 * {@code hashCode()} is a sure way to slow down performance of any
 * hash table. To ameliorate impact, when keys are {@link Comparable},
 * this class may use comparison order among keys to help break ties.
 * 当冲突过多时（即，key具有不同哈希值但取模表大小后落在相同的插槽中），
 * 表将动态扩展，预期平均效果是每个映射保持大约两个bin（对应于调整大小的0.75负载因子阈值）。
 * 随着映射的添加和删除，这个平均值可能会有很大的差异，
 * 但总的来说，这为哈希表维护了一个普遍接受的时间/空间权衡。
 * 不管怎样，调整此哈希表或任何其他类型的哈希表的大小可能是一个相对缓慢的操作。
 * 如果可能，最好提供一个大小估计值作为可选的initialCapacity构造函数参数。
 * 另外一个可选的构造函数参数loadFactor提供了一种定制初始表容量的进一步方法，
 * 它指定了在计算为给定数量的元素分配的空间量时要使用的表密度。
 * 此外，为了与该类的早期版本兼容，构造函数可以选择指定预期的concurrencyLevel作为内部大小调整的附加提示。
 * 请注意，使用许多具有完全相同的hashCode()的键肯定会降低任何哈希表的性能。
 * 为了改善影响，当键为Comparable时，此类可以使用键之间的比较顺序来帮助打破联系。
 *
 * <p>A {@link Set} projection of a ConcurrentHashMap may be created
 * (using {@link #newKeySet()} or {@link #newKeySet(int)}), or viewed
 * (using {@link #keySet(Object)} when only keys are of interest, and the
 * mapped values are (perhaps transiently) not used or all take the
 * same mapping value.
 * 可以创建ConcurrentHashMap的一个集合投影（使用newKeySet()或者newKeySet(int)），
 *
 * <p>A ConcurrentHashMap can be used as scalable frequency map (a
 * form of histogram or multiset) by using {@link
 * java.util.concurrent.atomic.LongAdder} values and initializing via
 * {@link #computeIfAbsent computeIfAbsent}. For example, to add a count
 * to a {@code ConcurrentHashMap<String,LongAdder> freqs}, you can use
 * {@code freqs.computeIfAbsent(k -> new LongAdder()).increment();}
 * ConcurrentHashMap可以通过使用LongAdder值并通过computeIfAbsent初始化，
 * 用作可伸缩频率图（直方图或多集的一种形式）。
 * 例如，要将计数添加到ConcurrentHashMap<String，LongAdder> freqs，
 * 可以使用freqs.computeIfAbsent(k -> new LongAdder()).increment();
 *
 * <p>This class and its views and iterators implement all of the
 * <em>optional</em> methods of the {@link Map} and {@link Iterator}
 * interfaces.
 * 此类及其视图和迭代器实现了Map和Iterator接口的所有可选方法。
 *
 * <p>Like {@link Hashtable} but unlike {@link HashMap}, this class
 * does <em>not</em> allow {@code null} to be used as a key or value.
 * 与Hashtable类似，但与HashMap不同，该类不允许将null用作键或值。
 *
 * <p>ConcurrentHashMaps support a set of sequential and parallel bulk
 * operations that, unlike most {@link Stream} methods, are designed
 * to be safely, and often sensibly, applied even with maps that are
 * being concurrently updated by other threads; for example, when
 * computing a snapshot summary of the values in a shared registry.
 * There are three kinds of operation, each with four forms, accepting
 * functions with Keys, Values, Entries, and (Key, Value) arguments
 * and/or return values. Because the elements of a ConcurrentHashMap
 * are not ordered in any particular way, and may be processed in
 * different orders in different parallel executions, the correctness
 * of supplied functions should not depend on any ordering, or on any
 * other objects or values that may transiently change while
 * computation is in progress; and except for forEach actions, should
 * ideally be side-effect-free. Bulk operations on {@link java.util.Map.Entry}
 * objects do not support method {@code setValue}.
 * ConcurrentHashMaps支持一组顺序和并行批量操作，与大多数Stream方法不同，
 * 这些操作被设计为安全 且通常合理 地应用于其他线程同时更新的map；
 * 例如，在计算共享注册表中的值的快照摘要时。
 * 有三种操作，每种操作有四种形式，接受带有键、值、条目和（键、值）参数和/或返回值的函数。
 * 因为ConcurrentHashMap的元素没有以任何特定的方式排序，
 * 并且可以在不同的并行执行中以不同的顺序进行处理，所提供函数的正确性不应取决于任何顺序，
 * 或在计算过程中可能暂时改变的任何其他对象或值；除forEach作用外，理想情况下应无副作用。
 * java.util.Map.Entry对象上的批量操作不支持方法setValue。
 *
 * <ul>
 * <li> forEach: Perform a given action on each element.
 * A variant form applies a given transformation on each element
 * before performing the action.</li>
 * forEach:对每个元素执行给定的操作。变量形式在执行操作之前对每个元素应用给定的转换。
 *
 * <li> search: Return the first available non-null result of
 * applying a given function on each element; skipping further
 * search when a result is found.</li>
 * search:返回对每个元素应用给定函数的第一个可用非空结果；找到结果时跳过进一步搜索。
 *
 * <li> reduce: Accumulate each element.  The supplied reduction
 * function cannot rely on ordering (more formally, it should be
 * both associative and commutative).  There are five variants:
 *
 * <ul>
 *
 * <li> Plain reductions. (There is not a form of this method for
 * (key, value) function arguments since there is no corresponding
 * return type.)</li>
 *
 * <li> Mapped reductions that accumulate the results of a given
 * function applied to each element.</li>
 *
 * <li> Reductions to scalar doubles, longs, and ints, using a
 * given basis value.</li>
 *
 * </ul>
 * </li>
 * </ul>
 *
 * <p>These bulk operations accept a {@code parallelismThreshold}
 * argument. Methods proceed sequentially if the current map size is
 * estimated to be less than the given threshold. Using a value of
 * {@code Long.MAX_VALUE} suppresses all parallelism.  Using a value
 * of {@code 1} results in maximal parallelism by partitioning into
 * enough subtasks to fully utilize the {@link
 * ForkJoinPool#commonPool()} that is used for all parallel
 * computations. Normally, you would initially choose one of these
 * extreme values, and then measure performance of using in-between
 * values that trade off overhead versus throughput.
 *
 * <p>The concurrency properties of bulk operations follow
 * from those of ConcurrentHashMap: Any non-null result returned
 * from {@code get(key)} and related access methods bears a
 * happens-before relation with the associated insertion or
 * update.  The result of any bulk operation reflects the
 * composition of these per-element relations (but is not
 * necessarily atomic with respect to the map as a whole unless it
 * is somehow known to be quiescent).  Conversely, because keys
 * and values in the map are never null, null serves as a reliable
 * atomic indicator of the current lack of any result.  To
 * maintain this property, null serves as an implicit basis for
 * all non-scalar reduction operations. For the double, long, and
 * int versions, the basis should be one that, when combined with
 * any other value, returns that other value (more formally, it
 * should be the identity element for the reduction). Most common
 * reductions have these properties; for example, computing a sum
 * with basis 0 or a minimum with basis MAX_VALUE.
 *
 * <p>Search and transformation functions provided as arguments
 * should similarly return null to indicate the lack of any result
 * (in which case it is not used). In the case of mapped
 * reductions, this also enables transformations to serve as
 * filters, returning null (or, in the case of primitive
 * specializations, the identity basis) if the element should not
 * be combined. You can create compound transformations and
 * filterings by composing them yourself under this "null means
 * there is nothing there now" rule before using them in search or
 * reduce operations.
 *
 * <p>Methods accepting and/or returning Entry arguments maintain
 * key-value associations. They may be useful for example when
 * finding the key for the greatest value. Note that "plain" Entry
 * arguments can be supplied using {@code new
 * AbstractMap.SimpleEntry(k,v)}.
 *
 * <p>Bulk operations may complete abruptly, throwing an
 * exception encountered in the application of a supplied
 * function. Bear in mind when handling such exceptions that other
 * concurrently executing functions could also have thrown
 * exceptions, or would have done so if the first exception had
 * not occurred.
 *
 * <p>Speedups for parallel compared to sequential forms are common
 * but not guaranteed.  Parallel operations involving brief functions
 * on small maps may execute more slowly than sequential forms if the
 * underlying work to parallelize the computation is more expensive
 * than the computation itself.  Similarly, parallelization may not
 * lead to much actual parallelism if all processors are busy
 * performing unrelated tasks.
 *
 * <p>All arguments to all task methods must be non-null.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
```

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {

    /**
     * Table initialization and resizing control.  When negative, the
     * table is being initialized or resized: -1 for initialization,
     * else -(1 + the number of active resizing threads).  Otherwise,
     * when table is null, holds the initial table size to use upon
     * creation, or 0 for default. After initialization, holds the
     * next element count value upon which to resize the table.
     * 表初始化和调整大小控制。如果为负值，则正在初始化或调整表的大小：
     * -1用于初始化，否则 -(1+活动调整大小的线程的数量)。
     * 否则，当table为null时，将保留创建时使用的初始表大小，默认值为0。
     * 初始化后，保存下一个要调整表大小的元素计数值。
     */
    private transient volatile int sizeCtl;
    
    /**
     * Maps the specified key to the specified value in this table.
     * Neither the key nor the value can be null.
     *
     * <p>The value can be retrieved by calling the {@code get} method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key or value is null
     */
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                        new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                            value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                    value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }

    /**
     * Initializes table, using the size recorded in sizeCtl.
     */
    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
}
```