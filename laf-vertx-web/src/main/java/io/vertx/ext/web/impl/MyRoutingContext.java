package io.vertx.ext.web.impl;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.impl.RouteImpl;
import io.vertx.ext.web.impl.RouterImpl;
import io.vertx.ext.web.impl.RoutingContextImpl;

import java.util.Set;

/**
 * 感知环境的路由上下文
 */
public class MyRoutingContext extends RoutingContextImpl {
    protected Environment environment;

    public MyRoutingContext(final String mountPoint, final RouterImpl router, final HttpServerRequest request,
                            final Set<RouteImpl> routes, final Environment environment) {
        super(mountPoint, router, request, routes);
        this.environment = environment;
    }

    @Override
    public <T> T get(final String key) {
        //允许从环境里面获取参数
        T result = super.get(key);
        if (result == null) {
            result = environment.getObject(key);
        }
        return result;
    }

}