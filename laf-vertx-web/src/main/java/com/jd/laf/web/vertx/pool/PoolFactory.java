package com.jd.laf.web.vertx.pool;

import java.util.List;

/**
 * 对象池构造器
 *
 * @param <T>
 */
public interface PoolFactory<T> {

    /**
     * 创建一个对象池
     *
     * @param capacity 池大小
     * @return
     */
    Pool<T> create(int capacity);

    /**
     * 创建对象池，并进行初始化
     *
     * @param capacity    池大小
     * @param objects 初始化大小
     * @return
     */
    Pool<T> create(int capacity, List<T> objects);

}
