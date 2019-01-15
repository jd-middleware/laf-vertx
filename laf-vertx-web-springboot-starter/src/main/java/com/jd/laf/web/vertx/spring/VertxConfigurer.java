package com.jd.laf.web.vertx.spring;

import io.vertx.core.VertxOptions;

/**
 * Vertx配置器
 */
public interface VertxConfigurer {
    /**
     * 配置构造器
     *
     * @param options
     */
    void configure(VertxOptions options);
}
