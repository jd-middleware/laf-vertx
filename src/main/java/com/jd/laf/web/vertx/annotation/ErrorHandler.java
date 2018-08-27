package com.jd.laf.web.vertx.annotation;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 异常处理器
 */
public interface ErrorHandler extends Handler<RoutingContext> {

    /**
     * 类型
     *
     * @return
     */
    String type();

}
