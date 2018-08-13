package com.jd.laf.web.vertx;

import java.io.BufferedReader;
import java.io.IOException;
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

    /**
     * 构造器
     */
    public static class Builder {

        /**
         * 构造
         *
         * @param reader
         * @return
         * @throws IOException
         */
        public static VertxConfig build(final BufferedReader reader) throws IOException {

            VertxConfig config = new VertxConfig();

            String line;
            String path;
            RouteType type;
            String[] handlers;
            int typeEnd;
            int pathEnd;
            Route route;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    //注释
                    continue;
                }
                typeEnd = line.indexOf(' ');
                if (typeEnd <= 0) {
                    //没有类型
                    continue;
                }
                path = null;
                type = RouteType.valueOf(line.substring(0, typeEnd).toUpperCase());
                if (type == null) {
                    //类型不支持
                    continue;
                }
                pathEnd = line.indexOf('=', typeEnd + 1);
                if (pathEnd > 0) {
                    path = line.substring(typeEnd + 1, pathEnd);
                    handlers = line.substring(pathEnd + 1).split(",");
                } else if (type != RouteType.ERROR) {
                    //非异常都需要路径，异常没有路径则是默认异常处理
                    continue;
                } else {
                    handlers = line.substring(typeEnd + 1).split(",");
                }
                if (handlers != null && handlers.length > 0) {
                    route = new Route(type, path);
                    for (String handler : handlers) {
                        handler = handler.trim();
                        if (handler != null) {
                            route.add(handler);
                        }
                    }
                    if (!route.isEmpty()) {
                        config.add(route);
                    }
                }
            }
            return config;
        }

    }

}
