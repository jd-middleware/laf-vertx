package com.jd.laf.web.vertx.render;

import com.jd.laf.binding.marshaller.JsonProviders;
import com.jd.laf.web.vertx.Command;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Jackson渲染
 */
public class JsonRender implements Render {

    @Override
    public void render(final RoutingContext context) throws Exception {
        //获取结果
        Object result = context.get(Command.RESULT);
        HttpServerResponse response = context.response();
        response.putHeader(CONTENT_TYPE, APPLICATION_JSON);
        if (result != null) {
            //数据不为空
            response.end(JsonProviders.getPlugin().getMarshaller().marshall(result));
        } else {
            response.end();
        }
    }

    @Override
    public String type() {
        return APPLICATION_JSON;
    }
}
