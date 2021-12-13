#Set的实现类 HashSet、LinkedHashSet、TreeSet
Set的实现类都是基于Map来实现的。熟悉了Map之后再看Set就简单多了。  

##一： HashSet
```java
/**
 * This class implements the <tt>Set</tt> interface, backed by a hash table
 * (actually a <tt>HashMap</tt> instance).  It makes no guarantees as to the
 * iteration order of the set; in particular, it does not guarantee that the
 * order will remain constant over time.  This class permits the <tt>null</tt>
 * element.
 * 这个类实现Set接口，由一个哈希表（实际上是一个HashMap实例）支持。
 * 它不保证集合的迭代顺序；特别是，它不能保证顺序在一段时间内保持不变。此类允许空元素。
 *
 * <p>This class offers constant time performance for the basic operations
 * (<tt>add</tt>, <tt>remove</tt>, <tt>contains</tt> and <tt>size</tt>),
 * assuming the hash function disperses the elements properly among the
 * buckets.  Iterating over this set requires time proportional to the sum of
 * the <tt>HashSet</tt> instance's size (the number of elements) plus the
 * "capacity" of the backing <tt>HashMap</tt> instance (the number of
 * buckets).  Thus, it's very important not to set the initial capacity too
 * high (or the load factor too low) if iteration performance is important.
 * 此类为基本操作（add、remove、contains和size）提供恒定时间性能，假设散列函数将元素正确地分散在存储桶中。
 * 迭代这个集合需要的时间与HashSet实例的大小（元素数）加上支持HashMap实例的“容量”（bucket数）之和成比例。
 * 因此，如果迭代性能很重要，那么不要将初始容量设置得太高（或负载因子太低），这一点非常重要。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash set concurrently, and at least one of
 * the threads modifies the set, it <i>must</i> be synchronized externally.
 * This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the set.
 * 请注意，此实现是不同步的。
 * 如果多个线程同时访问哈希集，并且至少有一个线程修改该集，则必须在外部对其进行同步。
 * 这通常通过在自然封装集合的某个对象上进行同步来实现。
 *
 * If no such object exists, the set should be "wrapped" using the
 * {@link Collections#synchronizedSet Collections.synchronizedSet}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the set:<pre>
 * 如果不存在这样的对象，则应使用Collections.synchronizedSet方法“包装”集合。
 * 最好在创建时执行此操作，以防止意外不同步地访问集合：
 *   Set s = Collections.synchronizedSet(new HashSet(...));</pre>
 *
 * <p>The iterators returned by this class's <tt>iterator</tt> method are
 * <i>fail-fast</i>: if the set is modified at any time after the iterator is
 * created, in any way except through the iterator's own <tt>remove</tt>
 * method, the Iterator throws a {@link ConcurrentModificationException}.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.
 * 此类的iterator方法返回的迭代器是快速失败的：如果在创建迭代器后的任何时候，
 * 以迭代器自己的remove方法以外的任何方式修改set，迭代器将抛出ConcurrentModificationException。
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
 * 快速失败迭代器会尽最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的正确性的程序是错误的：迭代器的快速失败行为应该只用于检测bug。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Collection
 * @see     Set
 * @see     TreeSet
 * @see     HashMap
 * @since   1.2
 */

public class HashSet<E>
    extends AbstractSet<E>
    implements Set<E>, Cloneable, java.io.Serializable
{
    //内部保存这以Set的元素类型E未key的HashMap
    private transient HashMap<E,Object> map;

    // Dummy value to associate with an Object in the backing Map
    // 与协助Map中的对象关联的虚拟值
    private static final Object PRESENT = new Object();

    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * default initial capacity (16) and load factor (0.75).
     * 构造一个新的空集合；协助的HashMap实例具有默认的初始容量（16）和负载因子（0.75）。
     */
    public HashSet() {
        map = new HashMap<>();
    }

    /**
     * Constructs a new, empty linked hash set.  (This package private
     * constructor is only used by LinkedHashSet.) The backing
     * HashMap instance is a LinkedHashMap with the specified initial
     * capacity and the specified load factor.
     * 构造一个新的空的LinkedHashSet。（此包专用构造函数仅由LinkedHashSet使用。）
     * 协作的HashMap实例是具有指定初始容量和指定负载因子的LinkedHashMap。
     *
     * @param      initialCapacity   the initial capacity of the hash map
     * @param      loadFactor        the load factor of the hash map
     * @param      dummy             ignored (distinguishes this
     *             constructor from other int, float constructor.)
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero, or if the load factor is nonpositive
     */
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

   public boolean add(E e) {
      return map.put(e, PRESENT)==null;
   }

   public boolean remove(Object o) {
      return map.remove(o)==PRESENT;
   }

   public void clear() {
      map.clear();
   }

   public boolean contains(Object o) {
      return map.containsKey(o);
   }

   public int size() {
      return map.size();
   }

   public Iterator<E> iterator() {
      return map.keySet().iterator();
   }
}
```
总结：  
1. HashSet是使用HashMap来协助实现的，将Set的集合作为了HashMap的key，PRESENT作为值。
2. 因为是基于HashMap来实现的，所以具备HashMap的特点：
   2.1 不保证迭代顺序
   2.2 允许空元素
   2.3 基本操作（add、remove、contains和size）提供恒定时间性能
   2.4 此实现是不同步的
   2.5 iterator方法返回的迭代器是快速失败的
3. 此类也提供了一个default权限的构造方法，会创建一个LinkedHashMap，提供给LinkedHashSet使用。



##二： LinkedHashSet
```java
/**
 * <p>Hash table and linked list implementation of the <tt>Set</tt> interface,
 * with predictable iteration order.  This implementation differs from
 * <tt>HashSet</tt> in that it maintains a doubly-linked list running through
 * all of its entries.  This linked list defines the iteration ordering,
 * which is the order in which elements were inserted into the set
 * (<i>insertion-order</i>).  Note that insertion order is <i>not</i> affected
 * if an element is <i>re-inserted</i> into the set.  (An element <tt>e</tt>
 * is reinserted into a set <tt>s</tt> if <tt>s.add(e)</tt> is invoked when
 * <tt>s.contains(e)</tt> would return <tt>true</tt> immediately prior to
 * the invocation.)
 * 哈希表和链表的集合接口实现，具有可预测的迭代顺序。
 * 此实现与HashSet的不同之处在于，它维护一个贯穿其所有条目的双链接列表（使用的就是LinkedHashMap，一样的特性）。
 * 此链表定义了迭代顺序，即元素插入到集合中的顺序（插入顺序）。
 * 请注意，如果将图元重新插入到集合中，则插入顺序不受影响。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author  Josh Bloch
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Set
 * @see     HashSet
 * @see     TreeSet
 * @see     Hashtable
 * @since   1.4
 */

public class LinkedHashSet<E>
    extends HashSet<E>
    implements Set<E>, Cloneable, java.io.Serializable {
    
    /**
     * Constructs a new, empty linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     */
    public LinkedHashSet() {
        super(16, .75f, true);
    }
}
```
总结：  
1. LinkedHashSet最省事，继承于HashSet，所有方法都不用写了，连构造函数都在HashSet中写了一部分。  
2. LinkedHashSet使用的是LinkedHashSet来实现，所以保证了迭代顺序，但是只有插入顺序，不像LinkedHashMap还有访问顺序。

##三：TreeSet
```java
public class TreeSet<E> extends AbstractSet<E>
    implements NavigableSet<E>, Cloneable, java.io.Serializable
{
   /**
    * The backing map. 协助的map
    */
   private transient NavigableMap<E,Object> m;

   // Dummy value to associate with an Object in the backing Map
   private static final Object PRESENT = new Object();

   /**
    * Constructs a set backed by the specified navigable map.
    */
   TreeSet(NavigableMap<E,Object> m) {
      this.m = m;
   }

   /**
    * Constructs a new, empty tree set, sorted according to the
    * natural ordering of its elements.  All elements inserted into
    * the set must implement the {@link Comparable} interface.
    * Furthermore, all such elements must be <i>mutually
    * comparable</i>: {@code e1.compareTo(e2)} must not throw a
    * {@code ClassCastException} for any elements {@code e1} and
    * {@code e2} in the set.  If the user attempts to add an element
    * to the set that violates this constraint (for example, the user
    * attempts to add a string element to a set whose elements are
    * integers), the {@code add} call will throw a
    * {@code ClassCastException}.
    * 构造一个新的空TreeSet，根据其元素的自然顺序进行排序。
    * 插入到集合中的所有元素必须实现Comparable接口。
    * 此外，所有这些元素必须相互可比：e1.compareTo(e2)不能为集合中的任何元素e1和e2抛出ClassCastException。
    * 如果用户尝试向集合添加违反此约束的元素（例如，用户尝试向元素为整数的set添加字符串元素），
    * 则add调用将抛出ClassCastException。
    */
   public TreeSet() {
      this(new TreeMap<E,Object>());
   }
   
   //使用比较器来初始化TreeMap
   public TreeSet(Comparator<? super E> comparator) {
      this(new TreeMap<>(comparator));
   }
}
```
总结：  
1. TreeSet使用的是TreeMap来实现，也就具有了TreeMap的特性。根据元素来排序，不是插入顺序哦
2. TreeSet也是实现了有序的Set接口（SortSet、NavigableSet），也有floor、ceiling这些方法。