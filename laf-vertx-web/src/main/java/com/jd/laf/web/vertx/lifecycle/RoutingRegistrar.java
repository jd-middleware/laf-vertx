package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.config.VertxConfig;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.ROUTING;

/**
 * 路由处理器注册器
 */
public class RoutingRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment, final VertxConfig config) throws Exception {
        EnvironmentAware.setup(vertx, environment, ROUTING.extensions());
    }

    @Override
    public int order() {
        return ROUTING_ORDER;
    }
}
