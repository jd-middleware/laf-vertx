package com.jd.laf.web.vertx.context;

import com.jd.laf.binding.reflect.PropertySupplier;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由上下文属性提供者
 */
public class RoutingContextSupplier implements PropertySupplier {

    @Override
    public Object get(final Object target, final String name) throws Exception {
        return ((RoutingContext) target).get(name);
    }

    @Override
    public boolean support(final Class<?> clazz) {
        return RoutingContext.class.isAssignableFrom(clazz);
    }
}
