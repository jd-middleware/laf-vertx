package com.jd.laf.vertx.spring.events;

import io.vertx.core.Context;
import io.vertx.core.Verticle;

/**
 * 执行器部署事件
 */
public final class VerticleDeployedEvent extends AbstractVerticleEvent {

    public VerticleDeployedEvent(Verticle verticle, Context context) {
        super(verticle, context);
    }
}
