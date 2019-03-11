package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.SessionParam;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * 会话参数绑定
 */
public class SessionParamBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        SessionParam annotation = (SessionParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        String name = annotation.value();
        name = name == null || name.isEmpty() ? context.getName() : name;
        Session session = ((RoutingContext) source).session();
        return session == null ? false : context.bind(session.get(name));
    }

    @Override
    public Class<?> annotation() {
        return SessionParam.class;
    }
}
