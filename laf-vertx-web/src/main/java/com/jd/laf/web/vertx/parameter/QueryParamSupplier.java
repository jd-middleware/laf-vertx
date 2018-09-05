package com.jd.laf.web.vertx.parameter;

import io.vertx.core.http.HttpServerRequest;

/**
 * 请求参数提供者
 */
public class QueryParamSupplier implements ParameterSupplier {

    HttpServerRequest request;

    public QueryParamSupplier(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public String get(final String name) {
        return name == null || name.isEmpty() ? null : request.getParam(name);
    }
}
