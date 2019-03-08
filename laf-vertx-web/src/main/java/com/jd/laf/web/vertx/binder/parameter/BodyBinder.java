package com.jd.laf.web.vertx.binder.parameter;

import com.jd.laf.binding.Plugin;
import com.jd.laf.binding.binder.Binder;
import com.jd.laf.web.vertx.binding.ParamBinding;
import com.jd.laf.web.vertx.annotation.parameter.Body;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import io.vertx.ext.web.RoutingContext;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Properties;

import static com.jd.laf.web.vertx.binding.ParamBinding.PARAM_BINDING_ARGS;

/**
 * 参数Body 绑定
 */
public class BodyBinder implements Binder {

    @Override
    public boolean bind(final Context context) throws ReflectionException {
        Body annotation = (Body) context.getAnnotation();
        Object source = context.getSource();
        if (!(source instanceof RoutingContext)) {
            return false;
        }
        RoutingContext ctx = (RoutingContext) source;
        Body.BodyType type = annotation.type();
        ParamBinding.BindingMethod method = (ParamBinding.BindingMethod) context.getTarget();
        int paramIndex = method.getParamIndex();
        Object[] args = ctx.get(PARAM_BINDING_ARGS);
        //获取字段类型
        Method m = method.getMethod();
        Class classType = m.getParameterTypes()[paramIndex];

        if (type == Body.BodyType.DETECT) {
            String contentType = ctx.getAcceptableContentType();
            if (contentType != null) {
                contentType = contentType.toLowerCase();
                if (contentType.indexOf("json") >= 0) {
                    type = Body.BodyType.JSON;
                } else if (contentType.indexOf("xml") >= 0) {
                    type = Body.BodyType.XML;
                } else if (contentType.indexOf("properties") >= 0) {
                    type = Body.BodyType.PROPERTIES;
                } else {
                    type = Body.BodyType.TEXT;
                }
            } else {
                type = Body.BodyType.JSON;
            }
        }

        try {
            switch (type) {
                case JSON:
                    args[paramIndex] = context.bind(Plugin.JSON.get().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), classType, null));
                case XML:
                    args[paramIndex] = context.bind(Plugin.XML.get().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), classType, null));
                case PROPERTIES:
                    Properties properties = new Properties();
                    properties.load(new StringReader(ctx.getBody().toString()));
                    args[paramIndex] = context.bind(properties);
                default:
                    args[paramIndex] = context.bind(ctx.getBody());
            }
        } catch (ReflectionException e) {
            throw e;
        } catch (Exception e) {
            throw new ReflectionException(e.getMessage(), e);
        }

        return true;
    }

    @Override
    public Class<?> annotation() {
        return Body.class;
    }
}
