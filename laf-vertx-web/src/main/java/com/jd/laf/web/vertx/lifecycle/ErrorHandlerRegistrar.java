package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.ErrorHandler;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.ERROR;

/**
 * 异常处理器注册器
 */
public class ErrorHandlerRegistrar implements Registrar {

    public static final int ERROR_HANDLER_ORDER = RenderRegistrar.RENDER_ORDER + 1;

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        for (ErrorHandler handler : ERROR.extensions()) {
            EnvironmentAware.setup(vertx, environment, handler);
        }
    }

    @Override
    public int order() {
        //在渲染后执行，因为里面用到了渲染插件
        return ERROR_HANDLER_ORDER;
    }
}
