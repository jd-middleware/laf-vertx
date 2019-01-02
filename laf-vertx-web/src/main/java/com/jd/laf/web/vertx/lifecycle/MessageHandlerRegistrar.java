package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.MessageHandler;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.MESSAGE;

/**
 * 消息处理器注册器
 */
public class MessageHandlerRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        //加载扩展点
        for (MessageHandler handler : MESSAGE.extensions()) {
            EnvironmentAware.setup(vertx, environment, handler);
        }
    }
}
