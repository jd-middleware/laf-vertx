package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.QueryParam;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;

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
        //TODO 增加List转String
        return context.bind(ctx.request().getParam(name));
    }

    @Override
    public Class<?> annotation() {
        return QueryParam.class;
    }
}
