package com.jd.laf.web.vertx.render;

import com.jd.laf.web.vertx.Command;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Jackson渲染
 */
public class JacksonRender implements Render {

    static {
        Json.mapper.setSerializationInclusion(NON_NULL);
    }

    @Override
    public void render(final RoutingContext context) {
        Object result = context.get(Command.RESULT);
        HttpServerResponse response = context.response();
        response.putHeader(CONTENT_TYPE, APPLICATION_JSON);
        response.end(Json.encode(result));
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String type() {
        return APPLICATION_JSON;
    }
}
