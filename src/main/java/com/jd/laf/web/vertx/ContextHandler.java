package com.jd.laf.web.vertx;

import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * 上下文参数
 */
public class ContextHandler implements RoutingHandler, ContextAware {

    public static final String CONTEXT = "context";
    private Map<String, Object> parameters;

    @Override
    public void setup(final Context context) {
        this.parameters = context.parameters;
    }

    @Override
    public String type() {
        return CONTEXT;
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