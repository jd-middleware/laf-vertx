package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 */
public interface RoutingHandler extends Handler<RoutingContext>, Type {

}
