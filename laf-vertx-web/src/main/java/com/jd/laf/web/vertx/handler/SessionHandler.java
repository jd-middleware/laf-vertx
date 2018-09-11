package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.SystemAware;
import com.jd.laf.web.vertx.SystemContext;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

import static com.jd.laf.web.vertx.SystemContext.*;
import static io.vertx.ext.web.handler.SessionHandler.DEFAULT_SESSION_COOKIE_NAME;
import static io.vertx.ext.web.handler.SessionHandler.DEFAULT_SESSION_TIMEOUT;

/**
 * 会话处理器资源
 */
public class SessionHandler implements RoutingHandler, SystemAware {

    public static final String DEFAULT_SESSION_NAME = "vertx-web.sessions";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return "session";
    }

    @Override
    public void setup(final SystemContext context) {
        boolean localSession = context.getBoolean(SESSION_LOCAL, false);
        String sessionName = context.getString(SESSION_NAME, DEFAULT_SESSION_NAME);
        String sessionCookieName = context.getString(SESSION_COOKIE_NAME, DEFAULT_SESSION_COOKIE_NAME);
        long sessionTimeout = context.getPositive(SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT);
        SessionStore store = localSession ? LocalSessionStore.create(context.getVertx(), sessionName) :
                ClusteredSessionStore.create(context.getVertx(), sessionName);
        handler = io.vertx.ext.web.handler.SessionHandler.create(store).
                setSessionCookieName(sessionCookieName).
                setSessionTimeout(sessionTimeout);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
