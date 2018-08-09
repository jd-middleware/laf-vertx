package com.jd.laf.web.vertx;

import com.jd.laf.extension.Extensible;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 消息处理器
 */
@Extensible("vertx.consumer")
public interface MessageHandler<T> extends Handler<Message<T>> {

}
