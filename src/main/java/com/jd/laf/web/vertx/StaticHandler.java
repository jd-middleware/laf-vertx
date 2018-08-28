package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 静态资源
 */
public class StaticHandler implements RoutingHandler {

    protected volatile Handler<RoutingContext> handler;

    @Override
    public String type() {
        return "static";
    }

    @Override
    public void handle(final RoutingContext context) {
        if (handler == null) {
            synchronized (this) {
                if (handler == null) {
                    Handler<RoutingContext> target;
                    String webRoot = context.get(WEB_ROOT);
                    if (webRoot == null || webRoot.isEmpty()) {
                        target = io.vertx.ext.web.handler.StaticHandler.create();
                    } else {
                        target = io.vertx.ext.web.handler.StaticHandler.create(webRoot);
                    }
                    handler = target;
                }
            }
        }
        handler.handle(context);
    }
}
