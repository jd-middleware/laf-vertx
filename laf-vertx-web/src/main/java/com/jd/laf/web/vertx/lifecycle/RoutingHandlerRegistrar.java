package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.RoutingHandlers;

import java.util.Map;

/**
 * 路由处理器注册器
 */
public class RoutingHandlerRegistrar implements Registrar {

    @Override
    public void register(final Environment environment) throws Exception {
        for (Map.Entry<String, RoutingHandler> entry : RoutingHandlers.getPlugins().entrySet()) {
            environment.setup(entry.getValue());
        }
    }

    @Override
    public int order() {
        return HANDLER;
    }
}
