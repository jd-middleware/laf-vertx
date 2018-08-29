package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RouteAware;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.SystemAware;
import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.config.RouteConfig;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.SystemContext.TEMPLATE;

/**
 * 上下文参数
 */
public class ContextHandler implements RoutingHandler, SystemAware, RouteAware<ContextHandler> {

    public static final String CONTEXT = "context";
    protected SystemContext systemContext;
    protected RouteConfig config;

    @Override
    public void setup(final SystemContext context) {
        this.systemContext = context;
    }

    @Override
    public void setup(final RouteConfig config) {
        this.config = config;
    }

    @Override
    public ContextHandler create() {
        ContextHandler result = new ContextHandler();
        result.setup(systemContext);
        return result;
    }

    @Override
    public String type() {
        return CONTEXT;
    }

    @Override
    public void handle(final RoutingContext context) {
        //系统环境变量
        systemContext.foreach((a, b) -> context.put(a, b));
        //模板信息
        if (config != null && config.getTemplate() != null && !config.getTemplate().isEmpty()) {
            context.put(TEMPLATE, config.getTemplate());
        }
        context.next();
    }

}