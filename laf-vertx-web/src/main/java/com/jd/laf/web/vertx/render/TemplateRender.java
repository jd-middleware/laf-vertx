package com.jd.laf.web.vertx.render;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystemException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;

import static com.jd.laf.web.vertx.Environment.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

/**
 * 模板渲染
 */
public class TemplateRender implements Render, EnvironmentAware {

    protected TemplateEngine engine;
    protected String templateDirectory;

    @Override
    public void render(final RoutingContext context) throws Exception {
        String template = context.get(TEMPLATE);
        if (template == null || template.isEmpty()) {
            context.fail(new FileNotFoundException("template is not found."));
        } else if (engine == null) {
            context.fail(new IllegalStateException("template engine is not found."));
        } else {
            context.response().putHeader(CONTENT_TYPE, TEXT_HTML);
            engine.render(context.data(), templateDirectory + template, rs -> {
                if (rs.succeeded()) {
                    context.response().end(rs.result());
                } else {
                    Throwable e = rs.cause();
                    if (e instanceof FileSystemException) {
                        context.fail(e.getCause() != null ? e.getCause() : e);
                    } else {
                        context.fail(rs.cause());
                    }
                }
            });
        }
    }

    @Override
    public void setup(final Vertx vertx, final Environment environment) {
        engine = environment.getObject(TEMPLATE_ENGINE, TemplateEngine.class);
        templateDirectory = environment.getString(TEMPLATE_DIRECTORY, DEFAULT_TEMPLATE_DIRECTORY);
        if (!templateDirectory.endsWith(File.separator)) {
            templateDirectory = templateDirectory + File.separator;
        }
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
