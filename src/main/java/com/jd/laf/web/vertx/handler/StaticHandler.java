package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.SystemAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.SystemContext.INDEX_PAGE;
import static com.jd.laf.web.vertx.SystemContext.WEB_ROOT;

/**
 * 静态资源
 */
public class StaticHandler implements RoutingHandler, SystemAware {

    public static final String STATIC = "static";
    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return STATIC;
    }

    @Override
    public void setup(final SystemContext context) {
        io.vertx.ext.web.handler.StaticHandler target = io.vertx.ext.web.handler.StaticHandler.create();
        String webRoot = context.getString(WEB_ROOT);
        if (webRoot != null && !webRoot.isEmpty()) {
            target.setWebRoot(webRoot);
        }
        //设置默认页
        String indexPage = context.getString(INDEX_PAGE);
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
