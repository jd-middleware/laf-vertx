package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.CVertx;
import io.vertx.ext.web.RoutingContext;

/**
 * Vertx绑定
 */
public class VertxBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        RoutingContext ctx = (RoutingContext) context.getSource();
        return context.bind(ctx.vertx());
    }

    @Override
    public Class<?> annotation() {
        return CVertx.class;
    }
}
