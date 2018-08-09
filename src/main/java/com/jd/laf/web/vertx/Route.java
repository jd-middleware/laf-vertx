package com.jd.laf.web.vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置的路由信息
 */
public class Route {
    //方法
    private RouteType type;
    //路径
    private String path;
    //处理器
    private List<String> handlers = new ArrayList<>(5);

    public Route(RouteType type) {
        this.type = type;
    }

    public Route(RouteType type, String path) {
        this.type = type;
        this.path = path;
    }

    public Route(RouteType type, String path, List<String> handlers) {
        this.type = type;
        this.path = path;
        this.handlers = handlers;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<String> handlers) {
        this.handlers = handlers;
    }

    public boolean isEmpty() {
        return handlers == null || handlers.isEmpty();
    }

    public void add(final String handler) {
        if (handler != null && !handler.isEmpty()) {
            handlers.add(handler);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Route{");
        sb.append("type=").append(type);
        if (path != null && !path.isEmpty()) {
            sb.append(", path='").append(path).append('\'');
        }
        sb.append(", handlers=").append(handlers);
        sb.append('}');
        return sb.toString();
    }
}
