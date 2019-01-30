package com.jd.laf.web.vertx.config;

import io.vertx.core.http.HttpMethod;

/**
 * 配置的路由类型
 */
public enum RouteType {
    OPTIONS(HttpMethod.OPTIONS),
    GET(HttpMethod.GET),
    HEAD(HttpMethod.HEAD),
    POST(HttpMethod.POST),
    PUT(HttpMethod.PUT),
    DELETE(HttpMethod.DELETE),
    TRACE(HttpMethod.TRACE),
    CONNECT(HttpMethod.CONNECT),
    PATCH(HttpMethod.PATCH),
    ERROR;

    private HttpMethod method;

    RouteType() {
    }

    RouteType(HttpMethod method) {
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
