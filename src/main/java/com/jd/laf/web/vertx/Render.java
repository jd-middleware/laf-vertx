package com.jd.laf.web.vertx;

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
    String render(Object obj);

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
