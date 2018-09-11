package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.render.Render;
import com.jd.laf.web.vertx.render.Renders;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 渲染处理器注册器
 */
public class RenderRegister implements Register {

    @Override
    public void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        for (Map.Entry<String, Render> entry : Renders.getPlugins().entrySet()) {
            initializer.accept(entry.getValue());
        }
    }

    @Override
    public int order() {
        return 4;
    }
}
