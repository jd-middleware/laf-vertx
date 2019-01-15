package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.TemplateProvider;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;

import static com.jd.laf.web.vertx.Environment.*;
import static com.jd.laf.web.vertx.Plugin.TEMPLATE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_CONTENT_TYPE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

/**
 * 模板处理器
 */
public class TemplateHandler implements RoutingHandler, EnvironmentAware {

    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return "template";
    }

    @Override
    public void setup(final Vertx vertx, final Environment environment) throws Exception {
        TemplateEngine engine = environment.getObject(TEMPLATE_ENGINE, TemplateEngine.class);
        if (engine == null) {
            String type = environment.getString(TEMPLATE_TYPE, "beetl");
            TemplateProvider provider = TEMPLATE.get(type);
            if (provider != null) {
                engine = provider.create(vertx, environment);
                environment.put(TEMPLATE_ENGINE, engine);
            } else {
                throw new IllegalStateException("template engine is not found. " + type);
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
