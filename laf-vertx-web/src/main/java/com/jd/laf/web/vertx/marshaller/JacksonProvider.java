package com.jd.laf.web.vertx.marshaller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.jd.laf.binding.marshaller.JsonProvider;
import com.jd.laf.binding.marshaller.Marshaller;
import com.jd.laf.binding.marshaller.Unmarshaller;
import io.vertx.core.json.Json;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Jackson提供者
 */
public class JacksonProvider implements JsonProvider {

    static {
        Json.mapper.setSerializationInclusion(NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }


    @Override
    public Unmarshaller getUnmarshaller() {
        return JacksonUnmarshaller.INSTANCE;
    }

    @Override
    public Marshaller getMarshaller() {
        return JacksonMarshaller.INSTANCE;
    }

    /**
     * Jackson反序列化器
     */
    protected static class JacksonUnmarshaller implements Unmarshaller {
        public static final Unmarshaller INSTANCE = new JacksonUnmarshaller();

        @Override
        public <T> T unmarshall(final String value, final Class<T> clazz, final String format) {
            return Json.decodeValue(value, clazz);
        }

        @Override
        public <T> T unmarshall(final String value, final Class<T> clazz) {
            return Json.decodeValue(value, clazz);
        }
    }

    /**
     * Jackson序列化器
     */
    protected static class JacksonMarshaller implements Marshaller {

        public static final Marshaller INSTANCE = new JacksonMarshaller();

        @Override
        public String marshall(final Object target) {
            if (target == null) {
                return null;
            }
            return Json.encode(target);
        }
    }
}
