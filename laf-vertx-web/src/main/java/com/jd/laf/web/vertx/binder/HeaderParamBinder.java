package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.HeaderParam;
import io.vertx.ext.web.RoutingContext;

/**
 * 表单绑定
 */
public class HeaderParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        HeaderParam annotation = (HeaderParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;
        return context.bind(((RoutingContext) source).request().getHeader(name));
    }

    @Override
    public Class<?> annotation() {
        return HeaderParam.class;
    }
}
