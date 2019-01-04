package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.config.VertxConfig;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.ERROR;

/**
 * 异常处理器注册器
 */
public class ErrorRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment, final VertxConfig config) throws Exception {
        EnvironmentAware.setup(vertx, environment, ERROR.extensions());
    }

    @Override
    public int order() {
        //在渲染后执行，因为里面用到了渲染插件
        return ERROR_ORDER;
    }
}
