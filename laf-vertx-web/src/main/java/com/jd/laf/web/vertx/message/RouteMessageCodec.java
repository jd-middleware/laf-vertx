package com.jd.laf.web.vertx.message;

/**
 * 审计日志消息编解码处理器
 */
public class RouteMessageCodec extends JsonMessageCodec<RouteMessage> {

    public RouteMessageCodec() {
        super(RouteMessage.class);
    }
}
