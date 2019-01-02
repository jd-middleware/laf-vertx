package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Vertx;
import io.vertx.ext.web.common.template.TemplateEngine;

/**
 * 模板引擎提供者
 */
public interface TemplateProvider extends Type<String> {

    /**
     * 创建引擎
     *
     * @param vertx
     * @param context 上下文
     * @return
     * @throws Exception
     */
    TemplateEngine create(Vertx vertx, Environment context) throws Exception;

}
