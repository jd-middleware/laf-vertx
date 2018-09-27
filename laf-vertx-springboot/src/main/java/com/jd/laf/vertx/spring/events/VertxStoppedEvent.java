package com.jd.laf.vertx.spring.events;

import io.vertx.core.Vertx;


public final class VertxStoppedEvent extends AbstractVertxEvent {

    public VertxStoppedEvent(Vertx vertx) {
        super(vertx);
    }
}
