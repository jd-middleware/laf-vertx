package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.CookieHandlerImpl;

/**
 * Cookie处理器
 */
public class CookieHandler extends CookieHandlerImpl implements RoutingHandler, EnvironmentAware {

    public static final String COOKIE = "cookie";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return COOKIE;
    }

    @Override
    public void setup(final Vertx vertx, final Environment environment) {
        handler = io.vertx.ext.web.handler.CookieHandler.create();
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
