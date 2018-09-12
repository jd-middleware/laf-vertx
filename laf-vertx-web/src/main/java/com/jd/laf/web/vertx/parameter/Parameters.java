package com.jd.laf.web.vertx.parameter;

import io.vertx.core.http.HttpServerRequest;

import static com.jd.laf.web.vertx.parameter.Parameter.valueOf;

/**
 * 工具类
 */
public class Parameters {
    //线程变量，避免频繁调用
    //protected static ThreadLocal<RequestParameter> local = new ThreadLocal<>();

    /**
     * 获取请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static Parameter query(final HttpServerRequest request) {
        return valueOf(new QueryParamSupplier(request));
    }

    /**
     * 获取头请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static Parameter header(final HttpServerRequest request) {
        return valueOf(new HeaderParamSupplier(request));
    }

    /**
     * 获取表单参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static Parameter form(final HttpServerRequest request) {
        return valueOf(new FormParamSupplier(request));
    }

    /**
     * 获取请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static RequestParameter get(final HttpServerRequest request) {
        return new RequestParameter(request);
    }

    /**
     * 参数
     */
    public static class RequestParameter {
        //请求参数
        Parameter query;
        //请求头参数
        Parameter header;
        //表单参数
        Parameter form;

        HttpServerRequest request;

        public RequestParameter(HttpServerRequest request) {
            this.request = request;
        }

        public Parameter query() {
            if (query == null) {
                query = valueOf(new QueryParamSupplier(request));
            }
            return query;
        }

        public Parameter header() {
            if (header == null) {
                header = valueOf(new HeaderParamSupplier(request));
            }
            return header;
        }

        public Parameter form() {
            if (form == null) {
                form = valueOf(new FormParamSupplier(request));
            }
            return form;
        }
    }

}
