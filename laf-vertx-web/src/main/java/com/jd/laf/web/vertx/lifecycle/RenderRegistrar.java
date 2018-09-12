package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.render.Render;
import com.jd.laf.web.vertx.render.Renders;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 渲染处理器注册器
 */
public class RenderRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        for (Map.Entry<String, Render> entry : Renders.getPlugins().entrySet()) {
            environment.setup(entry.getValue());
        }
    }

    @Override
    public int order() {
        return HANDLER;
    }
}
