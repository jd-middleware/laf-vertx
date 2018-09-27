package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Environment.BODY_LIMIT;
import static com.jd.laf.web.vertx.Environment.UPLOAD_DIR;

/**
 * Body处理器
 */
public class BodyHandler implements RoutingHandler, EnvironmentAware {

    public static final String BODY = "body";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return BODY;
    }

    @Override
    public void setup(final Vertx vertx, final Environment environment) {
        io.vertx.ext.web.handler.BodyHandler target = io.vertx.ext.web.handler.BodyHandler.create();
        String uploadDir = environment.getString(UPLOAD_DIR);
        if (uploadDir != null && !uploadDir.isEmpty()) {
            target.setUploadsDirectory(uploadDir);
        }
        target.setBodyLimit(environment.getLong(BODY_LIMIT, -1L));
        handler = target;
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
