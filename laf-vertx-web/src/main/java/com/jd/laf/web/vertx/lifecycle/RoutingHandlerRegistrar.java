package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.ROUTING;

/**
 * 路由处理器注册器
 */
public class RoutingHandlerRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        for (RoutingHandler handler : ROUTING.extensions()) {
            EnvironmentAware.setup(vertx, environment, handler);
        }
    }

}
