package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.PathParam;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;

/**
 * 表单绑定
 */
public class PathParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        PathParam annotation = (PathParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;
        return context.bind(((RoutingContext) source).pathParam(name));
    }

    @Override
    public Class<?> annotation() {
        return PathParam.class;
    }
}
