package com.jd.laf.web.vertx.template;

import io.vertx.core.Vertx;
import io.vertx.ext.web.templ.TemplateEngine;
import org.beetl.core.GroupTemplate;

/**
 * beetl模板引擎
 */
public interface BeetlTemplateEngine extends TemplateEngine {

    /**
     * Create a template engine using defaults
     *
     * @return the engine
     * @throws Exception
     */
    static BeetlTemplateEngine create(Vertx vertx) throws Exception {
        return new BeetlTemplateEngineImpl(vertx);
    }

    /**
     * Create a template engine using a custom Builder, e.g. if
     * you want use custom Filters or Functions.
     *
     * @return the engine
     */
    static BeetlTemplateEngine create(GroupTemplate engine) {
        return new BeetlTemplateEngineImpl(engine);
    }

}
