package com.jd.laf.web.vertx.template;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystemException;
import org.beetl.core.*;
import org.beetl.core.misc.BeetlUtil;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * beetl模板引擎实现
 */
public class BeetlTemplateEngineImpl implements BeetlTemplateEngine {

    protected final GroupTemplate groupTemplate;

    public BeetlTemplateEngineImpl(Vertx vertx) throws Exception {
        this(vertx, StandardCharsets.UTF_8);
    }

    public BeetlTemplateEngineImpl(Vertx vertx, Charset charset) throws Exception {
        this(new GroupTemplate(new VertxResourceLoader(vertx, charset), Configuration.defaultConfiguration()));
    }

    public BeetlTemplateEngineImpl(GroupTemplate engine) {
        this.groupTemplate = engine;
    }

    @Override
    public void render(final Map<String, Object> context, final String templateFile, final Handler<AsyncResult<Buffer>> handler) {
        try {
            Template template = groupTemplate.getTemplate(templateFile);
            template.binding(context);
            String text = template.render();
            handler.handle(Future.succeededFuture(Buffer.buffer(text)));
        } catch (FileSystemException e) {
            handler.handle(Future.failedFuture(e.getCause() != null ? e.getCause() : e));
        } catch (Exception e) {
            handler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public boolean isCachingEnabled() {
        return false;
    }

    /**
     * Vertx资源加载器
     */
    public static class VertxResourceLoader implements ResourceLoader {

        protected Vertx vertx;

        protected Charset charset;

        public VertxResourceLoader(Vertx vertx, Charset charset) {
            this.vertx = vertx;
            this.charset = charset;
        }

        @Override
        public Resource getResource(final String key) {
            return new VertxResource(key, this, vertx, charset);
        }

        @Override
        public boolean isModified(Resource key) {
            return key.isModified();
        }

        @Override
        public boolean exist(final String key) {
            return vertx.fileSystem().existsBlocking(key);
        }

        @Override
        public void close() {

        }

        @Override
        public void init(GroupTemplate gt) {

        }

        @Override
        public String getResourceId(final Resource resource, final String id) {
            if (resource == null) {
                return id;
            } else {
                return BeetlUtil.getRelPath(resource.getId(), id);
            }
        }

        @Override
        public String getInfo() {
            return "VertxResourceLoader";
        }

    }

    /**
     * 资源
     */
    protected static class VertxResource extends Resource {

        protected Vertx vertx;
        protected Charset charset;

        public VertxResource(String id, ResourceLoader loader, Vertx vertx, Charset charset) {
            super(id, loader);
            this.vertx = vertx;
            this.charset = charset;
        }

        @Override
        public Reader openReader() {
            Buffer buffer = vertx.fileSystem().readFileBlocking(id);
            return new StringReader(buffer.toString(charset));
        }

        @Override
        public boolean isModified() {
            return false;
        }
    }
}
