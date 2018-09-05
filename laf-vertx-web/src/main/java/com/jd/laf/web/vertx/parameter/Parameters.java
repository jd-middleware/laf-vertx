package com.jd.laf.web.vertx.parameter;

import io.vertx.core.http.HttpServerRequest;

/**
 * 工具类
 */
public class Parameters {
    //线程变量，避免频繁调用
    protected static ThreadLocal<Container> local = new ThreadLocal<>();

    /**
     * 获取请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static Parameter query(final HttpServerRequest request) {
        return get(request).request;
    }

    /**
     * 获取头请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    public static Parameter header(final HttpServerRequest request) {
        return get(request).header;
    }

    /**
     * 获取请求参数值
     *
     * @param request 请求
     * @return 参数值
     */
    protected static Container get(final HttpServerRequest request) {
        Container result = local.get();
        if (result == null) {
            result = new Container(Parameter.valueOf(new QueryParamSupplier(request)),
                    Parameter.valueOf(new HeaderParamSupplier(request)));
            local.set(result);
        }
        return result;
    }

    /**
     * 参数
     */
    protected static class Container {
        //请求参数
        Parameter request;
        //请求头参数
        Parameter header;

        public Container(Parameter request, Parameter header) {
            this.request = request;
            this.header = header;
        }
    }

}
