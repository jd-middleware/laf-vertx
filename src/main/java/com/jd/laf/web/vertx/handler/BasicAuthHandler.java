package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Context;
import com.jd.laf.web.vertx.ContextAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Context.AUTH_PROVIDER;

/**
 * 基础认证处理器
 */
public class BasicAuthHandler implements RoutingHandler, ContextAware {

    public static final String BASIC_AUTH = "basicAuth";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return BASIC_AUTH;
    }

    @Override
    public void setup(final Context context) {
        AuthProvider authProvider = context.getObject(AUTH_PROVIDER, AuthProvider.class);
        handler = io.vertx.ext.web.handler.BasicAuthHandler.create(authProvider);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
