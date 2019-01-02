package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 异常处理器
 */
public interface ErrorHandler extends Handler<RoutingContext>, Type {

}
