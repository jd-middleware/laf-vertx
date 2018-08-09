package com.jd.laf.web.vertx;

import com.jd.laf.extension.Extensible;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 异常处理器
 */
@Extensible("vertx.error")
public interface ErrorHandler extends Handler<RoutingContext> {

}
