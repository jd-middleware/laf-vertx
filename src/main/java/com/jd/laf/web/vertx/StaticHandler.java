package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * 静态资源
 */
public class StaticHandler implements RoutingHandler, ContextAware {

    public static final String STATIC = "static";
    protected volatile Handler<RoutingContext> handler;

    @Override
    public String type() {
        return STATIC;
    }

    @Override
    public void setup(final Map<String, Object> context) {
        io.vertx.ext.web.handler.StaticHandler target = io.vertx.ext.web.handler.StaticHandler.create();
        String webRoot = (String) context.get(WEB_ROOT);
        if (webRoot != null && !webRoot.isEmpty()) {
            target.setWebRoot(webRoot);
        }
        //设置默认页
        String indexPage = (String) context.get(INDEX_PAGE);
        if (indexPage != null && !indexPage.isEmpty()) {
            target.setIndexPage(indexPage);
        }
        handler = target;
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
