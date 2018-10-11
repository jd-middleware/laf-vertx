package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.message.CustomCodecs;
import io.vertx.core.Vertx;

/**
 * 自定义编解码注册器
 */
public class CodecRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        //部署多个实例冲突
        CustomCodecs.getPlugins().forEach(o -> vertx.eventBus().registerDefaultCodec(o.type(), o));
    }

    @Override
    public int order() {
        return HANDLER + 1;
    }
}
