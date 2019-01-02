package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 消息处理器
 */
public interface MessageHandler<T> extends Handler<Message<T>>, Type {


}
