package com.jd.laf.web.vertx.message;

import com.jd.laf.extension.Type;

/**
 * 自定义消息编解码
 *
 * @param <T>
 */
public interface CustomCodec<T> extends io.vertx.core.eventbus.MessageCodec<T, T>, Type<Class<T>> {

    @Override
    Class<T> type();

    @Override
    default String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    default byte systemCodecID() {
        return -1;
    }
}
