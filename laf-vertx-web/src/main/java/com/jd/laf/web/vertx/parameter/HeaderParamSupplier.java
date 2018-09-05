package com.jd.laf.web.vertx.parameter;

import io.vertx.core.http.HttpServerRequest;

/**
 * 请求头参数提供者
 */
public class HeaderParamSupplier implements ParameterSupplier {

    HttpServerRequest request;

    public HeaderParamSupplier(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public String get(final String name) {
        return name == null || name.isEmpty() ? null : request.getHeader(name);
    }
}
