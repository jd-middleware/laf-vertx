package org.unbrokendome.vertx.spring;

/**
 * Vertx配置器
 */
public interface VertxConfigurer {
    /**
     * 配置构造器
     *
     * @param builder
     */
    void configure(SpringVertx.Builder builder);
}
