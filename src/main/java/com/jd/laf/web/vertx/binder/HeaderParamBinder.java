package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.HeaderParam;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;

/**
 * 表单绑定
 */
public class HeaderParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        HeaderParam annotation = (HeaderParam) context.getAnnotation();
        RoutingContext ctx = (RoutingContext) context.getSource();
        Field field = context.getField();
        String name = annotation.value();
        name = name == null || name.isEmpty() ? field.getName() : name;
        return context.bind(ctx.request().getHeader(name));
    }

    @Override
    public Class<?> annotation() {
        return HeaderParam.class;
    }
}
