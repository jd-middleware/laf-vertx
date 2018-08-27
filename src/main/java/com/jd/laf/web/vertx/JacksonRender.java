package com.jd.laf.web.vertx;

import io.vertx.core.json.Json;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Jackson渲染
 */
public class JacksonRender implements Render {

    static {
        Json.mapper.setSerializationInclusion(NON_NULL);
    }

    @Override
    public String render(final Object obj) {
        return Json.encode(obj);
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
