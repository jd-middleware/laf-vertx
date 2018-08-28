package com.jd.laf.web.vertx;

import java.util.Map;

/**
 * 上下文感知
 */
public interface ContextAware {

    /**
     * 通过上下文来构建，单线程调用
     *
     * @param context 上下文
     */
    void setup(Map<String, Object> context);
}
