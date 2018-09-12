package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

import static com.jd.laf.web.vertx.Environment.*;
import static io.vertx.ext.web.handler.SessionHandler.DEFAULT_SESSION_COOKIE_NAME;
import static io.vertx.ext.web.handler.SessionHandler.DEFAULT_SESSION_TIMEOUT;

/**
 * 会话处理器资源
 */
public class SessionHandler implements RoutingHandler, EnvironmentAware {

    public static final String DEFAULT_SESSION_NAME = "vertx-web.sessions";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return "session";
    }

    @Override
    public void setup(final Environment environment) {
        boolean localSession = environment.getBoolean(SESSION_LOCAL, false);
        String sessionName = environment.getString(SESSION_NAME, DEFAULT_SESSION_NAME);
        String sessionCookieName = environment.getString(SESSION_COOKIE_NAME, DEFAULT_SESSION_COOKIE_NAME);
        long sessionTimeout = environment.getPositive(SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT);
        SessionStore store = localSession ? LocalSessionStore.create(environment.getVertx(), sessionName) :
                ClusteredSessionStore.create(environment.getVertx(), sessionName);
        handler = io.vertx.ext.web.handler.SessionHandler.create(store).
                setSessionCookieName(sessionCookieName).
                setSessionTimeout(sessionTimeout);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
