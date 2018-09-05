package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.marshaller.JsonProviders;
import com.jd.laf.binding.marshaller.XmlProviders;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.Body;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;

/**
 * Body绑定
 */
public class BodyBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        Body annotation = (Body) context.getAnnotation();
        Field field = context.getField();
        RoutingContext ctx = (RoutingContext) context.getSource();
        try {
            switch (annotation.type()) {
                case JSON:
                    return context.bind(JsonProviders.getPlugin().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), field.getType(), null));
                case XML:
                    return context.bind(XmlProviders.getPlugin().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), field.getType(), null));
                default:
                    return context.bind(ctx.getBody());
            }
        } catch (ReflectionException e) {
            throw e;
        } catch (Exception e) {
            throw new ReflectionException(e.getMessage(), e);
        }
    }

    @Override
    public Class<?> annotation() {
        return Body.class;
    }
}
