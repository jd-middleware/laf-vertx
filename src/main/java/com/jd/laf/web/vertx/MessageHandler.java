package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 消息处理器
 */
public interface MessageHandler<T> extends Handler<Message<T>> {

    /**
     * 类型
     *
     * @return
     */
    String type();

}
