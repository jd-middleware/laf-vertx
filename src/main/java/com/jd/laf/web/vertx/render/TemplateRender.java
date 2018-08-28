package com.jd.laf.web.vertx.render;

import com.jd.laf.web.vertx.Command;
import com.jd.laf.web.vertx.Context;
import com.jd.laf.web.vertx.ContextAware;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.TemplateEngine;

import static com.jd.laf.web.vertx.Context.TEMPLATE_DIRECTORY;
import static com.jd.laf.web.vertx.Context.TEMPLATE_ENGINE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

/**
 * 模板渲染
 */
public class TemplateRender implements Render, ContextAware {


    protected TemplateEngine engine;
    protected String templateDirectory;

    @Override
    public void render(final RoutingContext context) throws Exception {
        Command.TemplateResult result = context.get(Command.RESULT);
        context.put(Command.RESULT, result.getResult());
        HttpServerResponse response = context.response();
        response.putHeader(CONTENT_TYPE, TEXT_HTML);
        engine.render(context, templateDirectory, result.getTemplate(), rs -> {
            if (rs.succeeded()) {
                response.end(rs.result());
            } else {
                context.fail(rs.cause());
            }
        });
    }

    @Override
    public void setup(final Context context) {
        engine = context.getObject(TEMPLATE_ENGINE, TemplateEngine.class);
        templateDirectory = context.getString(TEMPLATE_DIRECTORY, DEFAULT_TEMPLATE_DIRECTORY);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String type() {
        return TEXT_HTML;
    }
}
