package com.jd.laf.web.vertx.render;

import com.jd.laf.web.vertx.Command;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * 文本渲染
 */
public class TextPlainRender implements Render {

    @Override
    public void render(final RoutingContext context) {
        Object result = context.get(Command.RESULT);
        HttpServerResponse response = context.response();
        response.putHeader(CONTENT_TYPE, TEXT_PLAIN);
        response.end(result == null ? "" : result.toString());
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String type() {
        return TEXT_PLAIN;
    }
}
