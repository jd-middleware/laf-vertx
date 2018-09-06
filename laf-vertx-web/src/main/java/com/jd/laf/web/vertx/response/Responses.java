package com.jd.laf.web.vertx.response;

import static com.jd.laf.web.vertx.response.Response.INTERNAL_ERROR;

/**
 * 响应工具类
 */
public abstract class Responses {

    /**
     * 成功响应
     *
     * @return
     */
    public static Response success() {
        return new Response();
    }

    /**
     * 成功响应
     *
     * @param data
     * @return
     */
    public static Response success(Object data) {
        return new Response(data);
    }

    /**
     * 成功响应
     *
     * @param code
     * @param data
     * @return
     */
    public static Response success(int code, Object data) {
        return new Response(code, data);
    }

    /**
     * 成功响应
     *
     * @param code   业务代码
     * @param status http响应码
     * @param data
     * @return
     */
    public static Response success(int code, int status, Object data) {
        return new Response(code, status, null, data);
    }

    /**
     * 异常响应
     *
     * @param code    代码
     * @param message 消息
     * @return
     */
    public static Response error(int code, String message) {
        return new Response(code, message);
    }

    /**
     * 异常响应
     *
     * @param code    代码
     * @param status  http响应码
     * @param message 消息
     * @return
     */
    public static Response error(int code, int status, String message) {
        return new Response(code, status, message, null);
    }

    /**
     * 异常响应
     *
     * @param throwable 异常
     * @return
     */
    public static Response error(Throwable throwable) {
        return error(throwable, null, INTERNAL_ERROR, INTERNAL_ERROR);
    }

    /**
     * 异常响应
     *
     * @param throwable 异常
     * @param supplier  异常转换
     * @return
     */
    public static Response error(Throwable throwable, ErrorSupplier supplier) {
        return error(throwable, supplier, INTERNAL_ERROR, INTERNAL_ERROR);
    }

    /**
     * 异常响应
     *
     * @param throwable
     * @param supplier      异常转换
     * @param defaultCode   默认业务响应代码
     * @param defaultStatus 默认HTTP响应代码
     * @return
     */
    public static Response error(Throwable throwable, ErrorSupplier supplier, int defaultCode, int defaultStatus) {
        if (throwable == null) {
            return error(defaultCode, defaultStatus, "unknown error.");
        } else if (supplier != null) {
            return supplier.error(throwable);
        } else {
            supplier = ErrorSuppliers.getPlugin(throwable.getClass());
            if (supplier != null) {
                return supplier.error(throwable);
            } else {
                return error(defaultCode, defaultStatus, throwable.getMessage());
            }
        }
    }


}
