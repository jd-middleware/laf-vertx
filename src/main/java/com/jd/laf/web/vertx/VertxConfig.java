package com.jd.laf.web.vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置
 */
public class VertxConfig {
    //路由处理器
    List<Route> routes = new ArrayList<>(50);
    //异常处理器
    List<Route> errors = new ArrayList<>(1);
    //消息处理器
    List<Route> messages = new ArrayList<>(10);

    public VertxConfig() {
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getErrors() {
        return errors;
    }

    public void setErrors(List<Route> errors) {
        this.errors = errors;
    }

    public List<Route> getMessages() {
        return messages;
    }

    public void setMessages(List<Route> messages) {
        this.messages = messages;
    }

    /**
     * 添加路由处理器配制
     *
     * @param route 路由处理器配制
     */
    public void add(final Route route) {
        if (route != null) {
            switch (route.getType()) {
                case MSG:
                    messages.add(route);
                    break;
                case ERROR:
                    errors.add(route);
                    break;
                default:
                    routes.add(route);
            }
        }
    }

}
