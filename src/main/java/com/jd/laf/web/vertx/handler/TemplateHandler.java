package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Context;
import com.jd.laf.web.vertx.ContextAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.TemplateEngine;

import static com.jd.laf.web.vertx.Context.*;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_CONTENT_TYPE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

/**
 * 模板处理器
 */
public class TemplateHandler implements RoutingHandler, ContextAware {

    public static final String TEMPLATE = "template";

    protected Handler<RoutingContext> handler;

    @Override
    public String type() {
        return TEMPLATE;
    }

    @Override
    public void setup(final Context context) {
        TemplateEngine engine = context.getObject(TEMPLATE_ENGINE, TemplateEngine.class);
        String templateDirectory = context.getString(TEMPLATE_DIRECTORY, DEFAULT_TEMPLATE_DIRECTORY);
        String contentType = context.getString(TEMPLATE_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        handler = io.vertx.ext.web.handler.TemplateHandler.create(engine, templateDirectory, contentType);
    }

    @Override
    public void handle(final RoutingContext context) {
        handler.handle(context);
    }
}
