package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import io.vertx.ext.web.RoutingContext;

/**
 * 上下文绑定
 */
public class ContextBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        RoutingContext ctx = (RoutingContext) context.getSource();
        return context.bind(ctx);
    }

    @Override
    public Class<?> annotation() {
        return com.jd.laf.web.vertx.annotation.Context.class;
    }
}
