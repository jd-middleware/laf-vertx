package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.Response;
import io.vertx.ext.web.RoutingContext;

/**
 * 响应绑定
 */
public class ResponseBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        RoutingContext ctx = (RoutingContext) context.getSource();
        return context.bind(ctx.response());
    }

    @Override
    public Class<?> annotation() {
        return Response.class;
    }
}
