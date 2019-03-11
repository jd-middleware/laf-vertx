package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.FormParam;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import java.util.Collection;

/**
 * 表单绑定
 */
public class FormParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        FormParam annotation = (FormParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;
        Class<?> type = context.getType();
        MultiMap attributes = ((RoutingContext) source).request().formAttributes();
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            //集合
            return context.bind(attributes.getAll(name));
        } else {
            return context.bind(attributes.get(name));
        }
    }

    @Override
    public Class<?> annotation() {
        return FormParam.class;
    }
}
