package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 */
public interface RoutingHandler extends Handler<RoutingContext> {

    /**
     * 类型
     *
     * @return
     */
    String type();

}
