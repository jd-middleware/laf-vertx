package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.marshaller.JsonProviders;
import com.jd.laf.binding.marshaller.XmlProviders;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.Body;
import com.jd.laf.web.vertx.annotation.Body.BodyType;
import io.vertx.ext.web.RoutingContext;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Body绑定
 */
public class BodyBinder implements Binder {
    @Override
    public boolean bind(final Context context) throws ReflectionException {
        Body annotation = (Body) context.getAnnotation();
        Field field = context.getField();
        RoutingContext ctx = (RoutingContext) context.getSource();

        BodyType type = annotation.type();
        if (type == BodyType.DETECT) {
            String contentType = ctx.getAcceptableContentType();
            if (contentType != null) {
                contentType = contentType.toLowerCase();
                if (contentType.indexOf("json") >= 0) {
                    type = BodyType.JSON;
                } else if (contentType.indexOf("xml") >= 0) {
                    type = BodyType.XML;
                } else if (contentType.indexOf("properties") >= 0) {
                    type = BodyType.PROPERTIES;
                } else {
                    type = BodyType.TEXT;
                }
            } else {
                type = BodyType.JSON;
            }
        }

        try {
            switch (type) {
                case JSON:
                    return context.bind(JsonProviders.getPlugin().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), field.getType(), null));
                case XML:
                    return context.bind(XmlProviders.getPlugin().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), field.getType(), null));
                case PROPERTIES:
                    byte[] data = ctx.getBody().getBytes();
                    Properties properties = new Properties();
                    properties.load(new ByteArrayInputStream(data));
                    return context.bind(properties);
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
