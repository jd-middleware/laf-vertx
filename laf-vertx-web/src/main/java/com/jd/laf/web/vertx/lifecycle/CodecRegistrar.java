package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.CODEC;

/**
 * 自定义编解码注册器
 */
public class CodecRegistrar implements Registrar {

    public static final int CODEC_ORDER = ORDER - 1;

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        //部署多个实例冲突
        CODEC.extensions().forEach(o -> vertx.eventBus().registerDefaultCodec(o.type(), o));
    }

    @Override
    public int order() {
        return CODEC_ORDER;
    }
}
