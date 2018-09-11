package com.jd.laf.web.vertx.lifecycle;

/**
 * 初始化
 *
 * @param <T>
 */
@FunctionalInterface
public interface Initializer<T> {

    /**
     * 初始化
     *
     * @param obj
     * @throws Exception
     */
    void accept(T obj) throws Exception;
}
