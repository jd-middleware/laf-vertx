package com.jd.laf.web.vertx.parameter;

import io.vertx.core.http.HttpServerRequest;

/**
 * 表单参数提供者
 */
public class FormParamSupplier implements ParameterSupplier {

    HttpServerRequest request;

    public FormParamSupplier(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public String get(final String name) {
        return name == null || name.isEmpty() ? null : request.getFormAttribute(name);
    }
}
