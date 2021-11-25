## 接口ExecutorService
看文档注释  
```java
/**
 * An {@link Executor} that provides methods to manage termination and
 * methods that can produce a {@link Future} for tracking progress of
 * one or more asynchronous tasks.
 * 
 * 一个Executor，它提供了管理终止的方法和生成Future的方法，Future用于追踪一个或多个异步任务。
 *
 * <p>An {@code ExecutorService} can be shut down, which will cause
 * it to reject new tasks.  Two different methods are provided for
 * shutting down an {@code ExecutorService}. The {@link #shutdown}
 * method will allow previously submitted tasks to execute before
 * terminating, while the {@link #shutdownNow} method prevents waiting
 * tasks from starting and attempts to stop currently executing tasks.
 * Upon termination, an executor has no tasks actively executing, no
 * tasks awaiting execution, and no new tasks can be submitted.  An
 * unused {@code ExecutorService} should be shut down to allow
 * reclamation of its resources.
 * 
 * ExecutorService可以关闭，这将导致拒绝新任务。提供两种方法来关闭ExecutorService。
 * shutdown方法允许在终止之前执行先前提交的任务。
 * shutdownNow方法阻止等待的任务启动并且尝试停止正在执行中的任务。
 * 终止后，executor没有正在执行的任务，没有等待执行的任务，并且不能提交新的任务。
 * 一个没用的ExecutorService应该关闭，以回收其资源。
 *
 * <p>Method {@code submit} extends base method {@link
 * Executor#execute(Runnable)} by creating and returning a {@link Future}
 * that can be used to cancel execution and/or wait for completion.
 * Methods {@code invokeAny} and {@code invokeAll} perform the most
 * commonly useful forms of bulk execution, executing a collection of
 * tasks and then waiting for at least one, or all, to
 * complete. (Class {@link ExecutorCompletionService} can be used to
 * write customized variants of these methods.)
 * 
 * submit方法通过创建并返回一个Future来扩展基础方法Executor.execute(Runnable)，
 * Future可以用来取消执行 和/或 等待完成。
 * invokeAny和invokeAll用来做最常用的批量执行，执行一组任务并且等待至少一个或全部任务完成。
 * （ExecutorCompletionService类可以用来编写这些方法的自定义变体）
 *
 * <p>The {@link Executors} class provides factory methods for the
 * executor services provided in this package.
 * 
 * Executors类为这个包中提供的executor services提供工厂方法。
 *
 * <h3>Usage Examples</h3>
 * 示例用法
 *
 * Here is a sketch of a network service in which threads in a thread
 * pool service incoming requests. It uses the preconfigured {@link
 * Executors#newFixedThreadPool} factory method:
 * 
 * 下面是一个网络服务的简述，其中线程池中的线程为传入的请求提供服务。
 * 它使用预定义的Executors.newFixedThreadPool工厂方法：
 *
 *  <pre> {@code
 * class NetworkService implements Runnable {
 *   private final ServerSocket serverSocket;
 *   private final ExecutorService pool;
 *
 *   public NetworkService(int port, int poolSize)
 *       throws IOException {
 *     serverSocket = new ServerSocket(port);
 *     pool = Executors.newFixedThreadPool(poolSize);
 *   }
 *
 *   public void run() { // run the service
 *     try {
 *       for (;;) {
 *         pool.execute(new Handler(serverSocket.accept()));
 *       }
 *     } catch (IOException ex) {
 *       pool.shutdown();
 *     }
 *   }
 * }
 *
 * class Handler implements Runnable {
 *   private final Socket socket;
 *   Handler(Socket socket) { this.socket = socket; }
 *   public void run() {
 *     // read and service request on socket
 *   }
 * }}</pre>
 *
 * The following method shuts down an {@code ExecutorService} in two phases,
 * first by calling {@code shutdown} to reject incoming tasks, and then
 * calling {@code shutdownNow}, if necessary, to cancel any lingering tasks:
 * 
 * 下面的方法分两步来关闭ExecutorService，首先调用shutdown来拒绝接受新任务，
 * 然后调用shutdownNow，如果必须的话，取消任何继续存留的任务。
 *
 *  <pre> {@code
 * void shutdownAndAwaitTermination(ExecutorService pool) {
 *   pool.shutdown(); // Disable new tasks from being submitted
 *   try {
 *     // Wait a while for existing tasks to terminate
 *     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
 *       pool.shutdownNow(); // Cancel currently executing tasks
 *       // Wait a while for tasks to respond to being cancelled
 *       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
 *           System.err.println("Pool did not terminate");
 *     }
 *   } catch (InterruptedException ie) {
 *     // (Re-)Cancel if current thread also interrupted
 *     pool.shutdownNow();
 *     // Preserve interrupt status
 *     Thread.currentThread().interrupt();
 *   }
 * }}</pre>
 *
 * <p>Memory consistency effects: Actions in a thread prior to the
 * submission of a {@code Runnable} or {@code Callable} task to an
 * {@code ExecutorService}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * any actions taken by that task, which in turn <i>happen-before</i> the
 * result is retrieved via {@code Future.get()}.
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface ExecutorService extends Executor {

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     * 
     * 启动有序的关闭，执行以前提交的任务，但是不再接受新任务。
     * 如果已经关闭了再次调用没有其他效果。
     * 
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     * 此方法不会等待先前提交的任务执行完成，就是说会立即返回。
     * 可以使用awaitTermination来等待。
     *
     * @throws SecurityException if a security manager exists and
     *         shutting down this ExecutorService may manipulate
     *         threads that the caller is not permitted to modify
     *         because it does not hold {@link
     *         java.lang.RuntimePermission}{@code ("modifyThread")},
     *         or the security manager's {@code checkAccess} method
     *         denies access.
     */
    void shutdown();

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     * 尝试停止所有正在执行的任务，停止正在等待的任务的处理，并返回正在等待执行的任务的列表。
     *
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     * 此方法不会等待活跃的任务终止。使用awaitTermination来等待。
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
     * 除了尽最大努力停止正在执行的任务之外，没有其他保证。例如，典型的实现将使用Thread.interrupt方法来取消，
     * 因此任何未能响应中断的任务可能永远不会终止。
     *
     * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exists and
     *         shutting down this ExecutorService may manipulate
     *         threads that the caller is not permitted to modify
     *         because it does not hold {@link
     *         java.lang.RuntimePermission}{@code ("modifyThread")},
     *         or the security manager's {@code checkAccess} method
     *         denies access.
     */
    List<Runnable> shutdownNow();

    /**
     * Returns {@code true} if this executor has been shut down.
     * 如果已经关闭掉了则返回true
     * @return {@code true} if this executor has been shut down
     */
    boolean isShutdown();

    /**
     * Returns {@code true} if all tasks have completed following shut down.
     * Note that {@code isTerminated} is never {@code true} unless
     * either {@code shutdown} or {@code shutdownNow} was called first.
     * 
     * 如果关闭后所有任务都已完成，则返回true。注意：除非先调用了shutdown或shutdownNow，否则永远不会返回true。
     * 
     * @return {@code true} if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     * 
     * shutdown请求后进行阻塞，直到所有任务执行完成，或者超时发生，或者当前线程被中断（以先发生的为准）
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this executor terminated and 执行器已终止
     *         {@code false} if the timeout elapsed before termination 超时发生在终止之前
     * @throws InterruptedException if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     * 
     * 提供一个返回值的任务以供执行，并返回一个表示任务待定结果的Future。
     * Future的get方法将在任务成功完成时返回任务的结果。
     *
     * <p>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
     * 
     * 如果你想要立即阻塞等待一个任务，你可以使用result=exec.submit(aCallable).get()形式的构造
     *
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
     * 
     * 注意：Executors类包含一些方法可以将一些常用的闭包类对象转（例如 java.security.PrivilegedAction）
     * 换为Callable形式，以便提交它们。
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return the given result upon successful completion.
     * 
     * 提交一个Runnable任务以执行，并且返回一个表示该任务的Future。
     * Future的get方法将在成功完成后返回给定的结果。
     *
     * @param task the task to submit
     * @param result the result to return
     * @param <T> the type of the result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     * 
     * 提交一个Runnable任务以执行，并且返回一个表示该任务的Future。
     * Future的get方法将在成功完成后返回null。
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    Future<?> submit(Runnable task);

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results when all complete.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 
     * 执行给定的多个任务，当所有任务都完成时，返回一个保存其状态和结果的Future列表。
     * 对于返回列表中的每一个元素，Future.isDone为true。
     * 注意，已完成的任务可以是正常终止，也可以是抛出异常终止。
     * 如果在执行此操作时修改了给定的集合，则此方法的是未定义的。？？啥意思
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list, each of which has completed
     * @throws InterruptedException if interrupted while waiting, in
     *         which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks or any of its elements are {@code null}
     * @throws RejectedExecutionException if any task cannot be
     *         scheduled for execution
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, whichever happens first.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Upon return, tasks that have not completed are cancelled.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 
     * 执行给定的多个任务，当所有任务都完成时或者过期超时（无论哪种先发生），返回一个保存其状态和结果的Future列表。
     * 对于返回列表中的每一个元素，Future.isDone为true。
     * 返回后，未完成的任务将被取消。
     * 注意，已完成的任务可以是正常终止，也可以是抛出异常终止。
     * 如果在执行此操作时修改了给定的集合，则此方法的是未定义的。
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list. If the operation did not time out,
     *         each task will have completed. If it did time out, some
     *         of these tasks will not have completed.
     * @throws InterruptedException if interrupted while waiting, in
     *         which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks, any of its elements, or
     *         unit are {@code null}
     * @throws RejectedExecutionException if any task cannot be scheduled
     *         for execution
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do. Upon normal or exceptional return,
     * tasks that have not completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 
     * 执行给定的多个任务，返回一个已成功完成的任务的结果（即没有抛出异常），如果有的话。
     * 在正常或异常返回时，没完成的任务将被取消。
     * 如果在执行此操作时修改了给定的集合，则此方法的是未定义的。
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks or any element task
     *         subject to execution is {@code null}
     * @throws IllegalArgumentException if tasks is empty
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do before the given timeout elapses.
     * Upon normal or exceptional return, tasks that have not
     * completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 
     * 执行给定的多个任务，返回一个已成功完成的任务的结果（即没有抛出异常），如果在超时之前有的话。
     * 在正常或异常返回时，没完成的任务将被取消。
     * 如果在执行此操作时修改了给定的集合，则此方法的是未定义的。
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks, or unit, or any element
     *         task subject to execution is {@code null}
     * @throws TimeoutException if the given timeout elapses before
     *         any task successfully completes
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

接口ExecutorService总结：
1. ExecutorService 继承与Executor接口，也是将任务的提交和执行细节封装了起来，但是又进行了扩展。
2. 扩展提供了终止的方法：shutdown、shutdownNow、isShutdown、isTerminated、awaitTermination
    * shutdown：不再接受新任务，会等待已提交的任务都执行完，方法会立即返回。
    * shutdownNow：不再接受新任务，停止正在执行的任务和等待执行的任务，方法会立即放回，返回结果为等待执行的任务列表。
    * awaitTermination：shutdown后进行阻塞，直到超时或者所有任务执行完成。
3. 扩展了Executor.execute(Runnable)方法，返回了一个Future，Future可以取消任务、返回任务结果以及等待完成。
    * Future submit(task) ：提交一个任务（Callable、Runnable）并立即返回一个Future
    * List<Future> invokeAll(Collection tasks)：执行给定的多个任务，当所有任务完成时返回一个Future集合
    * T invokeAny(Collection tasks)：执行给定的多个任务，当其中一个任务完成就返回其Future。