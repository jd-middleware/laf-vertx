package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.message.CustomCodecs;
import io.vertx.core.Vertx;

/**
 * 自定义编解码注册器
 */
public class CodecRegister implements Register {

    @Override
    public void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        CustomCodecs.getPlugins().forEach(o -> vertx.eventBus().registerDefaultCodec(o.type(), o));
    }

    @Override
    public void deregister(final Vertx vertx) {
        CustomCodecs.getPlugins().forEach(o -> vertx.eventBus().unregisterDefaultCodec(o.type()));
    }

    @Override
    public int order() {
        return 0;
    }
}
