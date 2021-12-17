package com.wcn.jdk.example.collection.list;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueTest<E> {
    private transient volatile ConcurrentLinkedQueueTest.Node<E> head;
    private transient volatile ConcurrentLinkedQueueTest.Node<E> tail;

    public ConcurrentLinkedQueueTest(){
        head = tail = new Node<>(null);
    }
    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueueTest<Integer> queue = new ConcurrentLinkedQueueTest();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                queue.offer(11);
                System.out.println(Thread.currentThread().getName()+" offer success");
            }
        }, "thread1");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                queue.offer(21);
                queue.offer(22);
//                queue.offer(23);
//                queue.offer(24);
                System.out.println("pool:"+queue.poll());
                System.out.println("pool:"+queue.poll());
                System.out.println("pool:"+queue.poll());
                System.out.println(Thread.currentThread().getName()+" offer success");
            }
        }, "thread2");
        thread1.start();
        Thread.sleep(500);
        thread2.start();
    }

    public boolean offer(E e) {
        final ConcurrentLinkedQueueTest.Node<E> newNode = new ConcurrentLinkedQueueTest.Node<E>(e);

        for (ConcurrentLinkedQueueTest.Node<E> t = tail, p = t;;) {
            ConcurrentLinkedQueueTest.Node<E> q = p.next;
            if (q == null) {
                // p is last node
                if(Thread.currentThread().getName().equals("thread1")){
                    try {
                        Thread.sleep(2000);
                        System.out.println();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (p.casNext(null, newNode)) {
                    // Successful CAS is the linearization point
                    // for e to become an element of this queue,
                    // and for newNode to become "live".
                    if (p != t) // hop two nodes at a time
                        casTail(t, newNode);  // Failure is OK.
                    return true;
                }
                // Lost CAS race to another thread; re-read next
            }
            else if (p == q)
                // We have fallen off list.  If tail is unchanged, it
                // will also be off-list, in which case we need to
                // jump to head, from which all live nodes are always
                // reachable.  Else the new tail is a better bet.
                p = (t != (t = tail)) ? t : head;
            else
                // Check for tail updates after two hops.
                p = (p != t && t != (t = tail)) ? t : q;
        }
    }

    public E poll() {
        restartFromHead:
        for (;;) {
            for (ConcurrentLinkedQueueTest.Node<E> h = head, p = h, q;;) {
                E item = p.item;

                if (item != null && p.casItem(item, null)) {
                    // Successful CAS is the linearization point
                    // for item to be removed from this queue.
                    if (p != h) // hop two nodes at a time
                        updateHead(h, ((q = p.next) != null) ? q : p);
                    return item;
                }
                else if ((q = p.next) == null) {
                    updateHead(h, p);
                    return null;
                }
                else if (p == q)
                    continue restartFromHead;
                else
                    p = q;
            }
        }
    }

    private static class Node<E> {
        volatile E item;
        volatile ConcurrentLinkedQueueTest.Node<E> next;

        /**
         * Constructs a new node.  Uses relaxed write because item can
         * only be seen after publication via casNext.
         */
        Node(E item) {
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(ConcurrentLinkedQueueTest.Node<E> val) {
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(ConcurrentLinkedQueueTest.Node<E> cmp, ConcurrentLinkedQueueTest.Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        // Unsafe mechanics

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        static {
            try {
//                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                UNSAFE = (Unsafe) f.get(null);
                Class<?> k = ConcurrentLinkedQueueTest.Node.class;
                itemOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /**
     * Tries to CAS head to p. If successful, repoint old head to itself
     * as sentinel for succ(), below.
     */
    final void updateHead(ConcurrentLinkedQueueTest.Node<E> h, ConcurrentLinkedQueueTest.Node<E> p) {
        if (h != p && casHead(h, p))
            h.lazySetNext(h);
    }

    private boolean casTail(ConcurrentLinkedQueueTest.Node<E> cmp, ConcurrentLinkedQueueTest.Node<E> val) {
        System.out.println(Thread.currentThread().getName()+" casTail: "+val.item);
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private boolean casHead(ConcurrentLinkedQueueTest.Node<E> cmp, ConcurrentLinkedQueueTest.Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

    // Unsafe mechanics

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    static {
        try {
//            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);

            Class<?> k = ConcurrentLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
