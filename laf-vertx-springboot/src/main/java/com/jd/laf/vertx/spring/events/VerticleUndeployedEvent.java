package com.jd.laf.vertx.spring.events;

import io.vertx.core.Context;
import io.vertx.core.Verticle;

/**
 * 下线事件
 */
public final class VerticleUndeployedEvent extends AbstractVerticleEvent {

    public VerticleUndeployedEvent(Verticle verticle, Context context) {
        super(verticle, context);
    }
}
