package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.message.CustomCodecs;
import io.vertx.core.Vertx;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义编解码注册器
 */
public class CodecRegistrar implements Registrar {

    protected static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        if (counter.incrementAndGet() == 1) {
            CustomCodecs.getPlugins().forEach(o -> vertx.eventBus().registerDefaultCodec(o.type(), o));
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
        if (counter.decrementAndGet() == 0) {
            CustomCodecs.getPlugins().forEach(o -> vertx.eventBus().unregisterDefaultCodec(o.type()));
        }
    }

    @Override
    public int order() {
        return HANDLER + 1;
    }
}
