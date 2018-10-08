package org.unbrokendome.vertx.spring.events;

import io.vertx.core.Vertx;

/**
 * 停止事件
 */
public final class VertxStoppedEvent extends AbstractVertxEvent {

    public VertxStoppedEvent(Vertx vertx) {
        super(vertx);
    }
}
