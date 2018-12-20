package com.jd.laf.web.vertx.template;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.TemplateProvider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.common.template.TemplateEngine;

/**
 * beetl模板引擎
 */
public class BeetlProvider implements TemplateProvider {

    public static final String BEETL = "beetl";

    @Override
    public TemplateEngine create(final Vertx vertx, final Environment context) throws Exception {
        return BeetlTemplateEngine.create(vertx);
    }

    @Override
    public String type() {
        return BEETL;
    }
}
