package com.jd.laf.web.vertx;

/**
 * 系统上下文感知
 */
public interface EnvironmentAware {

    /**
     * 通过上下文来构建，单线程调用
     *
     * @param environment 上下文
     */
    void setup(Environment environment);
}