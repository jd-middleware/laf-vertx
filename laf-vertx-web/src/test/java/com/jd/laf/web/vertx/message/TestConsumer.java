package com.jd.laf.web.vertx.message;

import com.jd.laf.web.vertx.MessageHandler;
import io.vertx.core.eventbus.Message;

public class TestConsumer implements MessageHandler<String> {
    @Override
    public String type() {
        return "test";
    }

    @Override
    public void handle(Message<String> event) {

    }
}
