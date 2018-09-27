package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.*;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.TemplateEngine;

import static com.jd.laf.web.vertx.Environment.*;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_CONTENT_TYPE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

/**
 * 模板处理器
 */
public class TemplateHandler implements RoutingHandler, EnvironmentAware {

    public static final String TEMPLATE = "template";

    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return TEMPLATE;
    }

    @Override
    public void setup(final Vertx vertx, final Environment environment) throws Exception {
        TemplateEngine engine = environment.getObject(TEMPLATE_ENGINE, TemplateEngine.class);
        if (engine == null) {
            String type = environment.getString(TEMPLATE_TYPE, "beetl");
            if (type != null && !type.isEmpty()) {
                TemplateProvider provider = TemplateProviders.getPlugin(type);
                if (provider != null) {
                    engine = provider.create(vertx, environment);
                } else {
                    throw new IllegalStateException("template engine is not found. " + type);
                }
            }
        }
        String templateDirectory = environment.getString(TEMPLATE_DIRECTORY, DEFAULT_TEMPLATE_DIRECTORY);
        String contentType = environment.getString(TEMPLATE_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        handler = io.vertx.ext.web.handler.TemplateHandler.create(engine, templateDirectory, contentType);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
