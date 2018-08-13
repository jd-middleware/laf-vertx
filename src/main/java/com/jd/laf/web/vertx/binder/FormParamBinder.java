package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.CookieParam;
import com.jd.laf.web.vertx.annotation.FormParam;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;

/**
 * 表单绑定
 */
public class FormParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        FormParam annotation = (FormParam) context.getAnnotation();
        RoutingContext ctx = (RoutingContext) context.getSource();
        Field field = context.getField();
        String name = annotation.value();
        name = name == null || name.isEmpty() ? field.getName() : name;
        if (ctx != null) {
            Cookie cookie = ctx.getCookie(name);
            if (cookie != null) {
                return context.bind(cookie.getValue());
            }
        }

        return false;
    }

    @Override
    public Class<?> annotation() {
        return FormParam.class;
    }
}
