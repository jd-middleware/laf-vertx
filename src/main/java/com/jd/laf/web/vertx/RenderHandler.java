package com.jd.laf.web.vertx;

import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Render.APPLICATION_JSON;
import static com.jd.laf.web.vertx.Renders.getPlugin;

/**
 * 渲染并结束
 */
public class RenderHandler implements RoutingHandler {

    public static final String RENDER = "render";

    protected static final Render JSON = getPlugin(APPLICATION_JSON);

    @Override
    public String type() {
        return RENDER;
    }

    @Override
    public void handle(final RoutingContext context) {
        String contentType = context.getAcceptableContentType();
        contentType = contentType == null ? APPLICATION_JSON : contentType.toLowerCase();
        Render render = getPlugin(contentType);
        render = render == null ? JSON : render;
        context.response().end(render.render(context.get(Command.RESULT)));
    }
}
