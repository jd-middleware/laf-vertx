package com.jd.laf.web.vertx.message;

import com.jd.laf.web.vertx.config.RouteConfig;

/**
 * 路由事件，用于动态添加路由变更信息
 */
public class RouteMessage {

    public static final String TOPIC = "system_routes";

    private EventType type;

    private RouteConfig config;

    public RouteMessage() {
    }

    public RouteMessage(EventType type, RouteConfig config) {
        this.type = type;
        this.config = config;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public RouteConfig getConfig() {
        return config;
    }

    public void setConfig(RouteConfig config) {
        this.config = config;
    }

    public enum EventType {
        /**
         * 新增路由
         */
        ADD,
        /**
         * 删除路由
         */
        REMOVE
    }
}
