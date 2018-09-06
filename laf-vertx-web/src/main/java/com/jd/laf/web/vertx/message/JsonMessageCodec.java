package com.jd.laf.web.vertx.message;

import com.jd.laf.binding.marshaller.JsonProviders;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;

import java.nio.charset.StandardCharsets;

/**
 * JSON编解码
 *
 * @param <T>
 */
public class JsonMessageCodec<T> implements CustomCodec<T> {

    protected Class<T> type;

    public JsonMessageCodec(Class<T> type) {
        this.type = type;
    }

    @Override
    public void encodeToWire(final Buffer buffer, final T message) {
        if (message == null) {
            return;
        }
        try {
            String value = JsonProviders.getPlugin().getMarshaller().marshall(message);
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            buffer.appendInt(length);
            buffer.appendBytes(bytes);
        } catch (Exception e) {
            throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
        }
    }

    @Override
    public T decodeFromWire(final int position, final Buffer buffer) {
        int length = buffer.getInt(position);
        int start = position + 4;

        String value = buffer.getString(start, start + length);
        try {
            return JsonProviders.getPlugin().getUnmarshaller().unmarshall(value, type, null);
        } catch (Exception e) {
            throw new DecodeException("Failed to decode: " + e.getMessage(), e);
        }
    }

    @Override
    public T transform(T message) {
        return message;
    }

    @Override
    public Class<T> type() {
        return type;
    }
}
