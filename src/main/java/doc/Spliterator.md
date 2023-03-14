## Spliterator接口
```java
/**
 * An object for traversing and partitioning elements of a source.  The source
 * of elements covered by a Spliterator could be, for example, an array, a
 * {@link Collection}, an IO channel, or a generator function.
 * 
 * 一个用于遍历和划分愿元素的对象。Spliterator覆盖的元素源可以是，
 * 例如：数组、集合、IO通道、或者一个生成器函数。
 *
 * <p>A Spliterator may traverse elements individually ({@link
 * #tryAdvance tryAdvance()}) or sequentially in bulk
 * ({@link #forEachRemaining forEachRemaining()}).
 * 
 * Spliterator可以通过 tryAdvance()单独遍历元素，
 * 或者通过forEachRemainig()顺序批量遍历。
 *
 * <p>A Spliterator may also partition off some of its elements (using
 * {@link #trySplit}) as another Spliterator, to be used in
 * possibly-parallel operations.  Operations using a Spliterator that
 * cannot split, or does so in a highly imbalanced or inefficient
 * manner, are unlikely to benefit from parallelism.  Traversal
 * and splitting exhaust elements; each Spliterator is useful for only a single
 * bulk computation.

 * Spliterator 也可以将其某些元素（使用trySplit）划分为另一个 Spliterator，用于可能并行的操作。 
 * 使用不能拆分的 Spliterator 的操作，或者以高度不平衡或低效的方式进行拆分，不太可能从并行性中受益。 
 * 遍历和分割耗尽元素？？； 每个 Spliterator 仅对单个批量计算有用。
 *
 * <p>A Spliterator also reports a set of {@link #characteristics()} of its
 * structure, source, and elements from among {@link #ORDERED},
 * {@link #DISTINCT}, {@link #SORTED}, {@link #SIZED}, {@link #NONNULL},
 * {@link #IMMUTABLE}, {@link #CONCURRENT}, and {@link #SUBSIZED}. These may
 * be employed by Spliterator clients to control, specialize or simplify
 * computation.  For example, a Spliterator for a {@link Collection} would
 * report {@code SIZED}, a Spliterator for a {@link Set} would report
 * {@code DISTINCT}, and a Spliterator for a {@link SortedSet} would also
 * report {@code SORTED}.  Characteristics are reported as a simple unioned bit
 * set.
 * 
 * Spliterator 还可以通过characteristics()方法来报告其结构、来源和元素的特性。
 * 特性包括（ORDERED（有序）、DISTINCT（不重复）、SORTED（有序）、SIZED（有限大小）、NONNULL（非空）、
 * IMMUTABLE（不可变）、CONCURRENT（线程安全）、SUBSIZED（子Spliterator也是有限大小））
 * Spliterator客户端可以使用这些来控制、定制化或简化计算。
 * 例如：
 *      一个Collection的Spliterator会报告 SIZED
 *      一个Set的Spliterator会报告 DISTINCT
 *      一个SortedSet也会报告 SORTED
 * 特征被报告为一个联合位集。
 *
 * Some characteristics additionally constrain method behavior; for example if
 * {@code ORDERED}, traversal methods must conform to their documented ordering.
 * New characteristics may be defined in the future, so implementors should not
 * assign meanings to unlisted values.
 * 
 * 一些特征额外地限制了方法行为； 例如，如果 {@code ORDERED}，遍历方法必须符合它们记录的顺序。 
 * 将来可能会定义新的特征，因此实施者不应为未列出的值赋予含义。
 *
 * <p><a name="binding">A Spliterator that does not report {@code IMMUTABLE} or
 * {@code CONCURRENT} is expected to have a documented policy concerning:
 * when the spliterator <em>binds</em> to the element source; and detection of
 * structural interference of the element source detected after binding.</a>  A
 * <em>late-binding</em> Spliterator binds to the source of elements at the
 * point of first traversal, first split, or first query for estimated size,
 * rather than at the time the Spliterator is created.  A Spliterator that is
 * not <em>late-binding</em> binds to the source of elements at the point of
 * construction or first invocation of any method.  Modifications made to the
 * source prior to binding are reflected when the Spliterator is traversed.
 * After binding a Spliterator should, on a best-effort basis, throw
 * {@link ConcurrentModificationException} if structural interference is
 * detected.  Spliterators that do this are called <em>fail-fast</em>.  The
 * bulk traversal method ({@link #forEachRemaining forEachRemaining()}) of a
 * Spliterator may optimize traversal and check for structural interference
 * after all elements have been traversed, rather than checking per-element and
 * failing immediately.
 *
 * <p>Spliterators can provide an estimate of the number of remaining elements
 * via the {@link #estimateSize} method.  Ideally, as reflected in characteristic
 * {@link #SIZED}, this value corresponds exactly to the number of elements
 * that would be encountered in a successful traversal.  However, even when not
 * exactly known, an estimated value value may still be useful to operations
 * being performed on the source, such as helping to determine whether it is
 * preferable to split further or traverse the remaining elements sequentially.
 * 
 * Spliterators可以通过estimateSize方法估计剩余元素的数量。
 * 理想情况下，正如特性SIZED所反映的那样，该值正好对应于成功遍历中遇到的元素数量。
 * 然而，即使在不完全已知的情况下，估计值也可能对正在源上执行的操作有用，
 * 例如有助于确定是进一步拆分还是顺序遍历剩余元素。
 *
 * <p>Despite their obvious utility in parallel algorithms, spliterators are not
 * expected to be thread-safe; instead, implementations of parallel algorithms
 * using spliterators should ensure that the spliterator is only used by one
 * thread at a time.  This is generally easy to attain via <em>serial
 * thread-confinement</em>, which often is a natural consequence of typical
 * parallel algorithms that work by recursive decomposition.  A thread calling
 * {@link #trySplit()} may hand over the returned Spliterator to another thread,
 * which in turn may traverse or further split that Spliterator.  The behaviour
 * of splitting and traversal is undefined if two or more threads operate
 * concurrently on the same spliterator.  If the original thread hands a
 * spliterator off to another thread for processing, it is best if that handoff
 * occurs before any elements are consumed with {@link #tryAdvance(Consumer)
 * tryAdvance()}, as certain guarantees (such as the accuracy of
 * {@link #estimateSize()} for {@code SIZED} spliterators) are only valid before
 * traversal has begun.
 * 
 * 尽管Spliterator在并行算法中具有明显的实用性，但它不应该是线程安全的；
 * 相反，使用Spliterator的并行算法的实现应该确保Spliterator一次只能由一个线程使用。
 * 这通常很容易通过串行线程限制实现，这通常是通过递归分解工作的典型并行算法的自然结果。
 * 调用trySplit()的线程可能会将返回的Spliterator交给另一个线程，而该线程又可能遍历或进一步拆分该Spliterater。
 * 如果两个或多个线程在同一Spliterator上同时操作，则拆分和遍历的行为是未定义的。
 * 如果原始线程将Spliterator交给另一个线程进行处理，最好是在tryAdvance()使用任何元素之前进行切换，
 * 因为某些保证（例如SIZED Spliterator的estimateSize（）精度）仅在遍历开始之前有效。
 *
 * <p>Primitive subtype specializations of {@code Spliterator} are provided for
 * {@link OfInt int}, {@link OfLong long}, and {@link OfDouble double} values.
 * The subtype default implementations of
 * {@link Spliterator#tryAdvance(java.util.function.Consumer)}
 * and {@link Spliterator#forEachRemaining(java.util.function.Consumer)} box
 * primitive values to instances of their corresponding wrapper class.  Such
 * boxing may undermine any performance advantages gained by using the primitive
 * specializations.  To avoid boxing, the corresponding primitive-based methods
 * should be used.  For example,
 * {@link Spliterator.OfInt#tryAdvance(java.util.function.IntConsumer)}
 * and {@link Spliterator.OfInt#forEachRemaining(java.util.function.IntConsumer)}
 * should be used in preference to
 * {@link Spliterator.OfInt#tryAdvance(java.util.function.Consumer)} and
 * {@link Spliterator.OfInt#forEachRemaining(java.util.function.Consumer)}.
 * Traversal of primitive values using boxing-based methods
 * {@link #tryAdvance tryAdvance()} and
 * {@link #forEachRemaining(java.util.function.Consumer) forEachRemaining()}
 * does not affect the order in which the values, transformed to boxed values,
 * are encountered.
 * 
 * 为 int、long 和 double 值提供了 Spliterator 的原始子类型特化。 
 * tryAdvance(Consumer) 和 forEachRemaining(Consumer) 的子类型默认实现将原始值装箱为其相应包装类的实例。 
 * 这种装箱可能会破坏通过使用原始专业化获得的任何性能优势。 为避免装箱，应使用相应的基于原语的方法。 
 * 例如，应优先使用 Spliterator.OfInt.tryAdvance(IntConsumer) 和 Spliterator.OfInt.forEachRemaining(IntConsumer)，
 * 而不是 Spliterator.OfInt.tryAdvance(Consumer) 和 Spliterator.OfInt.forEachRemaining(Consumer)。 
 * 使用基于装箱的方法 tryAdvance() 和 forEachRemaining() 遍历原始值不会影响遇到转换为装箱值的值的顺序。
 *
 * @apiNote
 * <p>Spliterators, like {@code Iterator}s, are for traversing the elements of
 * a source.  The {@code Spliterator} API was designed to support efficient
 * parallel traversal in addition to sequential traversal, by supporting
 * decomposition as well as single-element iteration.  In addition, the
 * protocol for accessing elements via a Spliterator is designed to impose
 * smaller per-element overhead than {@code Iterator}, and to avoid the inherent
 * race involved in having separate methods for {@code hasNext()} and
 * {@code next()}.
 * 
 * Spliterators 和 Iterators 一样，用于遍历源的元素。 
 * Spliterator API 旨在通过支持分解和单元素迭代来支持高效的并行遍历和顺序遍历。 
 * 此外，通过 Spliterator 访问元素的协议旨在比 Iterator 施加更小的每个元素开销，
 * 并避免 hasNext() 和 next() 的单独方法所涉及的固有竞争。
 *
 * <p>For mutable sources, arbitrary and non-deterministic behavior may occur if
 * the source is structurally interfered with (elements added, replaced, or
 * removed) between the time that the Spliterator binds to its data source and
 * the end of traversal.  For example, such interference will produce arbitrary,
 * non-deterministic results when using the {@code java.util.stream} framework.
 * 
 * 对于可变源，如果在 Spliterator 绑定到其数据源和遍历结束之间源在结构上受到干扰（添加、替换或删除元素），
 * 则可能会发生任意和非确定性行为。 
 * 例如，在使用 java.util.stream 框架时，这种干扰会产生任意的、不确定的结果。
 *
 * <p>Structural interference of a source can be managed in the following ways
 * (in approximate order of decreasing desirability):
 * 源的结构性干扰可以通过以下方式进行管理（按可取性递减的大致顺序）：
 * 
 * <ul>
 * <li>The source cannot be structurally interfered with.
 * 来源不能在结构上受到干扰。 
 * 
 * <br>For example, an instance of
 * {@link java.util.concurrent.CopyOnWriteArrayList} is an immutable source.
 * A Spliterator created from the source reports a characteristic of
 * {@code IMMUTABLE}.</li>
 * 例如，java.util.concurrent.CopyOnWriteArrayList 的实例是不可变源。 
 * 从源创建的 Spliterator 报告了 IMMUTABLE 的特征。
 * 
 * <li>The source manages concurrent modifications.
 * 源管理并发修改。 
 * 
 * <br>For example, a key set of a {@link java.util.concurrent.ConcurrentHashMap}
 * is a concurrent source.  A Spliterator created from the source reports a
 * characteristic of {@code CONCURRENT}.</li>
 * 例如，java.util.concurrent.ConcurrentHashMap 的键集是一个并发源。 
 * 从源创建的 Spliterator 报告 CONCURRENT 的特征。
 * 
 * <li>The mutable source provides a late-binding and fail-fast Spliterator.
 * 可变源提供了一个后期绑定和快速失败的 Spliterator。 
 * 
 * <br>Late binding narrows the window during which interference can affect
 * the calculation; fail-fast detects, on a best-effort basis, that structural
 * interference has occurred after traversal has commenced and throws
 * {@link ConcurrentModificationException}.  For example, {@link ArrayList},
 * and many other non-concurrent {@code Collection} classes in the JDK, provide
 * a late-binding, fail-fast spliterator.</li>
 * 后期绑定缩小了干扰影响计算的窗口； 
 * fail-fast 在最大努力的基础上检测到在遍历开始后发生了结构干扰并抛出 ConcurrentModificationException。 
 * 例如，ArrayList 和 JDK 中的许多其他非并发 Collection 类提供了一个后期绑定、快速失败的拆分器。
 * 
 * <li>The mutable source provides a non-late-binding but fail-fast Spliterator.
 * 可变源提供了一个非后期绑定但快速失败的 Spliterator。 
 * 
 * <br>The source increases the likelihood of throwing
 * {@code ConcurrentModificationException} since the window of potential
 * interference is larger.</li>
 * 源增加了抛出 ConcurrentModificationException 的可能性，因为潜在干扰的窗口更大。
 * 
 * <li>The mutable source provides a late-binding and non-fail-fast Spliterator.
 * 可变源提供了一个后期绑定和非快速失败的 Spliterator。 
 * 
 * <br>The source risks arbitrary, non-deterministic behavior after traversal
 * has commenced since interference is not detected.
 * 由于未检测到干扰，在开始遍历后，源可能会出现任意的、不确定的行为。
 * 
 * </li>
 * <li>The mutable source provides a non-late-binding and non-fail-fast
 * Spliterator.
 * 可变源提供了一个非后期绑定和非快速失败的 Spliterator。 
 * 
 * <br>The source increases the risk of arbitrary, non-deterministic behavior
 * since non-detected interference may occur after construction.
 * 源增加了任意、不确定行为的风险，因为在构建之后可能会发生未检测到的干扰。
 * </li>
 * </ul>
 *
 *
 * @implNote
 * If the boolean system property {@code org.openjdk.java.util.stream.tripwire}
 * is set to {@code true} then diagnostic warnings are reported if boxing of
 * primitive values occur when operating on primitive subtype specializations.
 *
 * @param <T> the type of elements returned by this Spliterator
 *
 * @see Collection
 * @since 1.8
 */
public interface Spliterator<T> {
    /**
     * If a remaining element exists, performs the given action on it,
     * returning {@code true}; else returns {@code false}.  If this
     * Spliterator is {@link #ORDERED} the action is performed on the
     * next element in encounter order.  Exceptions thrown by the
     * action are relayed to the caller.
     * 
     * 如果存在剩余元素，则对其执行给定的操作，返回 true； 否则返回 false。 
     * 如果此 Spliterator 是 ORDERED，则按遇到顺序对下一个元素执行操作。 
     * 操作抛出的异常被转发给调用者。
     *
     * @param action The action
     * @return {@code false} if no remaining elements existed
     * upon entry to this method, else {@code true}.
     * @throws NullPointerException if the specified action is null
     */
    boolean tryAdvance(Consumer<? super T> action);

    /**
     * Performs the given action for each remaining element, sequentially in
     * the current thread, until all elements have been processed or the action
     * throws an exception.  If this Spliterator is {@link #ORDERED}, actions
     * are performed in encounter order.  Exceptions thrown by the action
     * are relayed to the caller.
     * 
     * 在当前线程中按顺序对每个剩余元素执行给定操作，直到处理完所有元素或操作引发异常。 
     * 如果此 Spliterator 是 ORDERED，则将按遇到顺序执行操作。 
     * 操作抛出的异常被转发给调用者。
     *
     * @implSpec
     * The default implementation repeatedly invokes {@link #tryAdvance} until
     * it returns {@code false}.  It should be overridden whenever possible.
     * 默认实现重复调用 tryAdvance() 直到它返回 false。 应尽可能覆盖它。
     *
     * @param action The action
     * @throws NullPointerException if the specified action is null
     */
    default void forEachRemaining(Consumer<? super T> action) {
        do { } while (tryAdvance(action));
    }

    /**
     * If this spliterator can be partitioned, returns a Spliterator
     * covering elements, that will, upon return from this method, not
     * be covered by this Spliterator.
     * 
     * 如果此 spliterator 可以分区，则返回一个 Spliterator 覆盖元素，
     * 从该方法返回时，该元素将不被此 Spliterator 覆盖。
     *
     * <p>If this Spliterator is {@link #ORDERED}, the returned Spliterator
     * must cover a strict prefix of the elements.
     * 
     * 如果此 Spliterator 是有序的，则返回的 Spliterator 必须覆盖元素的严格前缀。
     *
     * <p>Unless this Spliterator covers an infinite number of elements,
     * repeated calls to {@code trySplit()} must eventually return {@code null}.
     * Upon non-null return:
     * 
     * 除非此 Spliterator 涵盖无限数量的元素，否则重复调用 trySplit() 最终必须返回 null。 
     * 返回非空值时：
     * 
     * <ul>
     * <li>the value reported for {@code estimateSize()} before splitting,
     * must, after splitting, be greater than or equal to {@code estimateSize()}
     * for this and the returned Spliterator; and</li>
     * <li>if this Spliterator is {@code SUBSIZED}, then {@code estimateSize()}
     * for this spliterator before splitting must be equal to the sum of
     * {@code estimateSize()} for this and the returned Spliterator after
     * splitting.</li>
     * </ul>
     * 拆分前为 estimateSize() 报告的值必须在拆分后大于或等于此和返回的 Spliterator 的 estimateSize()； 
     * 并且如果此 Spliterator 是 SUBSIZED，则拆分前此拆分器的 estimateSize() 必须等于
     * 此拆分器的 estimateSize() 和拆分后返回的 Spliterator 的总和。
     *
     * <p>This method may return {@code null} for any reason,
     * including emptiness, inability to split after traversal has
     * commenced, data structure constraints, and efficiency
     * considerations.
     * 该方法可能因任何原因返回 null，包括空、遍历开始后无法拆分、数据结构约束和效率考虑。
     *
     * @apiNote
     * An ideal {@code trySplit} method efficiently (without
     * traversal) divides its elements exactly in half, allowing
     * balanced parallel computation.  Many departures from this ideal
     * remain highly effective; for example, only approximately
     * splitting an approximately balanced tree, or for a tree in
     * which leaf nodes may contain either one or two elements,
     * failing to further split these nodes.  However, large
     * deviations in balance and/or overly inefficient {@code
     * trySplit} mechanics typically result in poor parallel
     * performance.
     * 一个理想的 trySplit 方法有效地（无需遍历）将其元素精确地分成两半，从而允许平衡的并行计算。 
     * 许多背离这一理想的做法仍然非常有效； 例如，仅对一棵近似平衡的树进行近似分裂，
     * 或者对于叶子节点可能包含一个或两个元素的树，无法进一步分裂这些节点。 
     * 然而，平衡的大偏差和/或效率过低的 trySplit 机制通常会导致并行性能不佳。
     *
     * @return a {@code Spliterator} covering some portion of the
     * elements, or {@code null} if this spliterator cannot be split
     */
    Spliterator<T> trySplit();

    /**
     * Returns an estimate of the number of elements that would be
     * encountered by a {@link #forEachRemaining} traversal, or returns {@link
     * Long#MAX_VALUE} if infinite, unknown, or too expensive to compute.
     * 返回 forEachRemaining 遍历将遇到的元素数量的估计值，
     * 或者如果无限、未知或计算成本太高则返回 Long.MAX_VALUE。
     *
     * <p>If this Spliterator is {@link #SIZED} and has not yet been partially
     * traversed or split, or this Spliterator is {@link #SUBSIZED} and has
     * not yet been partially traversed, this estimate must be an accurate
     * count of elements that would be encountered by a complete traversal.
     * Otherwise, this estimate may be arbitrarily inaccurate, but must decrease
     * as specified across invocations of {@link #trySplit}.
     * 如果此 Spliterator 是 SIZED 并且尚未被部分遍历或拆分，
     * 或者此 Spliterator 是 SUBSIZED 并且尚未被部分遍历，
     * 则此估计必须是完整遍历将遇到的元素的准确计数。 
     * 否则，此估计可能任意不准确，但必须按照 trySplit 调用中指定的方式减少。
     *
     * @apiNote
     * Even an inexact estimate is often useful and inexpensive to compute.
     * For example, a sub-spliterator of an approximately balanced binary tree
     * may return a value that estimates the number of elements to be half of
     * that of its parent; if the root Spliterator does not maintain an
     * accurate count, it could estimate size to be the power of two
     * corresponding to its maximum depth.
     * 即使是不精确的估计通常也是有用的，而且计算成本低廉。 
     * 例如，近似平衡二叉树的子拆分器可能会返回一个值，该值估计元素的数量是其父元素的一半； 
     * 如果根 Spliterator 没有保持准确的计数，它可以将大小估计为其最大深度对应的 2 的幂。
     *
     * @return the estimated size, or {@code Long.MAX_VALUE} if infinite,
     *         unknown, or too expensive to compute.
     */
    long estimateSize();

    /**
     * Convenience method that returns {@link #estimateSize()} if this
     * Spliterator is {@link #SIZED}, else {@code -1}.
     * 如果此 Spliterator 为 SIZED，则返回 estimateSize()的便捷方法，否则为 -1。
     * 
     * @implSpec
     * The default implementation returns the result of {@code estimateSize()}
     * if the Spliterator reports a characteristic of {@code SIZED}, and
     * {@code -1} otherwise.
     * 如果 Spliterator 报告 SIZED 的特征，则默认实现返回 estimateSize() 的结果，否则返回 -1。
     *
     * @return the exact size, if known, else {@code -1}.
     */
    default long getExactSizeIfKnown() {
        return (characteristics() & SIZED) == 0 ? -1L : estimateSize();
    }

    /**
     * Returns a set of characteristics of this Spliterator and its
     * elements. The result is represented as ORed values from {@link
     * #ORDERED}, {@link #DISTINCT}, {@link #SORTED}, {@link #SIZED},
     * {@link #NONNULL}, {@link #IMMUTABLE}, {@link #CONCURRENT},
     * {@link #SUBSIZED}.  Repeated calls to {@code characteristics()} on
     * a given spliterator, prior to or in-between calls to {@code trySplit},
     * should always return the same result.
     * 返回此 Spliterator 及其元素的一组特征。 
     * 结果表示为来自 ORDERED、DISTINCT、SORTED、SIZED、NONNULL、
     * IMMUTABLE、CONCURRENT、SUBSIZED 的 ORed 值。 
     * 在调用 trySplit 之前或之间，在给定的拆分器上重复调用 characteristics() 应该始终返回相同的结果。
     *
     * <p>If a Spliterator reports an inconsistent set of
     * characteristics (either those returned from a single invocation
     * or across multiple invocations), no guarantees can be made
     * about any computation using this Spliterator.
     * 如果 Spliterator 报告一组不一致的特征（从单次调用返回的特征或跨多个调用返回的特征），
     * 则无法保证使用此 Spliterator 进行任何计算。
     *
     * @apiNote The characteristics of a given spliterator before splitting
     * may differ from the characteristics after splitting.  For specific
     * examples see the characteristic values {@link #SIZED}, {@link #SUBSIZED}
     * and {@link #CONCURRENT}.
     * 给定分裂器在分裂前的特征可能与分裂后的特征不同。 
     * 具体例子见特征值 SIZED、SUBSIZED 和 CONCURRENT。
     *
     * @return a representation of characteristics
     */
    int characteristics();

    /**
     * Returns {@code true} if this Spliterator's {@link
     * #characteristics} contain all of the given characteristics.
     * 如果此 Spliterator 的特征包含所有给定特征，则返回 true。
     *
     * @implSpec
     * The default implementation returns true if the corresponding bits
     * of the given characteristics are set.
     * 如果设置了给定特征的相应位，则默认实现返回 true。
     *
     * @param characteristics the characteristics to check for
     * @return {@code true} if all the specified characteristics are present,
     * else {@code false}
     */
    default boolean hasCharacteristics(int characteristics) {
        return (characteristics() & characteristics) == characteristics;
    }

    /**
     * If this Spliterator's source is {@link #SORTED} by a {@link Comparator},
     * returns that {@code Comparator}. If the source is {@code SORTED} in
     * {@linkplain Comparable natural order}, returns {@code null}.  Otherwise,
     * if the source is not {@code SORTED}, throws {@link IllegalStateException}.
     * 如果此 Spliterator 的源 由Comparator排序，则返回该比较器。 
     * 如果源按自然顺序排序，则返回 null。 否则，如果源未排序，则抛出 IllegalStateException。
     *
     * @implSpec
     * The default implementation always throws {@link IllegalStateException}.
     *
     * @return a Comparator, or {@code null} if the elements are sorted in the
     * natural order.
     * @throws IllegalStateException if the spliterator does not report
     *         a characteristic of {@code SORTED}.
     */
    default Comparator<? super T> getComparator() {
        throw new IllegalStateException();
    }

    /**
     * Characteristic value signifying that an encounter order is defined for
     * elements. If so, this Spliterator guarantees that method
     * {@link #trySplit} splits a strict prefix of elements, that method
     * {@link #tryAdvance} steps by one element in prefix order, and that
     * {@link #forEachRemaining} performs actions in encounter order.
     * 
     * 表示为元素定义了相遇顺序的特征值。 如果是这样，则此 Spliterator 保证方法 trySplit 拆分元素的严格前缀，
     * 方法 tryAdvance 按前缀顺序逐个处理元素，并且 forEachRemaining 按遇到顺序执行操作。
     *
     * <p>A {@link Collection} has an encounter order if the corresponding
     * {@link Collection#iterator} documents an order. If so, the encounter
     * order is the same as the documented order. Otherwise, a collection does
     * not have an encounter order.
     * 
     * 如果相应的 Collection.iterator 记录了顺序，则 Collection 具有遇到顺序。 
     * 如果是这样，遇到的顺序与记录的顺序相同。 否则，集合没有遇到顺序。
     *
     * @apiNote Encounter order is guaranteed to be ascending index order for
     * any {@link List}. But no order is guaranteed for hash-based collections
     * such as {@link HashSet}. Clients of a Spliterator that reports
     * {@code ORDERED} are expected to preserve ordering constraints in
     * non-commutative parallel computations.
     * 
     * 任何列表的遇到顺序保证是升序索引顺序。 但对于基于散列的集合（如 HashSet），无法保证顺序。 
     * 报告 ORDERED 的 Spliterator 的客户端应该在非交换并行计算中保留排序约束。
     */
    public static final int ORDERED    = 0x00000010;

    /**
     * Characteristic value signifying that, for each pair of
     * encountered elements {@code x, y}, {@code !x.equals(y)}. This
     * applies for example, to a Spliterator based on a {@link Set}.
     * 
     * 特征值表示，对于每对遇到的元素 x, y, !x.equals(y)。 
     * 例如，这适用于基于 Set 的 Spliterator。
     */
    public static final int DISTINCT   = 0x00000001;

    /**
     * Characteristic value signifying that encounter order follows a defined
     * sort order. If so, method {@link #getComparator()} returns the associated
     * Comparator, or {@code null} if all elements are {@link Comparable} and
     * are sorted by their natural ordering.
     * 
     * 表示遇到顺序遵循定义的排序顺序的特征值。 
     * 如果是这样，方法 getComparator() 返回关联的 Comparator，
     * 如果所有元素都是 Comparable 并且按其自然顺序排序，则返回 null。
     *
     * <p>A Spliterator that reports {@code SORTED} must also report
     * {@code ORDERED}.
     * 
     * 报告 SORTED 的 Spliterator 也必须报告 ORDERED。
     *
     * @apiNote The spliterators for {@code Collection} classes in the JDK that
     * implement {@link NavigableSet} or {@link SortedSet} report {@code SORTED}.
     * 
     * JDK 中实现 NavigableSet 或 SortedSet 的 Collection 类的拆分器报告 SORTED。
     */
    public static final int SORTED     = 0x00000004;

    /**
     * Characteristic value signifying that the value returned from
     * {@code estimateSize()} prior to traversal or splitting represents a
     * finite size that, in the absence of structural source modification,
     * represents an exact count of the number of elements that would be
     * encountered by a complete traversal.
     * 
     * 特征值表示遍历或拆分之前从 estimateSize() 返回的值表示有限大小，
     * 在没有结构源修改的情况下，表示完整遍历将遇到的元素数量的精确计数。
     *
     * @apiNote Most Spliterators for Collections, that cover all elements of a
     * {@code Collection} report this characteristic. Sub-spliterators, such as
     * those for {@link HashSet}, that cover a sub-set of elements and
     * approximate their reported size do not.
     * 
     * 大多数 Collections 的 Spliterators，涵盖了 Collection 的所有元素，报告了这个特征。 
     * 子拆分器（例如 HashSet 的子拆分器）覆盖了元素的子集并近似于它们报告的大小。
     */
    public static final int SIZED      = 0x00000040;

    /**
     * Characteristic value signifying that the source guarantees that
     * encountered elements will not be {@code null}. (This applies,
     * for example, to most concurrent collections, queues, and maps.)
     * 表示源保证遇到的元素不会为空的特征值。（例如，这适用于大多数并发集合、队列和映射。）
     */
    public static final int NONNULL    = 0x00000100;

    /**
     * Characteristic value signifying that the element source cannot be
     * structurally modified; that is, elements cannot be added, replaced, or
     * removed, so such changes cannot occur during traversal. A Spliterator
     * that does not report {@code IMMUTABLE} or {@code CONCURRENT} is expected
     * to have a documented policy (for example throwing
     * {@link ConcurrentModificationException}) concerning structural
     * interference detected during traversal.
     * 
     * 表示元素源不能进行结构修改的特征值； 
     * 也就是说，不能添加、替换或删除元素，因此在遍历期间不会发生此类更改。 
     * 不报告 IMMUTABLE 或 CONCURRENT 的 Spliterator 
     * 应该有关于遍历期间检测到的结构干扰的记录策略（例如抛出 ConcurrentModificationException）。
     */
    public static final int IMMUTABLE  = 0x00000400;

    /**
     * Characteristic value signifying that the element source may be safely
     * concurrently modified (allowing additions, replacements, and/or removals)
     * by multiple threads without external synchronization. If so, the
     * Spliterator is expected to have a documented policy concerning the impact
     * of modifications during traversal.
     * 
     * 特征值表示元素源可以在没有外部同步的情况下由多个线程安全地并发修改（允许添加、替换和/或删除）。 
     * 如果是这样，Spliterator 应该有一个关于遍历期间修改影响的文档化策略。
     *
     * <p>A top-level Spliterator should not report both {@code CONCURRENT} and
     * {@code SIZED}, since the finite size, if known, may change if the source
     * is concurrently modified during traversal. Such a Spliterator is
     * inconsistent and no guarantees can be made about any computation using
     * that Spliterator. Sub-spliterators may report {@code SIZED} if the
     * sub-split size is known and additions or removals to the source are not
     * reflected when traversing.
     * 
     * 顶级 Spliterator 不应同时报告 CONCURRENT 和 SIZED，
     * 因为有限大小（如果已知）可能会在遍历期间同时修改源时发生变化。 
     * 这样的 Spliterator 是不一致的，并且不能保证使用该 Spliterator 进行任何计算。 
     * 如果子拆分大小已知并且在遍历时未反映对源的添加或删除，则子拆分器可能会报告 SIZED。
     *
     * @apiNote Most concurrent collections maintain a consistency policy
     * guaranteeing accuracy with respect to elements present at the point of
     * Spliterator construction, but possibly not reflecting subsequent
     * additions or removals.
     * 
     * 大多数并发集合保持一致性策略，以保证在 Spliterator 构造时出现的元素的准确性，
     * 但可能不会反映后续的添加或删除。
     */
    public static final int CONCURRENT = 0x00001000;

    /**
     * Characteristic value signifying that all Spliterators resulting from
     * {@code trySplit()} will be both {@link #SIZED} and {@link #SUBSIZED}.
     * (This means that all child Spliterators, whether direct or indirect, will
     * be {@code SIZED}.)
     * 
     * 特征值表示由 trySplit() 产生的所有 Spliterators 都将是 SIZED 和 SUBSIZED。 
     * （这意味着所有子 Spliterator，无论是直接的还是间接的，都将是 SIZED。）
     *
     * <p>A Spliterator that does not report {@code SIZED} as required by
     * {@code SUBSIZED} is inconsistent and no guarantees can be made about any
     * computation using that Spliterator.
     * 
     * 不按照 SUBSIZED 的要求报告 SIZED 的 Spliterator 是不一致的，
     * 并且不能保证使用该 Spliterator 的任何计算。
     *
     * @apiNote Some spliterators, such as the top-level spliterator for an
     * approximately balanced binary tree, will report {@code SIZED} but not
     * {@code SUBSIZED}, since it is common to know the size of the entire tree
     * but not the exact sizes of subtrees.
     * 
     * 一些拆分器，例如近似平衡二叉树的顶层拆分器，将报告 SIZED 而不是 SUBSIZED，
     * 因为通常知道整棵树的大小而不知子树的确切大小。
     */
    public static final int SUBSIZED = 0x00004000;
}
```