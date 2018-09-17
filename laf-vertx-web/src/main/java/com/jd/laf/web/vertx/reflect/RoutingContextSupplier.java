package com.jd.laf.web.vertx.reflect;

import com.jd.laf.binding.reflect.PropertySupplier;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextImpl;

/**
 * 路由上下文属性提供者
 * Created by yangyang115 on 18-9-7.
 */
public class RoutingContextSupplier implements PropertySupplier {

    @Override
    public Object get(final Object target, final String name) throws Exception {
        RoutingContext context = (RoutingContext) target;
        return context.get(name);
    }

    @Override
    public boolean support(final Class<?> clazz) {
        return RoutingContextImpl.class.equals(clazz);
    }
}
