# Hashtable 源码分析  

##一： 类注释及内部变量预览  
```java
/**
 * This class implements a hash table, which maps keys to values. Any
 * non-<code>null</code> object can be used as a key or as a value. <p>
 * 此类实现了一个哈希表，它将key映射到values。任何非空的对象都可以用来作为key或value。
 *
 * To successfully store and retrieve objects from a hashtable, the
 * objects used as keys must implement the <code>hashCode</code>
 * method and the <code>equals</code> method. <p>
 * 要成功地从哈希表存储和检索对象，用作键的对象必须实现hashCode方法和equals方法。
 *
 * An instance of <code>Hashtable</code> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of <i>buckets</i> in the hash table, and the
 * <i>initial capacity</i> is simply the capacity at the time the hash table
 * is created.  Note that the hash table is <i>open</i>: in the case of a "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  The <i>load factor</i> is a measure of how full the hash
 * table is allowed to get before its capacity is automatically increased.
 * The initial capacity and load factor parameters are merely hints to
 * the implementation.  The exact details as to when and whether the rehash
 * method is invoked are implementation-dependent.<p>
 * 一个Hashtable实例有两个参数会影响其性能： 初始容量 和 负载因子。
 * 容量是哈希表中的存储桶数，初始容量只是创建哈希表时的容量。
 * 请注意，哈希表是打开的：在“哈希冲突”的情况下，单个bucket存储多个条目，必须按顺序搜索。
 * 负载因子是在自动增加哈希表容量之前允许哈希表达到的满度的度量。
 * 初始容量和负载因子参数只是对实现的提示。
 * 关于何时以及是否调用rehash方法的确切细节取决于实现。
 * 
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * <tt>Hashtable</tt> operations, including <tt>get</tt> and <tt>put</tt>).<p>
 * 通常，默认负载因子（.75）提供了时间和空间成本之间的良好折衷。
 * 较高的值会减少空间开销，但会增加查找条目的时间成本（这反映在大多数Hashtable操作，包括get和put）。
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for <code>rehash</code> operations, which are time-consuming.
 * No <code>rehash</code> operations will <i>ever</i> occur if the initial
 * capacity is greater than the maximum number of entries the
 * <tt>Hashtable</tt> will contain divided by its load factor.  However,
 * setting the initial capacity too high can waste space.<p>
 * 初始容量控制浪费的空间和需要执行耗时的 rehash 操作之间的权衡。
 * 如果初始容量大于哈希表包含的最大条目数除以其负载因子，则不会发生rehash操作。
 * 但是，将初始容量设置得过高可能会浪费空间。
 *
 * If many entries are to be made into a <code>Hashtable</code>,
 * creating it with a sufficiently large capacity may allow the
 * entries to be inserted more efficiently than letting it perform
 * automatic rehashing as needed to grow the table. <p>
 * 如果要在哈希表中创建很多条目，则创建具有足够大容量的哈希表 可能会比 
 * 让哈希表根据需要执行rehash以增加表 能更有效地插入条目。
 *
 * This example creates a hashtable of numbers. It uses the names of
 * the numbers as keys:
 * 本例创建一个数字哈希表。它使用数字的名称作为键：
 * <pre>   {@code
 *   Hashtable<String, Integer> numbers
 *     = new Hashtable<String, Integer>();
 *   numbers.put("one", 1);
 *   numbers.put("two", 2);
 *   numbers.put("three", 3);}</pre>
 *
 * <p>To retrieve a number, use the following code:
 * 使用以下代码来检索一个数字：
 * <pre>   {@code
 *   Integer n = numbers.get("two");
 *   if (n != null) {
 *     System.out.println("two = " + n);
 *   }}</pre>
 *
 * <p>The iterators returned by the <tt>iterator</tt> method of the collections
 * returned by all of this class's "collection view methods" are
 * <em>fail-fast</em>: if the Hashtable is structurally modified at any time
 * after the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * The Enumerations returned by Hashtable's keys and elements methods are
 * <em>not</em> fail-fast.
 * 此类的所有“集合视图方法”返回的迭代器都是快速失败的：
 * 如果在创建迭代器后的任何时候，以迭代器自己的remove方法以外的任何方式对哈希表进行结构修改，
 * 迭代器将抛出ConcurrentModificationException。
 * 因此，在面对并发修改时，迭代器会快速、干净地失败，而不是在将来的不确定时间冒着任意、不确定行为的风险。
 * 哈希表的键和元素方法返回的枚举是非快速失败的。
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
 * <p>As of the Java 2 platform v1.2, this class was retrofitted to
 * implement the {@link Map} interface, making it a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * 从Java2平台v1.2开始，这个类被改装为实现Map接口
 *
 * Java Collections Framework</a>.  Unlike the new collection
 * implementations, {@code Hashtable} is synchronized.  If a
 * thread-safe implementation is not needed, it is recommended to use
 * {@link HashMap} in place of {@code Hashtable}.  If a thread-safe
 * highly-concurrent implementation is desired, then it is recommended
 * to use {@link java.util.concurrent.ConcurrentHashMap} in place of
 * {@code Hashtable}.
 * Java集合框架。与新的集合实现不同，Hashtable是同步的。
 * 如果不需要线程安全实现，建议使用HashMap代替Hashtable。
 * 如果需要线程安全的高并发实现，则建议使用ConcurrentHashMap代替Hashtable。
 *
 * @author  Arthur van Hoff
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Object#equals(java.lang.Object)
 * @see     Object#hashCode()
 * @see     Hashtable#rehash()
 * @see     Collection
 * @see     Map
 * @see     HashMap
 * @see     TreeMap
 * @since JDK1.0
 */
public class Hashtable<K,V>
    extends Dictionary<K,V>
    implements Map<K,V>, Cloneable, java.io.Serializable {
    
    /**
     * The hash table data.
     * 哈希表的数据
     */
    private transient Entry<?,?>[] table;

    /**
     * The total number of entries in the hash table.
     * 该哈希表内所有条目的总数
     */
    private transient int count;

    /**
     * The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).)
     * 当哈希表的大小达到该阈值的时候进行扩容。（该字段的值=（capacity * loadFactor））
     * 阈值就是说我的容量是100，负载因子0.75，那么当前哈希表里的条目数达到（100*0.75）时，我就要扩容了。
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hashtable.
     * 负载因子（哈希表容量的负载达到多少比例）
     * @serial
     */
    private float loadFactor;

    /**
     * The number of times this Hashtable has been structurally modified
     * Structural modifications are those that change the number of entries in
     * the Hashtable or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the Hashtable fail-fast.  (See ConcurrentModificationException).
     * 此哈希表被结构修改的次数，结构修改是指更改哈希表中条目数或以其他方式修改其内部结构（例如，rehash）的次数。
     * 此字段用于使哈希表集合视图上的迭代器快速失效。
     */
    private transient int modCount = 0;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     * 要分配的最大数组大小。有些虚拟机在数组中保留一些header。
     * 尝试分配较大的数组可能会导致OutOfMemoryError：请求的数组大小超过VM限制。
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Constructs a new, empty hashtable with the specified initial
     * capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hashtable.
     * @param      loadFactor        the load factor of the hashtable.
     * @exception  IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     */
    public Hashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);

        if (initialCapacity==0)
            initialCapacity = 1;
        this.loadFactor = loadFactor;
        table = new Entry<?,?>[initialCapacity];
        threshold = (int)Math.min(initialCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
    }
}
```
总结：
1. Hashtable是从1.0版本就有的一个Hash表实现。
2. 用于键值对的映射数据。
3. 两个主要的参数 初始容量 和 负载因子，一个消耗性能的方法rehash用于扩容。
4. 线程安全的。


##二： put 、rehash 逻辑      
```java
public class Hashtable<K,V>
        extends Dictionary<K,V>
        implements Map<K,V>, Cloneable, java.io.Serializable {

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     * 将指定的键值对放到哈希表里。key和value都不能为空。
     *
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     * 可以使用与原始键相等的键调用get方法来检索该值。
     *
     * @param      key     the hashtable key
     * @param      value   the value
     * @return     the previous value of the specified key in this hashtable,
     *             or <code>null</code> if it did not have one
     *             此哈希表中指定键的上一个值，如果没有，则为null
     *             
     * @exception  NullPointerException  if the key or value is
     *               <code>null</code>
     * @see     Object#equals(Object)
     * @see     #get(Object)
     */
    public synchronized V put(K key, V value) {
        // Make sure the value is not null
        // 确保value不能空
        if (value == null) {
            throw new NullPointerException();
        }

        // Makes sure the key is not already in the hashtable.
        // 确保该key不在hashtable中
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        //根据hash值取模数组长度，确认所在数组的位置索引
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        for(; entry != null ; entry = entry.next) {
            //循环数组tab中该位置的buckets，一个单向链表，一个一个往后循环找
            if ((entry.hash == hash) && entry.key.equals(key)) {
                //找到了该key，将新值赋值进去，老值返回。
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }

        //如果该key不存在，则添加进去
        addEntry(hash, key, value, index);
        return null;
    }

    //添加条目
    private void addEntry(int hash, K key, V value, int index) {
        modCount++;//记录修改次数

        Entry<?,?> tab[] = table;
        if (count >= threshold) {
            // Rehash the table if the threshold is exceeded
            // 如果当前容量超过了阈值，则进行重新哈希扩容。
            rehash();

            // 扩容后要tab长度改变，要重新计算index索引
            tab = table;
            hash = key.hashCode();
            index = (hash & 0x7FFFFFFF) % tab.length;
        }

        // Creates the new entry.
        // 创建一个新的条目，并且将老的条目作为新的条目的next，
        // 再将新的条目是添加到链表的头部位置。因为tab数组中保存的是头部节点，
        // 如果要是新的条目添加到尾部，就需要通过头部节点循环找到尾部节点再添加，
        // 直接将新节点添加到头部简单省时。
        Entry<K,V> e = (Entry<K,V>) tab[index];
        tab[index] = new Entry<>(hash, key, value, e);
        count++;//条目总数加1
    }

    /**
     * Increases the capacity of and internally reorganizes this
     * hashtable, in order to accommodate and access its entries more
     * efficiently.  This method is called automatically when the
     * number of keys in the hashtable exceeds this hashtable's capacity
     * and load factor.
     * 增加此哈希表的容量并对其进行内部重组，以便更有效地容纳和访问其条目。
     * 当哈希表中的键数超过此哈希表的容量和负载因子时，将自动调用此方法。
     */
    @SuppressWarnings("unchecked")
    protected void rehash() {
        int oldCapacity = table.length;
        Entry<?,?>[] oldMap = table;

        // overflow-conscious code
        // 有溢出意识的代码，就是说不能大于最大值
        // 新的容量时老容量*2+1，但是不能大于MAX_ARRAY_SIZE
        int newCapacity = (oldCapacity << 1) + 1;
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            if (oldCapacity == MAX_ARRAY_SIZE)
                // Keep running with MAX_ARRAY_SIZE buckets
                return;
            newCapacity = MAX_ARRAY_SIZE;
        }
        Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];

        //改变的次数+1
        modCount++;
        //使用新的容量计算出新的阈值
        threshold = (int)Math.min(newCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
        table = newMap;

        //将老的table中的每个条目重新计算数组位置，重新加入，所以说扩容时耗性能的。
        //先循环外部数组，再循环内部链表
        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
                Entry<K,V> e = old;
                old = old.next;

                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = (Entry<K,V>)newMap[index];
                newMap[index] = e;
            }
        }
    }
}
```
总结：  
1. put方法是synchronized的，所以线程安全，但效率低下。
2. put的key和value都不允许为空。
3. put时，如果key已存在，则更新value，并返回老的value。
4. put时，先根据key的hash值取模计算出所在的数组位置，然后见该键值对加到该位置的链表的头部。
5. put方法根据当前哈希表的容量和阈值，当前条目个数>=初始容量*负载因子时，将自动进行扩容。
6. 扩容直接将容量翻倍，并将已存在的key-value从新hash计算出新的数组位置，从新构建数组和链表，消耗性能。

##三： get、remove逻辑    
```java
public class Hashtable<K,V>
        extends Dictionary<K,V>
        implements Map<K,V>, Cloneable, java.io.Serializable {
    
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * 返回指定键映射到的值，如果此map不包含该键的映射，则返回null。
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key.equals(k))},
     * then this method returns {@code v}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     * 更正规地说，如果这个map包含一个从键k到值v的映射，使得key.equals(k)，
     * 那么这个方法返回v；否则返回null。（最多可以有一个这样的映射。）
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     * @see     #put(Object, Object)
     */
    @SuppressWarnings("unchecked")
    public synchronized V get(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        //先根据hash值计算出所在tab数组中的索引位置，然后依次循环该位置的链表。
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                return (V)e.value;
            }
        }
        return null;
    }


    /**
     * Removes the key (and its corresponding value) from this
     * hashtable. This method does nothing if the key is not in the hashtable.
     * 从此哈希表中删除键（及其对应的值）。
     * 如果键不在哈希表中，则此方法不执行任何操作。
     *
     * @param   key   the key that needs to be removed
     * @return  the value to which the key had been mapped in this hashtable,
     *          or <code>null</code> if the key did not have a mapping
     * @throws  NullPointerException  if the key is <code>null</code>
     */
    public synchronized V remove(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        
        //先根据hash值计算出所在tab数组中的索引位置，然后依次循环该位置的链表。
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for(Entry<K,V> prev = null ; e != null ; prev = e, e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                modCount++;
                if (prev != null) {
                    //表示该key不是链表的第一个节点。则将本节点的前置节点的next属性赋值于该节点的next（将本节点下链）
                    prev.next = e.next;
                } else {
                    //表示链表的第一个节点就时该key，则直接将额。next放到数组中即可。
                    tab[index] = e.next;
                }
                count--;//总数减1
                //返回老的value
                V oldValue = e.value;
                e.value = null;
                return oldValue;
            }
        }
        return null;
    }
}
```
总结：  
1. get、remove 都是synchronized的，所以线程安全，但效率低下。
2. 逻辑都很简单，先根据key定位到数组位置，再一次数组内部的链表处理即可。

总体来说Hashtable结构简单，数组+链表形式，put、get、remove都是线程安全的，效率比较低下。