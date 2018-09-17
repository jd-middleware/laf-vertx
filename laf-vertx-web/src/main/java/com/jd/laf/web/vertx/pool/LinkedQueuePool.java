package com.jd.laf.web.vertx.pool;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于LinkedBlockQueue实现的对象池
 *
 * @param <T>
 */
public class LinkedQueuePool<T> implements Pool<T> {

    protected LinkedBlockingQueue<T> queue;

    protected int capacity;

    public LinkedQueuePool(LinkedBlockingQueue<T> queue, int capacity) {
        this.queue = queue;
        this.capacity = capacity;
    }

    @Override
    public T get() {
        return queue.poll();
    }

    @Override
    public void release(T t) {
        if (t != null) {
            if (t instanceof Cleanable) {
                ((Cleanable) t).clean();
            }
            queue.offer(t);
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
