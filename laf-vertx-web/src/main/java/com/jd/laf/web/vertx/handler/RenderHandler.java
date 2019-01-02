package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.render.Render;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Plugin.RENDER;
import static com.jd.laf.web.vertx.render.Render.APPLICATION_JSON;

/**
 * 渲染并结束
 */
public class RenderHandler implements RoutingHandler {

    public static final String RENDER_TYPE = "render";

    public static final Render JSON = RENDER.get(APPLICATION_JSON);

    @Override
    public String type() {
        return RENDER_TYPE;
    }

    @Override
    public void handle(final RoutingContext context) {
        render(context);
    }

    /**
     * 渲染
     *
     * @param context
     */
    public static void render(final RoutingContext context) {
        if (context == null || context.response().ended()) {
            return;
        }
        String contentType = context.request().getHeader("Content-Type");
        if (contentType == null || contentType.isEmpty()) {
            contentType = context.getAcceptableContentType();
        }
        contentType = contentType == null ? APPLICATION_JSON : contentType.toLowerCase();
        Render render = RENDER.get(contentType);
        render = render == null ? JSON : render;
        try {
            render.render(context);
        } catch (Exception e) {
            context.fail(e);
        }
    }
}
