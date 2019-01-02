package com.jd.laf.web.vertx.response;

import com.jd.laf.extension.Type;

/**
 * 异常响应转换接口
 */
public interface ErrorSupplier extends Type<Class<? extends Throwable>> {

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
    Class<? extends Throwable> type();

}
