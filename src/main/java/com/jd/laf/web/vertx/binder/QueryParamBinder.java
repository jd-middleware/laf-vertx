package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.QueryParam;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 查询参数绑定
 */
public class QueryParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        QueryParam annotation = (QueryParam) context.getAnnotation();
        RoutingContext ctx = (RoutingContext) context.getSource();
        Field field = context.getField();
        String name = annotation.value();
        name = name == null || name.isEmpty() ? field.getName() : name;

        Class<?> type = field.getType();
        MultiMap params = ctx.request().params();
        if (type.isAssignableFrom(List.class)) {
            return context.bind(params.getAll(name));
        } else if (type.isAssignableFrom(Set.class)) {
            return context.bind(new HashSet<>(params.getAll(name)));
        } else if (type.isAssignableFrom(SortedSet.class)) {
            return context.bind(new TreeSet<>(params.getAll(name)));
        } else {
            return context.bind(params.get(name));
        }
    }

    @Override
    public Class<?> annotation() {
        return QueryParam.class;
    }
}
