package com.jd.laf.web.vertx.render;

import com.jd.laf.extension.Ordered;
import com.jd.laf.extension.Type;
import io.vertx.ext.web.RoutingContext;

/**
 * 渲染
 */
public interface Render extends Ordered, Type<String> {

    String APPLICATION_JSON = "application/json";

    String APPLICATION_XML = "application/xml";

    String TEXT_PLAIN = "text/plain";

    String TEXT_HTML = "text/html";

    /**
     * 渲染
     *
     * @param context 上下文
     * @throws Exception
     */
    void render(RoutingContext context) throws Exception;

    @Override
    default int order() {
        return ORDER;
    }

}
