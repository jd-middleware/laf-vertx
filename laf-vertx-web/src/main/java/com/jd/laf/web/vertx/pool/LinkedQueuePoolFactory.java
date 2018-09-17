package com.jd.laf.web.vertx.pool;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于LinkedBlockingQueue实现的对象池构造器
 *
 * @param <T>
 */
public class LinkedQueuePoolFactory<T> implements PoolFactory<T> {

    @Override
    public Pool<T> create(final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }
        return new LinkedQueuePool<>(new LinkedBlockingQueue<>(capacity), capacity);
    }

    @Override
    public Pool<T> create(final int capacity, final List<T> objects) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }
        LinkedBlockingQueue queue = new LinkedBlockingQueue(capacity);
        int count = 0;
        if (objects != null) {
            for (Object obj : objects) {
                if (obj != null && queue.offer(obj)) {
                    if (++count >= capacity) {
                        break;
                    }
                }
            }
        }

        return new LinkedQueuePool<>(queue, capacity);
    }
}
