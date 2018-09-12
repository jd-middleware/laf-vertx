package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 响应时间头处理器
 */
public class ResponseTimeHandler implements RoutingHandler, EnvironmentAware {

    public static final String RESPONSE_TIME = "responseTime";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return RESPONSE_TIME;
    }

    @Override
    public void setup(final Environment environment) {
        handler = io.vertx.ext.web.handler.ResponseTimeHandler.create();
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
