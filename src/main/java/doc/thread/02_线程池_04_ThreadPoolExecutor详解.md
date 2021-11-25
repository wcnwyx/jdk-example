##一：基础概念、内部变量预览等
```java
/**
 * An {@link ExecutorService} that executes each submitted task using
 * one of possibly several pooled threads, normally configured
 * using {@link Executors} factory methods.
 * 
 * 一种ExecutorService，它使用可能的几个池线程之一执行每个提交的任务，这些线程通常使用Executors工厂方法进行配置。
 *
 * <p>Thread pools address two different problems: they usually
 * provide improved performance when executing large numbers of
 * asynchronous tasks, due to reduced per-task invocation overhead,
 * and they provide a means of bounding and managing the resources,
 * including threads, consumed when executing a collection of tasks.
 * Each {@code ThreadPoolExecutor} also maintains some basic
 * statistics, such as the number of completed tasks.
 * 
 * 线程池解决两个不同的问题：它们通常在执行大量异步任务时提供更好的性能，这是因为减少了每个任务的调用开销，
 * 它们还提供了一种方法来限制和管理执行任务集合时所消耗的资源，包括线程。
 * 每个 ThreadPoolExecutor 还维护一些基本统计信息，例如已完成任务的数量。
 *
 * <p>To be useful across a wide range of contexts, this class
 * provides many adjustable parameters and extensibility
 * hooks. However, programmers are urged to use the more convenient
 * {@link Executors} factory methods {@link
 * Executors#newCachedThreadPool} (unbounded thread pool, with
 * automatic thread reclamation), {@link Executors#newFixedThreadPool}
 * (fixed size thread pool) and {@link
 * Executors#newSingleThreadExecutor} (single background thread), that
 * preconfigure settings for the most common usage
 * scenarios. Otherwise, use the following guide when manually
 * configuring and tuning this class:
 * 
 * 为了在广泛的上下文中有用，这个类提供了许多可调整的参数和可扩展性挂钩。
 * 但是，建议程序员使用更方便的Executors工厂方法
 * Executors.newCachedThreadPool（具有自动线程回收功能的无界线程池）、
 * Executors.newFixedThreadPool（固定大小的线程池）、
 * Executors.newSingleThreadExecutor（单后台线程），最常见的使用场景的预配置设置。
 * 否则，请在手动配置和优化此类时使用以下指南：
 *
 * <dl>
 *
 * <dt>Core and maximum pool sizes</dt>
 *
 * <dd>A {@code ThreadPoolExecutor} will automatically adjust the
 * pool size (see {@link #getPoolSize})
 * according to the bounds set by
 * corePoolSize (see {@link #getCorePoolSize}) and
 * maximumPoolSize (see {@link #getMaximumPoolSize}).
 * 
 * ThreadPoolExecutor将根据corePoolSize（请参阅getCorePoolSize）
 * 和maximumPoolSize（请参阅getMaximumPoolSize）设置的界限自动调整池大小（请参阅getPoolSize）。
 *
 * When a new task is submitted in method {@link #execute(Runnable)},
 * and fewer than corePoolSize threads are running, a new thread is
 * created to handle the request, even if other worker threads are
 * idle.  If there are more than corePoolSize but less than
 * maximumPoolSize threads running, a new thread will be created only
 * if the queue is full.  By setting corePoolSize and maximumPoolSize
 * the same, you create a fixed-size thread pool. By setting
 * maximumPoolSize to an essentially unbounded value such as {@code
 * Integer.MAX_VALUE}, you allow the pool to accommodate an arbitrary
 * number of concurrent tasks. Most typically, core and maximum pool
 * sizes are set only upon construction, but they may also be changed
 * dynamically using {@link #setCorePoolSize} and {@link
 * #setMaximumPoolSize}. </dd>
 * 
 * 当execute(Runnable)方法提交任务时，
 * 如果正在运行的线程数小于corePoolSize，就会新建一个线程来处理这个请求，即使其它工作线程空闲。
 * 如果正在运行的线程数大于corePoolSize 小于maximumPoolSize，只有当queue满了的时候，才会创建一个新线程。
 * 通过设置相同的corePoolSize和maximumPoolSize，可以创建一个固定大小的线程池。
 * 通过将maximumPoolSize设置为一个基本无界的值（例如Integer.MAX_VALUE），将允许池子容纳任意数量的并发任务。
 * 大多数情况下，corePoolSize和maximumPoolSize仅在构建时设置，
 * 但是他们也可以通过setCorePoolSize和setMaximumPoolSize动态修改。
 *
 * <dt>On-demand construction</dt>
 * 
 * 按需构建
 *
 * <dd>By default, even core threads are initially created and
 * started only when new tasks arrive, but this can be overridden
 * dynamically using method {@link #prestartCoreThread} or {@link
 * #prestartAllCoreThreads}.  You probably want to prestart threads if
 * you construct the pool with a non-empty queue. </dd>
 * 
 * 默认情况下，只有在新任务到达时才最初创建和启动核心线程，但这可以使用方法 prestartCoreThread 或 prestartAllCoreThreads 动态覆盖。
 * 如果您使用非空队列构造池，您可能想要预启动线程
 *
 * <dt>Creating new threads</dt>
 * 
 * 创建新线程
 *
 * <dd>New threads are created using a {@link ThreadFactory}.  If not
 * otherwise specified, a {@link Executors#defaultThreadFactory} is
 * used, that creates threads to all be in the same {@link
 * ThreadGroup} and with the same {@code NORM_PRIORITY} priority and
 * non-daemon status. By supplying a different ThreadFactory, you can
 * alter the thread's name, thread group, priority, daemon status,
 * etc. If a {@code ThreadFactory} fails to create a thread when asked
 * by returning null from {@code newThread}, the executor will
 * continue, but might not be able to execute any tasks. Threads
 * should possess the "modifyThread" {@code RuntimePermission}. If
 * worker threads or other threads using the pool do not possess this
 * permission, service may be degraded: configuration changes may not
 * take effect in a timely manner, and a shutdown pool may remain in a
 * state in which termination is possible but not completed.</dd>
 * 
 * 使用一个ThreadFactory来创建新线程。如果没有特别指定，会使用Executors.defaultThreadFactory，
 * 创建的线程都在同一个 ThreadGroup 中，并具有相同的 NORM_PRIORITY 优先级和非守护进程状态。
 * 通过提供一个不同的ThreadFactory，你可以改变线程的名字、线程组、优先级、守护状态等。
 * 当请求创建线程，但是ThreadFactory通过newThread方法创建失败，executor将继续执行，但是可能不会处理任何任务。
 * 线程应该拥有RuntimePermission的"modifyThread"权限。如果工作线程或其它使用池的线程不拥有此权限，
 * 服务可能会降级：配置更改可能无法及时生效，关闭池可能会保持可以终止但未完成的状态。
 *
 * <dt>Keep-alive times</dt>
 *
 * <dd>If the pool currently has more than corePoolSize threads,
 * excess threads will be terminated if they have been idle for more
 * than the keepAliveTime (see {@link #getKeepAliveTime(TimeUnit)}).
 * This provides a means of reducing resource consumption when the
 * pool is not being actively used. If the pool becomes more active
 * later, new threads will be constructed. This parameter can also be
 * changed dynamically using method {@link #setKeepAliveTime(long,
 * TimeUnit)}.  Using a value of {@code Long.MAX_VALUE} {@link
 * TimeUnit#NANOSECONDS} effectively disables idle threads from ever
 * terminating prior to shut down. By default, the keep-alive policy
 * applies only when there are more than corePoolSize threads. But
 * method {@link #allowCoreThreadTimeOut(boolean)} can be used to
 * apply this time-out policy to core threads as well, so long as the
 * keepAliveTime value is non-zero. </dd>
 * 
 * 如果池中当前的线程数量大于corePoolSize，如果空闲时间超过keepAliveTime，过量的将被终止。
 * 这提供了一种在未积极使用池时减少资源消耗的方法。如果池稍后变得更加活跃，则将构建新线程。
 * 该参数也可以通过setKeepAliveTime来动态修改。使用Long.MAX_VALUE这个值可以在关闭前禁止终止线程。
 * 默认情况下，仅当有超过 corePoolSize 的线程时，keep-alive 策略才适用。
 * 但是方法  allowCoreThreadTimeOut(boolean) 也可将此超时策略应用于核心线程，只要 keepAliveTime 值不为零。
 *
 * <dt>Queuing</dt>
 *
 * <dd>Any {@link BlockingQueue} may be used to transfer and hold
 * submitted tasks.  The use of this queue interacts with pool sizing:
 * 
 * 任何 BlockingQueue 都可用于传输和保持提交的任务。此队列的使用与池大小相互影响：
 *
 * <ul>
 *
 * <li> If fewer than corePoolSize threads are running, the Executor
 * always prefers adding a new thread
 * rather than queuing.</li>
 * 如果正在运行的线程数少于corePoolSize，Executor总是添加一个新线程 而不是排队。
 *
 * <li> If corePoolSize or more threads are running, the Executor
 * always prefers queuing a request rather than adding a new
 * thread.</li>
 * 如果正在运行的线程数大于等于corePoolSize，Executor总是将请求进行排队，而不是添加一个新的线程。
 *
 * <li> If a request cannot be queued, a new thread is created unless
 * this would exceed maximumPoolSize, in which case, the task will be
 * rejected.</li>
 * 如果一个请求不能进行排队，将创建一个新的线程，除非这会超过maximumPoolSize，这种情况下任务将被拒绝。
 *
 * </ul>
 *
 * There are three general strategies for queuing:
 * 排队的三种一般性策略：
 * <ol>
 *
 * <li> <em> Direct handoffs.</em> A good default choice for a work
 * queue is a {@link SynchronousQueue} that hands off tasks to threads
 * without otherwise holding them. Here, an attempt to queue a task
 * will fail if no threads are immediately available to run it, so a
 * new thread will be constructed. This policy avoids lockups when
 * handling sets of requests that might have internal dependencies.
 * Direct handoffs generally require unbounded maximumPoolSizes to
 * avoid rejection of new submitted tasks. This in turn admits the
 * possibility of unbounded thread growth when commands continue to
 * arrive on average faster than they can be processed.  </li>
 * 工作队列的一个很好的默认选择是 SynchronousQueue ，它将任务交给线程而不用其他方式保留它们。
 *
 * <li><em> Unbounded queues.</em> Using an unbounded queue (for
 * example a {@link LinkedBlockingQueue} without a predefined
 * capacity) will cause new tasks to wait in the queue when all
 * corePoolSize threads are busy. Thus, no more than corePoolSize
 * threads will ever be created. (And the value of the maximumPoolSize
 * therefore doesn't have any effect.)  This may be appropriate when
 * each task is completely independent of others, so tasks cannot
 * affect each others execution; for example, in a web page server.
 * While this style of queuing can be useful in smoothing out
 * transient bursts of requests, it admits the possibility of
 * unbounded work queue growth when commands continue to arrive on
 * average faster than they can be processed.  </li>
 *
 * <li><em>Bounded queues.</em> A bounded queue (for example, an
 * {@link ArrayBlockingQueue}) helps prevent resource exhaustion when
 * used with finite maximumPoolSizes, but can be more difficult to
 * tune and control.  Queue sizes and maximum pool sizes may be traded
 * off for each other: Using large queues and small pools minimizes
 * CPU usage, OS resources, and context-switching overhead, but can
 * lead to artificially low throughput.  If tasks frequently block (for
 * example if they are I/O bound), a system may be able to schedule
 * time for more threads than you otherwise allow. Use of small queues
 * generally requires larger pool sizes, which keeps CPUs busier but
 * may encounter unacceptable scheduling overhead, which also
 * decreases throughput.  </li>
 *
 * </ol>
 *
 * </dd>
 *
 * <dt>Rejected tasks</dt>
 *
 * <dd>New tasks submitted in method {@link #execute(Runnable)} will be
 * <em>rejected</em> when the Executor has been shut down, and also when
 * the Executor uses finite bounds for both maximum threads and work queue
 * capacity, and is saturated.  In either case, the {@code execute} method
 * invokes the {@link
 * RejectedExecutionHandler#rejectedExecution(Runnable, ThreadPoolExecutor)}
 * method of its {@link RejectedExecutionHandler}.  Four predefined handler
 * policies are provided:
 *
 * <ol>
 *
 * <li> In the default {@link ThreadPoolExecutor.AbortPolicy}, the
 * handler throws a runtime {@link RejectedExecutionException} upon
 * rejection. </li>
 *
 * <li> In {@link ThreadPoolExecutor.CallerRunsPolicy}, the thread
 * that invokes {@code execute} itself runs the task. This provides a
 * simple feedback control mechanism that will slow down the rate that
 * new tasks are submitted. </li>
 *
 * <li> In {@link ThreadPoolExecutor.DiscardPolicy}, a task that
 * cannot be executed is simply dropped.  </li>
 *
 * <li>In {@link ThreadPoolExecutor.DiscardOldestPolicy}, if the
 * executor is not shut down, the task at the head of the work queue
 * is dropped, and then execution is retried (which can fail again,
 * causing this to be repeated.) </li>
 *
 * </ol>
 *
 * It is possible to define and use other kinds of {@link
 * RejectedExecutionHandler} classes. Doing so requires some care
 * especially when policies are designed to work only under particular
 * capacity or queuing policies. </dd>
 *
 * <dt>Hook methods</dt>
 *
 * <dd>This class provides {@code protected} overridable
 * {@link #beforeExecute(Thread, Runnable)} and
 * {@link #afterExecute(Runnable, Throwable)} methods that are called
 * before and after execution of each task.  These can be used to
 * manipulate the execution environment; for example, reinitializing
 * ThreadLocals, gathering statistics, or adding log entries.
 * Additionally, method {@link #terminated} can be overridden to perform
 * any special processing that needs to be done once the Executor has
 * fully terminated.
 *
 * <p>If hook or callback methods throw exceptions, internal worker
 * threads may in turn fail and abruptly terminate.</dd>
 *
 * <dt>Queue maintenance</dt>
 *
 * <dd>Method {@link #getQueue()} allows access to the work queue
 * for purposes of monitoring and debugging.  Use of this method for
 * any other purpose is strongly discouraged.  Two supplied methods,
 * {@link #remove(Runnable)} and {@link #purge} are available to
 * assist in storage reclamation when large numbers of queued tasks
 * become cancelled.</dd>
 *
 * <dt>Finalization</dt>
 *
 * <dd>A pool that is no longer referenced in a program <em>AND</em>
 * has no remaining threads will be {@code shutdown} automatically. If
 * you would like to ensure that unreferenced pools are reclaimed even
 * if users forget to call {@link #shutdown}, then you must arrange
 * that unused threads eventually die, by setting appropriate
 * keep-alive times, using a lower bound of zero core threads and/or
 * setting {@link #allowCoreThreadTimeOut(boolean)}.  </dd>
 *
 * </dl>
 *
 * <p><b>Extension example</b>. Most extensions of this class
 * override one or more of the protected hook methods. For example,
 * here is a subclass that adds a simple pause/resume feature:
 *
 *  <pre> {@code
 * class PausableThreadPoolExecutor extends ThreadPoolExecutor {
 *   private boolean isPaused;
 *   private ReentrantLock pauseLock = new ReentrantLock();
 *   private Condition unpaused = pauseLock.newCondition();
 *
 *   public PausableThreadPoolExecutor(...) { super(...); }
 *
 *   protected void beforeExecute(Thread t, Runnable r) {
 *     super.beforeExecute(t, r);
 *     pauseLock.lock();
 *     try {
 *       while (isPaused) unpaused.await();
 *     } catch (InterruptedException ie) {
 *       t.interrupt();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void pause() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = true;
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void resume() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = false;
 *       unpaused.signalAll();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ThreadPoolExecutor extends AbstractExecutorService {

    /**
     * The main pool control state, ctl, is an atomic integer packing
     * two conceptual fields
     *   workerCount, indicating the effective number of threads
     *   runState,    indicating whether running, shutting down etc
     * 主池控制状态，ctl，是一个包含两个概念字段的原子整数：
     *    workerCount, 指示有效线程数
     *    runState,    只是是否正在运行、关闭等。
     *
     * In order to pack them into one int, we limit workerCount to
     * (2^29)-1 (about 500 million) threads rather than (2^31)-1 (2
     * billion) otherwise representable. If this is ever an issue in
     * the future, the variable can be changed to be an AtomicLong,
     * and the shift/mask constants below adjusted. But until the need
     * arises, this code is a bit faster and simpler using an int.
     * 为了将他们打包进一个int，我们限制workerCount为(2^29)-1 (大约5亿)个线程而不是(2^31)-1（20亿）个。
     * 如果这在将来成为问题，可以将变量更改为AtomicLong，并调整下面的移位/掩码常数。
     * 但是，在需要之前，使用int代码会更快、更简单一些。
     *
     * The workerCount is the number of workers that have been
     * permitted to start and not permitted to stop.  The value may be
     * transiently different from the actual number of live threads,
     * for example when a ThreadFactory fails to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating. The user-visible pool size is
     * reported as the current size of the workers set.
     * workCount是允许启动和不允许轻质的worker数量。
     * 该值可能瞬间的不同于活动线程的实际数量，例如，当ThreadFactory在被请求时无法创建线程，
     * 并且退出的线程在终止之前仍在执行簿记时。用户可见池大小将报告为worker集合的当前大小。
     *
     * The runState provides the main lifecycle control, taking on values:
     * runState提供主要的生命周期控制，具有以下值：
     *
     *   RUNNING:  Accept new tasks and process queued tasks
     *             接受新任务并处理排队的任务
     *             
     *   SHUTDOWN: Don't accept new tasks, but process queued tasks
     *             不接受新任务，但是执行排队的任务
     *             
     *   STOP:     Don't accept new tasks, don't process queued tasks,
     *             and interrupt in-progress tasks
     *             不接受新任务，不执行排队的任务，并且中断正在执行的任务。
     *             
     *   TIDYING:  All tasks have terminated, workerCount is zero,
     *             the thread transitioning to state TIDYING
     *             will run the terminated() hook method
     *             所有任务已经结束，workCount为0，转换到状态TIDYING的线程将运行terminated()钩子方法
     *             
     *   TERMINATED: terminated() has completed 
     *               terminated()已完成
     *
     * The numerical order among these values matters, to allow
     * ordered comparisons. The runState monotonically increases over
     * time, but need not hit each state. The transitions are:
     * 这些值之间的数字顺序很重要，以便进行有序比较。
     * 运行状态随时间单调增加，但不需要达到每个状态。这些转变是：
     *
     * RUNNING -> SHUTDOWN
     *    On invocation of shutdown(), perhaps implicitly in finalize()
     *    调用shutdown()时，可能隐式地在finalize()中
     *    
     * (RUNNING or SHUTDOWN) -> STOP
     *    On invocation of shutdownNow()
     *    调用shutdownNow()时
     *    
     * SHUTDOWN -> TIDYING
     *    When both queue and pool are empty
     *    当队列和池都是空的时候
     *    
     * STOP -> TIDYING
     *    When pool is empty
     *    当池是空的时候
     *    
     * TIDYING -> TERMINATED
     *    When the terminated() hook method has completed
     *    当terminated()钩子方法执行完时
     *
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     * 在awaitTermination()中等待的线程将在状态达到TERMINATED时返回。
     *
     * Detecting the transition from SHUTDOWN to TIDYING is less
     * straightforward than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a recheck -- see
     * below).
     * 检测从SHUTDOWN到TIDYING的转换并不像您希望的那样简单，
     * 因为队列在非空之后可能变为空，而在SHUTDOWN状态期间，队列也可能变为空，
     * 我们看到workerCount为0（有时需要重新检查——见下文）。
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    private static final int COUNT_BITS = Integer.SIZE - 3;
	
	//实际最大线程数，用户指定的最大线程数不会超过此值。
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    //获取当前线程池状态，从ctl中解析出来。
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
	
	//获取当前worker数量，即线程数量，也是通过ctl解析出来。
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    
	private static int ctlOf(int rs, int wc) { return rs | wc; }

	//用于判断当前线程池是否是运行装填，只有RUNNING的值小于SHUTDOWN
	private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * The queue used for holding tasks and handing off to worker
     * threads.  We do not require that workQueue.poll() returning
     * null necessarily means that workQueue.isEmpty(), so rely
     * solely on isEmpty to see if the queue is empty (which we must
     * do for example when deciding whether to transition from
     * SHUTDOWN to TIDYING).  This accommodates special-purpose
     * queues such as DelayQueues for which poll() is allowed to
     * return null even if it may later return non-null when delays
     * expire.
     * 用于保存任务并将其传递给工作线程的队列。
     * 我们不要求workQueue.poll()返回null必然意味着workQueue.isEmptya()为空,
     * 因此，仅依靠isEmpty来查看队列是否为空（例如，在决定是否从关闭转换为清理时，我们必须这样做）。
     * 这适用于特殊用途的队列，例如允许poll（）返回null的DelayQueues，
     * 即使延迟过期后poll（）可能返回非null。
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * Lock held on access to workers set and related bookkeeping.
     * While we could use a concurrent set of some sort, it turns out
     * to be generally preferable to use a lock. Among the reasons is
     * that this serializes interruptIdleWorkers, which avoids
     * unnecessary interrupt storms, especially during shutdown.
     * Otherwise exiting threads would concurrently interrupt those
     * that have not yet interrupted. It also simplifies some of the
     * associated statistics bookkeeping of largestPoolSize etc. We
     * also hold mainLock on shutdown and shutdownNow, for the sake of
     * ensuring workers set is stable while separately checking
     * permission to interrupt and actually interrupting.
     * 锁定workers集合和相关簿记的访问权限。
     * 虽然我们可以使用某种类型的并发集，但事实证明，通常最好使用锁。
     * 其中一个原因是它序列化了中断的workers？？？，从而避免了不必要的中断风暴，尤其是在关闭期间。
     * 否则，退出的线程将同时中断那些尚未中断的线程。
     * 它还简化了一些与largestPoolSize等相关的统计簿记。
     * 我们还在shutdown和shutdownNow时保持mainLock，以确保workers设置稳定，
     * 同时分别检查是否允许中断和实际中断。
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Set containing all worker threads in pool. Accessed only when
     * holding mainLock.
     * 包含池中所有worker线程的集合。仅在持有mainLock时访问。
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * Wait condition to support awaitTermination
     * 支持awaitTermination方法的一个Condition
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Tracks largest attained pool size. Accessed only under
     * mainLock.
	 * 跟踪达到的最大池大小。仅在mainLock下访问。
     */
    private int largestPoolSize;

    /**
     * Counter for completed tasks. Updated only on termination of
     * worker threads. Accessed only under mainLock.
	 * 已完成任务的计数器。仅在工作线程终止时更新。仅在mainLock下访问。
     */
    private long completedTaskCount;

    /*
     * All user control parameters are declared as volatiles so that
     * ongoing actions are based on freshest values, but without need
     * for locking, since no internal invariants depend on them
     * changing synchronously with respect to other actions.
	 * 所有用户控制参数都声明为volatile，因此正在进行的操作基于最新的值，
	 * 但不需要锁定，因为没有内部不变数据依赖于它们相对于其他操作同步更改。
     */

    /**
     * Factory for new threads. All threads are created using this
     * factory (via method addWorker).  All callers must be prepared
     * for addWorker to fail, which may reflect a system or user's
     * policy limiting the number of threads.  Even though it is not
     * treated as an error, failure to create threads may result in
     * new tasks being rejected or existing ones remaining stuck in
     * the queue.
	 * 新线程的工厂。所有线程通过这个工厂来创建（通过addWorker方法）。
	 * 所有调用者都必须为addWorker失败做好准备，这可能反映了系统或用户限制线程数量的策略。
	 * 即使未将其视为错误，创建线程的失败也可能会导致新任务被拒绝，或现有任务仍卡在队列中。
     *
     * We go further and preserve pool invariants even in the face of
     * errors such as OutOfMemoryError, that might be thrown while
     * trying to create threads.  Such errors are rather common due to
     * the need to allocate a native stack in Thread.start, and users
     * will want to perform clean pool shutdown to clean up.  There
     * will likely be enough memory available for the cleanup code to
     * complete without encountering yet another OutOfMemoryError.
	 * 我们更进一步，保留池不变量，即使在遇到错误（例如OutOfMemoryError）时也是如此，
	 * 这些错误可能在尝试创建线程时抛出。由于需要在Thread.start中分配本机堆栈，
	 * 因此此类错误非常常见，用户需要执行清理池关闭以进行清理。
	 * 可能会有足够的内存来完成清理代码，而不会遇到另一个OutOfMemoryError。
     */
    private volatile ThreadFactory threadFactory;

    /**
     * Handler called when saturated or shutdown in execute.
	 * execute中在饱和或关闭时调用的处理程序。
     */
    private volatile RejectedExecutionHandler handler;

    /**
     * Timeout in nanoseconds for idle threads waiting for work.
     * Threads use this timeout when there are more than corePoolSize
     * present or if allowCoreThreadTimeOut. Otherwise they wait
     * forever for new work.
	 * 空闲线程等待工作的超时时间（纳秒）。
	 * 当目前存在线程多于corePoolSize或allowCoreThreadTimeOut(允许核心线程超时)时，线程使用此超时。
	 * 否则，他们将永远等待新的工作。
     */
    private volatile long keepAliveTime;

    /**
     * If false (default), core threads stay alive even when idle.
     * If true, core threads use keepAliveTime to time out waiting
     * for work.
	 * 如果是flase（默认值），核心线程即使在空闲时也保持活着。
	 * 如果是true，核心线程在等待工作时使用keepAliveTime来进行超时。
     */
    private volatile boolean allowCoreThreadTimeOut;

    /**
     * Core pool size is the minimum number of workers to keep alive
     * (and not allow to time out etc) unless allowCoreThreadTimeOut
     * is set, in which case the minimum is zero.
	 * 核心线程数是保持存活状态（不允许超时等）的最小工作线程数，
	 * 除非设置了allowCoreThreadTimeOut，在这种情况下，最小值为零。
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size. Note that the actual maximum is internally
     * bounded by CAPACITY.
	 * 最大池大小。请注意，实际最大值在内部由CAPACITY限制。
     */
    private volatile int maximumPoolSize;

    /**
     * The default rejected execution handler
	 * 默认被拒绝的执行程序。
     */
    private static final RejectedExecutionHandler defaultHandler =
            new AbortPolicy();




    /**
     * Class Worker mainly maintains interrupt control state for
     * threads running tasks, along with other minor bookkeeping.
     * This class opportunistically extends AbstractQueuedSynchronizer
     * to simplify acquiring and releasing a lock surrounding each
     * task execution.  This protects against interrupts that are
     * intended to wake up a worker thread waiting for a task from
     * instead interrupting a task being run.  We implement a simple
     * non-reentrant mutual exclusion lock rather than use
     * ReentrantLock because we do not want worker tasks to be able to
     * reacquire the lock when they invoke pool control methods like
     * setCorePoolSize.  Additionally, to suppress interrupts until
     * the thread actually starts running tasks, we initialize lock
     * state to a negative value, and clear it upon start (in
     * runWorker).
     * Worker类主要维护运行任务的线程的中断控制状态，以及其他次要的簿记。
     * 此类适时地扩展AbstractQueuedSynchronizer，以简化获取和释放围绕每个任务执行的锁的过程。
     * This protects against interrupts that are  intended to wake up a worker thread waiting for a task from instead interrupting a task being run.
     * 我们实现了一个简单的不可重入互斥锁，而不是使用可重入锁（ReentrantLock），
     * 因为我们不希望工作任务在调用诸如setCorePoolSize之类的池控制方法时能够重新获取锁。
     * 此外，为了在线程实际开始运行任务之前抑制中断，我们将锁定状态初始化为负值，
     * 并在启动时清除它（在runWorker中）。
     */
    private final class Worker
            extends AbstractQueuedSynchronizer
            implements Runnable
    {
        /** Thread this worker is running in.  Null if factory fails. */
		// 用来运行此worker的线程，如果工厂失败则为null。
        final Thread thread;
        
        /** Initial task to run.  Possibly null. */
		//要运行的初始任务。可能为null。
        Runnable firstTask;
        
        /** Per-thread task counter */
		//每个线程完成的任务计数器
        volatile long completedTasks;

        /**
         * Creates with given first task and thread from ThreadFactory.
         * @param firstTask the first task (null if none)
         */
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker 在运行worker之前阻止中断
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /** Delegates main run loop to outer runWorker  */
		//将主运行循环委托给外部runWorker方法
        public void run() {
            runWorker(this);
        }

        // Lock methods
        //
        // The value 0 represents the unlocked state.
		// 0表示未锁定状态
        // The value 1 represents the locked state.
		// 1表示锁定状态

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }
    
}
```

##二：execute相关逻辑
```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    
    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     * 在将来的某个时间执行给定的任务。该任务可能执行在一个新的线程里或者一个已经缓存的线程里。
     *
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code RejectedExecutionHandler}.
     * 如果任务不能提交以供执行，要么因为此执行器已关闭，要么因为容量已满，
     * 该任务将被当前的RejectedExecutionHandler执行。
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *         {@code RejectedExecutionHandler}, if the task
     *         cannot be accepted for execution
     * @throws NullPointerException if {@code command} is null
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         * 分3步进行：
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         * 如果当前运行中的线程数小于corePoolSize，尝试使用给定的指令作为其第一个任务来启动一个新的线程，
         * 对addWorker的调用以原子方式检查runState和workerCount，
         * 因此，通过返回false来警告，可以防止在不应该添加线程时错误添加。
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         * 如果一个任务可以被成功的排队，然后，我们仍然需要再次检查是否应该添加一个线程
         * （因为自上次检查以来，已有线程已死亡），或者自进入此方法以来，池是否已关闭。
         * 因此，我们重新检查状态，如有必要，在停止排队时回滚排队，或者在一个线程都没有时启动一个新线程。
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         * 如果我们不能将任务排队，将尝试添加一个新的线程。
         * 如果添加失败，我们知道我们已经被关闭或饱和，因此拒绝这个任务。
         */
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
			//第一步：当前线程数量小于corePoolSize，则调用addWorker()直接创建新的线程
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
		
		//第二步：到这里就说明线程数量大于等于corePoolSize了
        if (isRunning(c) && workQueue.offer(command)) {
		    //当前状态是运行中，并且成功将任务加入workQueue队列。
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
				//再次检测状态，如果不在运行中了，则移除刚添加的任务，并执行拒绝处理
                reject(command);
            else if (workerCountOf(recheck) == 0)
				//状态是运行中，但是当前线程没有了，则直接创建一个新的线程
				//注意，这里addWorker没有初始任务，因为该任务已经加到队列中了，
				//如果将command作为新的线程初始任务，加上队列里的任务，就会重复执行两次了。
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
			//第三步：直接创建新的线程，如果失败，执行拒绝处理
            reject(command);
    }

    /**
     * Checks if a new worker can be added with respect to current
     * pool state and the given bound (either core or maximum). If so,
     * the worker count is adjusted accordingly, and, if possible, a
     * new worker is created and started, running firstTask as its
     * first task. This method returns false if the pool is stopped or
     * eligible to shut down. It also returns false if the thread
     * factory fails to create a thread when asked.  If the thread
     * creation fails, either due to the thread factory returning
     * null, or due to an exception (typically OutOfMemoryError in
     * Thread.start()), we roll back cleanly.
     * 根据当前池状态和给定边界（核心或最大值）检查是否可以添加新的worker。
     * 如果可以，则相应的调整worker计数，并且如果可能的话，一个新的worker将创建并启动，
     * 将firstTask作为其第一个任务来运行。如果pool已关闭或者有资格关闭则该方法返回false。
     * 当线程工厂在请求创建一个线程失败时也将返回false。如果线程创建失败，
     * 或者是由于线程工厂返回null，或者是由于异常（通常是Thread.start()的OutOfMemoryError），
     * 我们将完全回滚。
     *
     * @param firstTask the task the new thread should run first (or
     * null if none). Workers are created with an initial first task
     * (in method execute()) to bypass queuing when there are fewer
     * than corePoolSize threads (in which case we always start one),
     * or when the queue is full (in which case we must bypass queue).
     * Initially idle threads are usually created via
     * prestartCoreThread or to replace other dying workers.
     * 新线程应该首先运行的任务（如果没有则为null）。
     * 当线程少于corePoolSize时（在这种情况下，我们总是启动一个线程），
     * 或者当队列已满时（在这种情况下，我们必须绕过队列），
     * 将使用初始的第一个任务（在方法execute（）中）创建工作线程以绕过队列。
     * 最初空闲线程通常是通过prestartCoreThread创建的，或用于替换其他正在死亡的workers线程
     *
     * @param core if true use corePoolSize as bound, else
     * maximumPoolSize. (A boolean indicator is used here rather than a
     * value to ensure reads of fresh values after checking other pool
     * state).
     * 如果为true，则使用corePoolSize作为绑定，否则使用maximumPoolSize。
     * （这里使用boolean标志而不是值来确保在检查其他池状态后读取新值）
     *
     * @return true if successful
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
		//这里使用了标签逻辑，类似于goto
        retry:
		
		//这个for循环是用来检查状态和更新计数器的
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);//获取当前状态

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                    ! (rs == SHUTDOWN &&
                            firstTask == null &&
                            ! workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                        wc >= (core ? corePoolSize : maximumPoolSize))
					//如果当前线程数大于等于限制数量，则不创建
                    return false;
                if (compareAndIncrementWorkerCount(c))
					//通过CAS操作来增加当前线程数，如果成功则跳出这两个for循环，继续往后执行了。
                    break retry;
                c = ctl.get();  // Re-read ctl 重新获取ctl
                if (runStateOf(c) != rs)
                    //新获取的状态和前面获取的rs不一致了，再次从头循环判断
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
                //compareAndIncrementWorkerCount 的CAS失败因为worker数量改变了，则再次执行内部循环
            }
        }

		//开始创建woker
        boolean workerStarted = false;//worker是否启动成功
        boolean workerAdded = false;//worker是否添加成功
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock. 在保持锁的同时重新检查
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    //在ThreadFactory出现故障或在获取锁之前关闭时退出。
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                            (rs == SHUTDOWN && firstTask == null)) {
                        //如果是运行状态或者SHUTDOWN状态但是firstTask为null
                        if (t.isAlive()) // precheck that t is startable
                            //如果新创建的已经被启动了，则抛出异常
                            throw new IllegalThreadStateException();
                        workers.add(w);//将新创建的worker加到workers集合中
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;//当前个数大于largestPoolSize，则更新之
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();//启动线程
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                //如果线程启动失败，则之后失败处理逻辑
                addWorkerFailed(w);
        }
        return workerStarted;
    }
	
	/**
     * Removes this task from the executor's internal queue if it is
     * present, thus causing it not to be run if it has not already
     * started.
     * 如果存在此任务，则将其从执行器的内部队列中移除，从而导致尚未启动的任务无法运行。
     *
     * <p>This method may be useful as one part of a cancellation
     * scheme.  It may fail to remove tasks that have been converted
     * into other forms before being placed on the internal queue. For
     * example, a task entered using {@code submit} might be
     * converted into a form that maintains {@code Future} status.
     * However, in such cases, method {@link #purge} may be used to
     * remove those Futures that have been cancelled.
     * 此方法可作为取消方案的一部分使用。它可能无法删除在放入内部队列之前已转换为其他形式的任务。
     * 例如，使用submit输入的任务可能会转换为保持Future状态的形式。
     * 然而，在这种情况下，可以使用方法purge删除已取消的Futures。
     *
     * @param task the task to remove
     * @return {@code true} if the task was removed
     */
    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }
	
    /**
     * Invokes the rejected execution handler for the given command.
     * Package-protected for use by ScheduledThreadPoolExecutor.
     * 为给定命令调用被拒绝的执行处理程序。
     */
    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }
}
```

##三： runWorker 任务、线程执行逻辑
execute创建的worker是一个Runnable，其run方法被委托给了runWorker方法。  
```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    /**
     * Main worker run loop.  Repeatedly gets tasks from queue and
     * executes them, while coping with a number of issues:
     * worker运行循环。重复从队列中获取任务并执行它们，同时处理许多问题：
     *
     * 1. We may start out with an initial task, in which case we
     * don't need to get the first one. Otherwise, as long as pool is
     * running, we get tasks from getTask. If it returns null then the
     * worker exits due to changed pool state or configuration
     * parameters.  Other exits result from exception throws in
     * external code, in which case completedAbruptly holds, which
     * usually leads processWorkerExit to replace this thread.
     * 我们可以从一个初始任务开始，在这种情况下，我们不需要得到第一个任务。
     * 否则，只要池在运行，我们就可以从getTask获取任务。
     * 如果返回null，则worker将由于池状态或配置参数的更改而退出。
     * 其他退出源于外部代码中的异常抛出，在这种情况下completedAbruptly保持不变，
     * 这通常会导致processWorkerExit替换此线程。
     *
     * 2. Before running any task, the lock is acquired to prevent
     * other pool interrupts while the task is executing, and then we
     * ensure that unless pool is stopping, this thread does not have
     * its interrupt set.
     * 在运行任何任务之前，都会获取锁以防止任务执行时发生其他池中断，
     * 然后我们会确保除非池停止，否则该线程不会设置其中断。
     * 
     *
     * 3. Each task run is preceded by a call to beforeExecute, which
     * might throw an exception, in which case we cause thread to die
     * (breaking loop with completedAbruptly true) without processing
     * the task.
     * 每个任务运行之前都会调用beforeExecute，这可能会引发异常，在这种情况下，
     * 会导致线程在不处理该任务的情况下死亡（使用CompletedThroughtly true中断循环）。
     *
     * 4. Assuming beforeExecute completes normally, we run the task,
     * gathering any of its thrown exceptions to send to afterExecute.
     * We separately handle RuntimeException, Error (both of which the
     * specs guarantee that we trap) and arbitrary Throwables.
     * Because we cannot rethrow Throwables within Runnable.run, we
     * wrap them within Errors on the way out (to the thread's
     * UncaughtExceptionHandler).  Any thrown exception also
     * conservatively causes thread to die.
     * 假设beforeExecute正常完成，我们运行任务，收集其抛出的任何异常以发送给afterExecute。
     * 我们分别处理RuntimeException、Error（这两个规范都保证我们可以捕获）和任意的Throwable。
     * 因为我们无法在Runnable.run中重新抛出Throwable，所以在退出时将它们包装在Error中（到线程的UncaughtExceptionHandler）。
     * 任何抛出的异常都会保守地导致线程死亡。
     *
     * 5. After task.run completes, we call afterExecute, which may
     * also throw an exception, which will also cause thread to
     * die. According to JLS Sec 14.20, this exception is the one that
     * will be in effect even if task.run throws.
     * task.run 完成后，我们调用afterExecute，其也会抛出一个异常，其也会导致线程死亡。
     * 根据JLS第14.20节，即使是task.run抛出的异常，此异常也将生效。
     *
     * The net effect of the exception mechanics is that afterExecute
     * and the thread's UncaughtExceptionHandler have as accurate
     * information as we can provide about any problems encountered by
     * user code.
     * 异常机制的最终效果是afterExecute和线程的UncaughtExceptionHandler具有尽可能准确的信息，
     * 我们可以提供有关用户代码遇到的任何问题的信息。
     *
     * @param w the worker
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                // 如果pool正在停止，确保线程已经被中断；否则，确保线程没有被中断。
                // ???
                if ((runStateAtLeast(ctl.get(), STOP) ||
                        (Thread.interrupted() &&
                                runStateAtLeast(ctl.get(), STOP))) &&
                        !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }


    /**
     * Performs blocking or timed wait for a task, depending on
     * current configuration settings, or returns null if this worker
     * must exit because of any of:
     * 1. There are more than maximumPoolSize workers (due to
     *    a call to setMaximumPoolSize).
     * 2. The pool is stopped.
     * 3. The pool is shutdown and the queue is empty.
     * 4. This worker timed out waiting for a task, and timed-out
     *    workers are subject to termination (that is,
     *    {@code allowCoreThreadTimeOut || workerCount > corePoolSize})
     *    both before and after the timed wait, and if the queue is
     *    non-empty, this worker is not the last thread in the pool.
     *
     * @return task, or null if the worker must exit, in which case
     *         workerCount is decremented
     */
    private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out? 上次执行poll是否超时

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();//减少当前worker数量并返回null
                return null;
            }

            int wc = workerCountOf(c);

            // Are workers subject to culling? worker是否会被淘汰
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if ((wc > maximumPoolSize || (timed && timedOut))
                    && (wc > 1 || workQueue.isEmpty())) {
                //(当前线程数>最大线程数 || (允许淘汰线程 && 已超时)) && (当前线程数>1 || 队列为空) 
                if (compareAndDecrementWorkerCount(c)) //减少当前worker数量并返回null
                    return null;
                continue;
            }

            try {
                //如果没有任务，线程在此处，阻塞到workQueue该阻塞队列上。
                // 如果该线程可以被淘汰，则使用带超时时间的poll方法，否则使用take一直阻塞到有任务添加到queue中。
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * Performs cleanup and bookkeeping for a dying worker. Called
     * only from worker threads. Unless completedAbruptly is set,
     * assumes that workerCount has already been adjusted to account
     * for exit.  This method removes thread from worker set, and
     * possibly terminates the pool or replaces the worker if either
     * it exited due to user task exception or if fewer than
     * corePoolSize workers are running or queue is non-empty but
     * there are no workers.
     *
     * @param w the worker
     * @param completedAbruptly if the worker died due to user exception
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && ! workQueue.isEmpty())
                    min = 1;
                if (workerCountOf(c) >= min)
                    return; // replacement not needed
            }
            addWorker(null, false);
        }
    }
}
```