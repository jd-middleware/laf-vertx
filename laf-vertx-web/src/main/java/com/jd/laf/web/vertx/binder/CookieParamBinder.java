package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.CookieParam;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Cookie读取
 */
public class CookieParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        CookieParam annotation = (CookieParam) context.getAnnotation();
        RoutingContext ctx = (RoutingContext) context.getSource();
        Field field = context.getField();
        String name = annotation.value();
        name = name == null || name.isEmpty() ? field.getName() : name;
        Cookie cookie = ctx.getCookie(name);
        return cookie == null ? false : context.bind(cookie.getValue());
    }

    @Override
    public Class<?> annotation() {
        return CookieParam.class;
    }
}
