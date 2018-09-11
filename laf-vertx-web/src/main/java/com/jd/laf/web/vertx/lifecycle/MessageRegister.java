package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.MessageHandler;
import com.jd.laf.web.vertx.MessageHandlers;
import com.jd.laf.web.vertx.SystemContext;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 消息处理器注册器
 */
public class MessageRegister implements Register {

    @Override
    public void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        for (Map.Entry<String, MessageHandler> entry : MessageHandlers.getPlugins().entrySet()) {
            initializer.accept(entry.getValue());
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
    }

    @Override
    public int order() {
        return 3;
    }
}
