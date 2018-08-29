package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.SessionParam;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.lang.reflect.Field;

/**
 * 会话参数绑定
 */
public class SessionParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        SessionParam annotation = (SessionParam) context.getAnnotation();
        RoutingContext ctx = (RoutingContext) context.getSource();
        Field field = context.getField();
        String name = annotation.value();
        name = name == null || name.isEmpty() ? field.getName() : name;
        Session session = ctx.session();
        return session == null ? false : context.bind(session.get(name));
    }

    @Override
    public Class<?> annotation() {
        return SessionParam.class;
    }
}
