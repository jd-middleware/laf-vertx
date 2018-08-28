package com.jd.laf.web.vertx;

import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * 上下文参数
 */
public class ContextHandler implements RoutingHandler, ContextAware {

    private Map<String, Object> parameters;

    @Override
    public void setup(final Map<String, Object> context) {
        this.parameters = context;
    }

    @Override
    public String type() {
        return "context";
    }

    @Override
    public void handle(final RoutingContext context) {
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        context.next();
    }
}