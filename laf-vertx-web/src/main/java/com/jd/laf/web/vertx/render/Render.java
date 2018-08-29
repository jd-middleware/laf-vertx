package com.jd.laf.web.vertx.render;

import io.vertx.ext.web.RoutingContext;

/**
 * 渲染
 */
public interface Render {

    String APPLICATION_JSON = "application/json";

    String TEXT_PLAIN = "text/plain";

    String TEXT_HTML = "text/html";

    /**
     * 渲染
     *
     * @param context 上下文
     * @throws Exception
     */
    void render(RoutingContext context) throws Exception;

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
