package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.Plugin;
import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.Body;
import com.jd.laf.web.vertx.annotation.Body.BodyType;
import io.vertx.ext.web.RoutingContext;

import java.io.StringReader;
import java.util.Properties;

/**
 * Body绑定
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
                    return context.bind(Plugin.JSON.get().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), context.getType(), null));
                case XML:
                    return context.bind(Plugin.XML.get().getUnmarshaller().unmarshall(
                            ctx.getBodyAsString(), context.getType(), null));
                case PROPERTIES:
                    Properties properties = new Properties();
                    properties.load(new StringReader(ctx.getBody().toString()));
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
