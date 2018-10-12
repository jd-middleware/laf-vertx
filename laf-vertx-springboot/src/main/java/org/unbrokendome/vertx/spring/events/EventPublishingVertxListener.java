package org.unbrokendome.vertx.spring.events;

import org.unbrokendome.vertx.spring.VertxListener;
import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.function.Supplier;

/**
 * Vertx事件广播到应用事件总线
 */
public final class EventPublishingVertxListener implements VertxListener, ApplicationEventPublisherAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void vertxStarted(final Vertx vertx, final VertxOptions options) {
        publish(() -> new VertxStartedEvent(vertx, options));
    }

    @Override
    public void vertxStopped(final Vertx vertx) {
        publish(() -> new VertxStoppedEvent(vertx));
    }

    @Override
    public void verticleDeployed(final Verticle verticle, final Context context) {
        publish(() -> new VerticleDeployedEvent(verticle, context));
    }

    @Override
    public void verticleUndeployed(final Verticle verticle, final Context context) {
        publish(() -> new VerticleUndeployedEvent(verticle, context));
    }

    protected <T extends ApplicationEvent> void publish(final Supplier<T> supplier) {
        if (applicationEventPublisher != null) {
            T event = supplier.get();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Publishing %s with source %s", event.getClass().getSimpleName(), event.getSource()));
            }
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
