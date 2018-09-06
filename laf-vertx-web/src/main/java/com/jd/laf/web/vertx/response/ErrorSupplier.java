package com.jd.laf.web.vertx.response;

/**
 * 异常响应转换接口
 */
public interface ErrorSupplier {

    /**
     * 把异常转换成响应
     *
     * @param throwable
     * @return
     */
    Response error(Throwable throwable);

    /**
     * 支持的异常类
     *
     * @return
     */
    Class<?> type();

}
