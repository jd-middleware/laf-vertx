package com.jd.laf.web.vertx.binder.parameter;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.binding.ParamBinding;
import com.jd.laf.web.vertx.annotation.parameter.QueryParam;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.binding.ParamBinding.PARAM_BINDING_ARGS;

/**
 * 值绑定
 */
public class QueryParamBinder implements Binder {

    @Override
    public boolean bind(final Context context) throws ReflectionException {
        if (context == null) {
            return false;
        }
        QueryParam queryParam = (QueryParam) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        RoutingContext routingContext = (RoutingContext)source;
        ParamBinding.BindingMethod bindingMethod = (ParamBinding.BindingMethod) context.getTarget();
        int paramIndex = bindingMethod.getParamIndex();
        Object[] args = routingContext.get(PARAM_BINDING_ARGS);
        //字段名
        String name = queryParam.value();
        //获取属性值
        MultiMap params = ((RoutingContext) source).request().params();
        args[paramIndex] = params.get(name);
        return true;
    }

    @Override
    public Class<?> annotation() {
        return QueryParam.class;
    }
}
