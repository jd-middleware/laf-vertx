package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Context;
import com.jd.laf.web.vertx.ContextAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Context.*;
import static io.vertx.ext.web.handler.RedirectAuthHandler.DEFAULT_LOGIN_REDIRECT_URL;
import static io.vertx.ext.web.handler.RedirectAuthHandler.DEFAULT_RETURN_URL_PARAM;

/**
 * 重定向认证处理器资源
 */
public class RedirectAuthHandler implements RoutingHandler, ContextAware {

    public static final String REDIRECT_AUTH = "redirectAuth";

    protected volatile Handler<RoutingContext> handler;

    @Override
    public String type() {
        return REDIRECT_AUTH;
    }

    @Override
    public void setup(final Context context) {
        AuthProvider authProvider = context.getObject(AUTH_PROVIDER, AuthProvider.class);
        String redirectUrl = context.getString(AUTH_REDIRECT_URL, DEFAULT_LOGIN_REDIRECT_URL);
        String returnUrlParam = context.getString(AUTH_RETURN_URL_PARAM, DEFAULT_RETURN_URL_PARAM);

        handler = io.vertx.ext.web.handler.RedirectAuthHandler.create(authProvider, redirectUrl, returnUrlParam);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
