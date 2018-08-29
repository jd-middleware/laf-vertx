package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.SystemAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.SystemContext.AUTH_PROVIDER;

/**
 * 用户会话处理器资源
 */
public class UserSessionHandler implements RoutingHandler, SystemAware {

    public static final String USER_SESSION = "userSession";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return USER_SESSION;
    }

    @Override
    public void setup(final SystemContext context) {
        AuthProvider authProvider = context.getObject(AUTH_PROVIDER, AuthProvider.class);
        handler = io.vertx.ext.web.handler.UserSessionHandler.create(authProvider);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
