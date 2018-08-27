package com.jd.laf.web.vertx;

import io.vertx.ext.web.RoutingContext;

/**
 * 静态资源
 */
public class StaticHandler implements RoutingHandler {

    protected static io.vertx.ext.web.handler.StaticHandler delegate = io.vertx.ext.web.handler.StaticHandler.create();

    @Override
    public String type() {
        return "static";
    }

    @Override
    public void handle(final RoutingContext context) {
        delegate.handle(context);
    }
}
