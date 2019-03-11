package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.CookieParam;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * Cookie读取
 */
public class CookieParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        CookieParam annotation = (CookieParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;
        Cookie cookie = ((RoutingContext) source).getCookie(name);
        return cookie == null ? false : context.bind(cookie.getValue());
    }

    @Override
    public Class<?> annotation() {
        return CookieParam.class;
    }
}
