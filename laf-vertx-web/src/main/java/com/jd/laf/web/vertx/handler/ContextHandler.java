package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RouteAware;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.config.RouteConfig;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Environment.TEMPLATE;

/**
 * 上下文参数
 */
public class ContextHandler implements RoutingHandler, EnvironmentAware, RouteAware<ContextHandler> {

    public static final String CONTEXT = "context";
    protected Environment environment;
    protected RouteConfig config;

    @Override
    public void setup(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setup(final RouteConfig config) {
        this.config = config;
    }

    @Override
    public ContextHandler create() {
        ContextHandler result = new ContextHandler();
        result.setup(environment);
        return result;
    }

    @Override
    public String type() {
        return CONTEXT;
    }

    @Override
    public void handle(final RoutingContext context) {
        //系统环境变量
        environment.foreach((a, b) -> context.put(a, b));
        //模板信息
        if (config != null && config.getTemplate() != null && !config.getTemplate().isEmpty()) {
            context.put(TEMPLATE, config.getTemplate());
        }
        context.next();
    }

}