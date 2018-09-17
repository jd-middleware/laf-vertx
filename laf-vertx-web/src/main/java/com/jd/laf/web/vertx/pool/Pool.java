package com.jd.laf.web.vertx.pool;

/**
 * 对象池接口
 *
 * @param <T>
 */
public interface Pool<T> {

    /**
     * 获取
     *
     * @return
     */
    T get();

    /**
     * 释放
     *
     * @param t
     */
    void release(T t);

    /**
     * 最大容量
     *
     * @return
     */
    int capacity();

}
