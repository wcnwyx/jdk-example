## 接口Executor
ThreadPoolExecutor的顶层接口，看文档注释。  
```java
/**
 * An object that executes submitted {@link Runnable} tasks. This
 * interface provides a way of decoupling task submission from the
 * mechanics of how each task will be run, including details of thread
 * use, scheduling, etc.  An {@code Executor} is normally used
 * instead of explicitly creating threads. For example, rather than
 * invoking {@code new Thread(new(RunnableTask())).start()} for each
 * of a set of tasks, you might use:
 * 
 * 一个执行提交Runnable任务的对象。这个接口提供了一种将任务提交与如何运行每个任务的机制分离的方法，包括线程使用、调度等细节。
 * 通常使用Executor，而不是显式地创建线程。例如：不会为了一组任务中的每一个任务调用new Thread(new(RunnableTask())).start()，
 * 你可以使用下面的方法：
 *
 * <pre>
 * Executor executor = <em>anExecutor</em>;
 * executor.execute(new RunnableTask1());
 * executor.execute(new RunnableTask2());
 * ...
 * </pre>
 *
 * However, the {@code Executor} interface does not strictly
 * require that execution be asynchronous. In the simplest case, an
 * executor can run the submitted task immediately in the caller's
 * thread:
 * 
 * 但是，Executor接口并不严格要求执行是异步的。最简单的情况下，执行器可以在调用者的线程中立即运行提交的任务。
 *
 *  <pre> {@code
 * class DirectExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     r.run();
 *   }
 * }}</pre>
 *
 * More typically, tasks are executed in some thread other
 * than the caller's thread.  The executor below spawns a new thread
 * for each task.
 * 更典型的是，任务在调用者以外的线程中执行。下面的执行器为每一个提交的任务生成一个新的线程。
 *
 *  <pre> {@code
 * class ThreadPerTaskExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     new Thread(r).start();
 *   }
 * }}</pre>
 *
 * Many {@code Executor} implementations impose some sort of
 * limitation on how and when tasks are scheduled.  The executor below
 * serializes the submission of tasks to a second executor,
 * illustrating a composite executor.
 * 许多Executor的实现对任务的调度方式和时间加了某种限制。
 * 下面的executor将提交的任务序列化到第二个executor，解释了一个组合的executor。
 *
 *  <pre> {@code
 * class SerialExecutor implements Executor {
 *   final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
 *   final Executor executor;
 *   Runnable active;
 *
 *   SerialExecutor(Executor executor) {
 *     this.executor = executor;
 *   }
 *
 *   public synchronized void execute(final Runnable r) {
 *     tasks.offer(new Runnable() {
 *       public void run() {
 *         try {
 *           r.run();
 *         } finally {
 *           scheduleNext();
 *         }
 *       }
 *     });
 *     if (active == null) {
 *       scheduleNext();
 *     }
 *   }
 *
 *   protected synchronized void scheduleNext() {
 *     if ((active = tasks.poll()) != null) {
 *       executor.execute(active);
 *     }
 *   }
 * }}</pre>
 *
 * The {@code Executor} implementations provided in this package
 * implement {@link ExecutorService}, which is a more extensive
 * interface.  The {@link ThreadPoolExecutor} class provides an
 * extensible thread pool implementation. The {@link Executors} class
 * provides convenient factory methods for these Executors.
 * 这个包中提供的Executor的实现，其实现了ExecutorService，这是一个更广泛的接口。
 * ThreadPoolExecutor类提供了一个可扩展的线程池实现。Executors类为这些执行器提供了方便的工厂方法。
 *
 * <p>Memory consistency effects: Actions in a thread prior to
 * submitting a {@code Runnable} object to an {@code Executor}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * its execution begins, perhaps in another thread.
 * 内存一致性影响：在将可运行对象提交给执行器之前，线程中的操作发生在其执行开始之前，可能发生在另一个线程中。
 * 查看：https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     * 在将来的某个时间执行给定的命令。命令可以在一个新线程、一个线程池或者调用者的线程中执行，
     * 具体由Executor的实现来决定
     * @param command the runnable task 可运行的任务
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution 如果无法接受此任务执行
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```
Executor接口总结：就是将任务的提交和执行细节给封装起来了，上层不需要知道了解。
常见的执行方式有以下几种：
1. 直接在调用者的线程中执行。（没啥意义，还不如调用者线程直接调用呢）
2. 每一个任务都在一个新的线程中执行。
3. 任务在线程池中执行。ThreadPoolExecutor就是这种实现