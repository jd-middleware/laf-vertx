package com.jd.laf.web.vertx;

import com.jd.laf.web.vertx.config.RouteConfig;

/**
 * 路由配置感知
 */
public interface RouteAware<T extends RoutingHandler> extends Cloneable {

    /**
     * 根据路由配置进行设置
     *
     * @param config 配置
     */
    void setup(RouteConfig config);

    /**
     * 创建一份新对象
     *
     * @return
     */
    T clone();

}
