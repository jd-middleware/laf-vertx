package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.render.Render;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Plugin.RENDER;
import static com.jd.laf.web.vertx.render.Render.APPLICATION_JSON;

/**
 * 渲染并结束
 */
public class RenderHandler implements RoutingHandler {

    public static final String RENDER_TYPE = "render";

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public String type() {
        return RENDER_TYPE;
    }

    @Override
    public void handle(final RoutingContext context) {
        render(context, true);
    }

    /**
     * 渲染
     *
     * @param context         上下文
     * @param failOnException 异常调用上下文的fail
     */
    public static void render(final RoutingContext context, final boolean failOnException) {
        if (context == null || context.response().ended()) {
            return;
        }
        //接收的格式
        String contentType = context.getAcceptableContentType();
        if (contentType == null || contentType.isEmpty()) {
            //发送的格式
            contentType = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        }
        //没有拿到格式，则默认是JSON
        contentType = contentType == null || contentType.isEmpty() ? APPLICATION_JSON : contentType.toLowerCase();
        //获取渲染插件
        Render render = RENDER.get(contentType);
        //系统默认提供了JSON渲染
        if (render == null) {
            render = RENDER.get(APPLICATION_JSON);
        }
        try {
            render.render(context);
        } catch (Exception e) {
            if (failOnException) {
                context.fail(e);
            } else {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
