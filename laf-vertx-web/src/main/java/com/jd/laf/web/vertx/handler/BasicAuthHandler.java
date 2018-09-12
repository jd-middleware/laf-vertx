package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Environment.AUTH_PROVIDER;

/**
 * 基础认证处理器
 */
public class BasicAuthHandler implements RoutingHandler, EnvironmentAware {

    public static final String BASIC_AUTH = "basicAuth";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return BASIC_AUTH;
    }

    @Override
    public void setup(final Environment environment) {
        AuthProvider authProvider = environment.getObject(AUTH_PROVIDER, AuthProvider.class);
        handler = io.vertx.ext.web.handler.BasicAuthHandler.create(authProvider);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
