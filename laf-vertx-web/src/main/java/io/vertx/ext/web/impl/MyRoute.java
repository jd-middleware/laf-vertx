package io.vertx.ext.web.impl;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.config.RouteConfig;
import io.vertx.core.http.HttpMethod;

/**
 * 对配置感知的路由
 */
public class MyRoute extends RouteImpl {

    protected RouteConfig config;

    public MyRoute(RouterImpl router, int order) {
        super(router, order);
    }

    public MyRoute(RouterImpl router, int order, HttpMethod method, String path) {
        super(router, order, method, path);
    }

    public MyRoute(RouterImpl router, int order, String path) {
        super(router, order, path);
    }

    public MyRoute(RouterImpl router, int order, HttpMethod method, String regex, boolean bregex) {
        super(router, order, method, regex, bregex);
    }

    public MyRoute(RouterImpl router, int order, String regex, boolean bregex) {
        super(router, order, regex, bregex);
    }

    @Override
    protected synchronized void handleContext(final RoutingContextImplBase context) {
        //根据路由配置设置上下文
        if (context.currentRouteNextHandlerIndex() == 1 && config.getTemplate() != null && !config.getTemplate().isEmpty()) {
            context.put(Environment.TEMPLATE, config.getTemplate());
        }
        super.handleContext(context);
    }

    public void setConfig(RouteConfig config) {
        this.config = config;
    }
}
