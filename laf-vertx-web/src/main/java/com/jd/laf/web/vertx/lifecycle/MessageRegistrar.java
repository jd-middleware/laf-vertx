package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.MessageHandler;
import com.jd.laf.web.vertx.config.VertxConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.jd.laf.web.vertx.Plugin.CODEC;
import static com.jd.laf.web.vertx.Plugin.MESSAGE;

/**
 * 消息处理器注册器
 */
public class MessageRegistrar implements Registrar {

    protected static final Logger logger = LoggerFactory.getLogger(MessageRegistrar.class);

    //消费者
    protected List<Consumer> consumers = new ArrayList<>(20);

    @Override
    public void register(final Vertx vertx, final Environment environment, final VertxConfig config) throws Exception {
        //配置消费者
        EnvironmentAware.setup(vertx, environment, MESSAGE.extensions());
        //配置消息编解码
        EnvironmentAware.setup(vertx, environment, CODEC.extensions());

        final EventBus eventBus = vertx.eventBus();
        //注册编解码
        CODEC.extensions().forEach(o -> eventBus.registerDefaultCodec(o.type(), o));
        //注册消费者
        config.getMessages().forEach(route -> {
            route.getHandlers().forEach(name -> {
                MessageHandler handler = MESSAGE.get(name);
                if (handler != null && route.getPath() != null && !route.getPath().isEmpty()) {
                    consumers.add(new Consumer(route.getPath(), name, eventBus.consumer(route.getPath(), handler)));
                }
            });
        });
    }

    @Override
    public int order() {
        return MESSAGE_ORDER;
    }

    @Override
    public void deregister(final Vertx vertx) {
        //TODO 不能优雅的退出
        //反序遍历注销
        for (int i = consumers.size() - 1; i >= 0; i--) {
            Consumer consumer = consumers.get(i);
            consumer.getConsumer().unregister(new Handler<AsyncResult<Void>>() {
                @Override
                public void handle(AsyncResult<Void> event) {
                    if (event.succeeded()) {
                        logger.info(String.format("success unregistering consumer %s of %s", consumer.getHandler(), consumer.getPath()));
                    } else {
                        logger.info(String.format("failed unregistering consumer %s of %s", consumer.getHandler(), consumer.getPath()));
                    }
                }
            });
        }
        CODEC.extensions().forEach(o -> vertx.eventBus().unregisterDefaultCodec(o.type()));
        consumers.clear();
    }

    protected static class Consumer {

        protected String path;
        protected String handler;
        protected MessageConsumer consumer;

        public Consumer(String path, String handler, MessageConsumer consumer) {
            this.path = path;
            this.handler = handler;
            this.consumer = consumer;
        }

        public String getPath() {
            return path;
        }

        public String getHandler() {
            return handler;
        }

        public MessageConsumer getConsumer() {
            return consumer;
        }
    }
}
