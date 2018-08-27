package com.jd.laf.web.vertx;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Render.APPLICATION_JSON;
import static com.jd.laf.web.vertx.Renders.getPlugin;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

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
        HttpServerResponse response = context.response();
        response.putHeader(CONTENT_TYPE, APPLICATION_JSON);
        response.putHeader(CONTENT_ENCODING, "UTF-8");
        response.end(render.render(context.get(Command.RESULT)));
    }
}
