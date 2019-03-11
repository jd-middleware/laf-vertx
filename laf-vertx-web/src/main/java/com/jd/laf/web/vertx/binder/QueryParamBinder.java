package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.QueryParam;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import java.util.Collection;

/**
 * 查询参数绑定
 */
public class QueryParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        QueryParam annotation = (QueryParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;

        Class<?> type = context.getType();
        MultiMap params = ((RoutingContext) source).request().params();
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return context.bind(params.getAll(name));
        } else {
            return context.bind(params.get(name));
        }
    }

    @Override
    public Class<?> annotation() {
        return QueryParam.class;
    }
}
