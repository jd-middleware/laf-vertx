package com.jd.laf.web.vertx.template;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.TemplateProvider;
import io.vertx.ext.web.templ.TemplateEngine;

/**
 * beetl模板引擎
 */
public class BeetlProvider implements TemplateProvider {

    public static final String BEETL = "beetl";

    @Override
    public TemplateEngine create(final Environment context) throws Exception {
        return BeetlTemplateEngine.create(context.getVertx());
    }

    @Override
    public String type() {
        return BEETL;
    }
}
