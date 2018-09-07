package com.jd.laf.web.vertx;

import com.jd.laf.binding.reflect.PropertySupplier;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextImpl;

/**
 * Created by yangyang115 on 18-9-7.
 */
public class RoutingContextPropertySupplier implements PropertySupplier {

    @Override
    public Object get(Object target, String name) throws Exception {
        RoutingContext context = (RoutingContext) target;
        return context.get(name);
    }

    @Override
    public boolean support(Class<?> clazz) {
        return clazz.equals(RoutingContextImpl.class);
    }
}
