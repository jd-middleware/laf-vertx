package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

import static com.jd.laf.web.vertx.Context.SESSION_LOCAL;
import static com.jd.laf.web.vertx.Context.SESSION_NAME;

/**
 * 会话处理器资源
 */
public class SessionHandler implements RoutingHandler, ContextAware {

    public static final String SESSION = "session";
    protected volatile Handler<RoutingContext> handler;

    @Override
    public String type() {
        return SESSION;
    }

    @Override
    public void setup(final Context context) {
        boolean localSession = context.getBoolean(SESSION_LOCAL, false);
        String session = context.getString(SESSION_NAME);
        SessionStore store = localSession ? (session == null || session.isEmpty() ?
                LocalSessionStore.create(context.getVertx()) : LocalSessionStore.create(context.getVertx(), session)) :
                (session == null || session.isEmpty() ? ClusteredSessionStore.create(context.getVertx())
                        : ClusteredSessionStore.create(context.getVertx(), session));
        handler = io.vertx.ext.web.handler.SessionHandler.create(store);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
