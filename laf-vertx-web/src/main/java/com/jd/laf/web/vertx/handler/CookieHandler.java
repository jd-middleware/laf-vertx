package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.CookieHandlerImpl;

/**
 * Cookie处理器
 */
public class CookieHandler extends CookieHandlerImpl implements RoutingHandler, EnvironmentAware {

    public static final String COOKIE = "cookie";

    @Override
    public String type() {
        return COOKIE;
    }

    @Override
    public void setup(final Environment environment) {
    }

}
