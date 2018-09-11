package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.RoutingHandlers;
import com.jd.laf.web.vertx.SystemContext;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 路由处理器注册器
 */
public class RoutingRegister implements Register {

    @Override
    public void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        for (Map.Entry<String, RoutingHandler> entry : RoutingHandlers.getPlugins().entrySet()) {
            initializer.accept(entry.getValue());
        }
    }

    @Override
    public int order() {
        return 2;
    }
}
