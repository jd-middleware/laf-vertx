package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.MessageHandler;
import com.jd.laf.web.vertx.MessageHandlers;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 消息处理器注册器
 */
public class MessageHandlerRegistrar implements Registrar {

    @Override
    public void register(final Environment environment) throws Exception {
        for (Map.Entry<String, MessageHandler> entry : MessageHandlers.getPlugins().entrySet()) {
            environment.setup(entry.getValue());
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
    }

    @Override
    public int order() {
        return HANDLER;
    }
}
