package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * Body处理器
 */
public class BodyHandler implements RoutingHandler, ContextAware {

    public static final String BODY = "body";
    protected volatile Handler<RoutingContext> handler;

    @Override
    public String type() {
        return BODY;
    }

    @Override
    public void setup(final Map<String, Object> context) {
        io.vertx.ext.web.handler.BodyHandler target = io.vertx.ext.web.handler.BodyHandler.create();
        String uploadDir = (String) context.get(UPLOAD_DIR);
        if (uploadDir != null && !uploadDir.isEmpty()) {
            target.setUploadsDirectory(uploadDir);
        }
        //设置默认页
        Number bodyLimit = (Number) context.get(BODY_LIMIT);
        if (bodyLimit != null) {
            target.setBodyLimit(bodyLimit.longValue());
        }
        handler = target;
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
