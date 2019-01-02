package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.render.Render;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.RENDER;

/**
 * 渲染处理器注册器
 */
public class RenderRegistrar implements Registrar {

    public static final int RENDER_ORDER = ORDER + 1;

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        for (Render render : RENDER.extensions()) {
            EnvironmentAware.setup(vertx, environment, render);
        }
    }

    @Override
    public int order() {
        //模板引擎是在handler里面进行了初始化
        return RENDER_ORDER;
    }
}
