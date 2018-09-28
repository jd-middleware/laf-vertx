package com.jd.laf.web.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.templ.TemplateEngine;

/**
 * 模板引擎提供者
 */
public interface TemplateProvider {

    /**
     * 创建引擎
     *
     * @param vertx
     * @param context 上下文
     * @return
     * @throws Exception
     */
    TemplateEngine create(Vertx vertx, Environment context) throws Exception;

    /**
     * 类型
     *
     * @return
     */
    String type();
}
