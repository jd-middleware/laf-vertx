package com.jd.laf.web.vertx;

import io.vertx.core.http.HttpServerResponse;

/**
 * 渲染
 */
public interface Render {

    String APPLICATION_JSON = "application/json";

    /**
     * 渲染
     *
     * @param obj
     * @return
     */
    void render(HttpServerResponse response, Object obj);

    /**
     * 顺序
     *
     * @return
     */
    int order();

    /**
     * 类型
     *
     * @return
     */
    String type();
}
