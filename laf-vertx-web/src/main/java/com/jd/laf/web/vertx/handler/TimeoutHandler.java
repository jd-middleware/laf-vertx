package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RouteAware;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.config.RouteConfig;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.ext.web.handler.TimeoutHandler.DEFAULT_TIMEOUT;

/**
 * 超时处理器
 */
public class TimeoutHandler implements RoutingHandler, RouteAware<TimeoutHandler> {

    public static final String TIMEOUT = "timeout";
    protected RouteConfig config;
    protected io.vertx.ext.web.handler.TimeoutHandler handler;

    @Override
    public void setup(final RouteConfig config) {
        this.config = config;
        if (config != null) {
            handler = io.vertx.ext.web.handler.TimeoutHandler.create(
                    config.getTimeout() == null || config.getTimeout() <= 0 ? DEFAULT_TIMEOUT : config.getTimeout());
        }
    }

    @Override
    public TimeoutHandler clone() {
        try {
            return (TimeoutHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String type() {
        return TIMEOUT;
    }

    @Override
    public void handle(final RoutingContext context) {
        //系统环境变量
        if (handler != null) {
            handler.handle(context);
        } else {
            context.next();
        }
    }

}