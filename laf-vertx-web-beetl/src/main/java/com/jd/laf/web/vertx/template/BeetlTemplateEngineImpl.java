package com.jd.laf.web.vertx.template;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * beetl模板引擎实现
 */
public class BeetlTemplateEngineImpl implements BeetlTemplateEngine {

    protected final GroupTemplate groupTemplate;

    public BeetlTemplateEngineImpl(Vertx vertx) throws Exception {
        this(new GroupTemplate(new ClasspathResourceLoader(), Configuration.defaultConfiguration()));
    }

    public BeetlTemplateEngineImpl(GroupTemplate engine) {
        this.groupTemplate = engine;
    }

    @Override
    public void render(final RoutingContext context, final String templateDirectory,
                       final String templateFileName, final Handler<AsyncResult<Buffer>> handler) {
        try {
            Map<String, Object> data = context.data();
            Map<String, Object> variables = new HashMap<>(data.size() + 1);
            variables.put("context", context);
            variables.putAll(data);
            String file = templateDirectory + templateFileName;
            Template template = groupTemplate.getTemplate(file);
            template.binding(variables);
            String text = template.render();
            handler.handle(Future.succeededFuture(Buffer.buffer(text)));
        } catch (Exception e) {
            handler.handle(Future.failedFuture(e));
        }
    }
}
