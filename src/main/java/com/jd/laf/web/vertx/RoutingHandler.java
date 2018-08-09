package com.jd.laf.web.vertx;

import com.jd.laf.extension.Extensible;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 */
@Extensible("vertx.routing")
public interface RoutingHandler extends Handler<RoutingContext> {

}
