package com.jd.laf.web.vertx;

import io.vertx.core.Vertx;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态配置集处理上下文
 */
public class Context implements Serializable, Cloneable, Contextual {

    /**
     * WEB根路径
     */
    public static final String WEB_ROOT = "web.root";

    /**
     * 默认页
     */
    public static final String INDEX_PAGE = "web.indexPage";

    /**
     * Body大小限制
     */
    public static final String BODY_LIMIT = "web.bodyLimit";

    /**
     * 上传目录
     */
    public static final String UPLOAD_DIR = "web.uploadDir";
    /**
     * 验证器
     */
    public static final String VALIDATOR = "validator";

    /**
     * 本地会话
     */
    public static final String SESSION_LOCAL = "session.local";
    /**
     * 本地会话名称
     */
    public static final String SESSION_NAME = "session.name";

    /**
     * 认证提供者
     */
    public static final String AUTH_PROVIDER = "auth.provider";

    /**
     * 认证重定向URL
     */
    public static final String AUTH_REDIRECT_URL = "auth.redirect.url";

    /**
     * 认证跳回的URL参数
     */
    public static final String AUTH_RETURN_URL_PARAM = "auth.return.urlParam";

    /**
     * 用户参数
     */
    public static final String AUTH_USER_PARAM = "auth.userParam";

    /**
     * 密码参数
     */
    public static final String AUTH_PASSWORD_PARAM = "auth.passwordParam";

    /**
     * 模板引擎
     */
    public static final String TEMPLATE_ENGINE = "template.engine";

    /**
     * 模板目录
     */
    public static final String TEMPLATE_DIRECTORY = "template.directory";

    /**
     * 模板应答内容
     */
    public static final String TEMPLATE_CONTENT_TYPE = "template.contentType";

    // 参数
    protected ConcurrentHashMap<String, Object> parameters = new ConcurrentHashMap<String, Object>();

    protected Vertx vertx;

    public Context() {

    }

    public Context(Vertx vertx, Map<String, Object> parameters) {
        this.vertx = vertx;
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
    }

    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public <T> T getObject(final String name, final Class<T> clazz) {
        return (T) getObject(name);
    }

    @Override
    public Object getObject(final String name) {
        return parameters.get(name);
    }

    @Override
    public String getString(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    @Override
    public String getString(final String name, final String def) {
        String value = getString(name);
        return value == null || value.isEmpty() ? def : value;
    }

    @Override
    public Byte getByte(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).byteValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Byte.parseByte(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Byte getByte(final String name, final Byte def) {
        Byte result = getByte(name);
        return result == null ? def : result;
    }

    @Override
    public Short getShort(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).shortValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Short.parseShort(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Short getShort(final String name, final Short def) {
        Short result = getShort(name);
        return result == null ? def : result;
    }

    @Override
    public Integer getInteger(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).intValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Integer getInteger(final String name, final Integer def) {
        Integer result = getInteger(name);
        return result == null ? def : result;
    }

    @Override
    public Long getLong(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).longValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Long getLong(final String name, final Long def) {
        Long result = getLong(name);
        return result == null ? def : result;
    }

    @Override
    public Float getFloat(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).floatValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Float.parseFloat(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Float getFloat(final String name, final Float def) {
        Float result = getFloat(name);
        return result == null ? def : result;
    }

    @Override
    public Double getDouble(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).doubleValue();
        } else if (result instanceof CharSequence || result instanceof Character) {
            String text = result.toString();
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Double getDouble(final String name, final Double def) {
        Double result = getDouble(name);
        return result == null ? def : result;
    }

    @Override
    public Boolean getBoolean(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Number) {
            return ((Number) result).longValue() != 0;
        } else if (result instanceof Boolean) {
            return (Boolean) result;
        } else if (result instanceof Character) {
            return ((Character) result) != '0';
        } else if (result instanceof CharSequence) {
            String value = result.toString();
            if ("true".equalsIgnoreCase(value)) {
                return true;
            } else if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            try {
                return Long.parseLong(value) != 0;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean getBoolean(final String name, final Boolean def) {
        Boolean result = getBoolean(name);
        return result == null ? def : result;
    }

    @Override
    public Date getDate(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        } else if (result instanceof Date) {
            return (Date) result;
        } else if (result instanceof Number) {
            return new Date(((Number) result).longValue());
        } else if (result instanceof CharSequence) {
            String value = result.toString();
            try {
                return new Date((Long.parseLong(value)));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Date getDate(final String name, final Date def) {
        Date result = getDate(name);
        return result == null ? def : result;
    }

    @Override
    public Date getDate(final String name, final SimpleDateFormat format) {
        Object result = parameters.get(name);
        if (result == null) {
            return null;
        } else if (result instanceof Date) {
            return (Date) result;
        } else if (result instanceof Number) {
            return new Date(((Number) result).longValue());
        } else if (format == null) {
            return null;
        } else if (result instanceof CharSequence) {
            String value = result.toString();
            try {
                return format.parse(value);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Date getDate(final String name, final SimpleDateFormat format, final Date def) {
        Date result = getDate(name, format);
        return result == null ? def : result;
    }

    @Override
    public Date getDate(final String name, final String format, final String def) {
        SimpleDateFormat sdf = format == null || format.isEmpty() ? null : new SimpleDateFormat(format);
        Date result = getDate(name, sdf);
        try {
            return result == null ? (sdf == null || def == null || def.isEmpty() ? null : sdf.parse(def)) : result;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Byte getPositive(final String name, final Byte def) {
        Byte result = getByte(name, def);
        return result != null && result <= 0 ? def : result;
    }

    @Override
    public Short getPositive(final String name, final Short def) {
        Short result = getShort(name, def);
        return result != null && result <= 0 ? def : result;
    }

    @Override
    public Integer getPositive(final String name, final Integer def) {
        Integer result = getInteger(name, def);
        return result != null && result <= 0 ? def : result;
    }

    @Override
    public Long getPositive(final String name, final Long def) {
        Long result = getLong(name, def);
        return result != null && result <= 0 ? def : result;
    }

    @Override
    public Short getNatural(final String name, final Short def) {
        Short result = getShort(name, def);
        return result != null && result < 0 ? def : result;
    }

    @Override
    public Byte getNatural(final String name, final Byte def) {
        Byte result = getByte(name, def);
        return result != null && result < 0 ? def : result;
    }

    @Override
    public Integer getNatural(final String name, final Integer def) {
        Integer result = getInteger(name, def);
        return result != null && result < 0 ? def : result;
    }

    @Override
    public Long getNatural(final String name, final Long def) {
        Long result = getLong(name, def);
        return result != null && result < 0 ? def : result;
    }

    /**
     * 存放键值对
     *
     * @param name  键
     * @param value 值
     * @return 先前的对象
     */
    public Object put(final String name, final Object value) {
        return name == null ? null : parameters.put(name, value);
    }

    /**
     * 存放键值对
     *
     * @param name  键
     * @param value 值
     * @return 先前的对象
     */
    public Object putIfNotNull(final String name, final Object value) {
        return name == null || value == null ? null : parameters.put(name, value);
    }

    /**
     * 存放键值对
     *
     * @param name  键
     * @param value 值
     * @return 先前的对象
     */
    public Object putIfAbsent(final String name, final Object value) {
        return name == null ? null : parameters.putIfAbsent(name, value);
    }

    /**
     * 存放键值
     *
     * @param map 键值对
     */
    public void put(final Map<String, ?> map) {
        if (map != null) {
            parameters.putAll(map);
        }
    }

    /**
     * 存放键值
     *
     * @param map 键值对
     */
    public void putIfAbsent(final Map<String, ?> map) {
        if (map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                parameters.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 存放键值
     *
     * @param context 上下文
     */
    public void put(final Context context) {
        if (context != null) {
            put(context.parameters);
        }
    }

    /**
     * 存放键值
     *
     * @param context 上下文
     */
    public void putIfAbsent(final Context context) {
        if (context != null) {
            putIfAbsent(context.parameters);
        }
    }

    /**
     * 删除参数
     *
     * @param name 参数名称
     * @return 参数值
     */
    public Object remove(final String name) {
        return name == null ? null : parameters.remove(name);
    }

    /**
     * 清理所有参数
     */
    public void remove() {
        parameters.clear();
    }

    /**
     * 转换成Map对象
     *
     * @return Map对象
     */
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(parameters);
    }

    /**
     * 获取迭代器
     *
     * @return 迭代器
     */
    public Iterator<Map.Entry<String, Object>> iterator() {
        return parameters.entrySet().iterator();
    }

    @Override
    public Context clone() {
        Context result = new Context();
        result.put(parameters);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Context context = (Context) o;

        return parameters != null ? parameters.equals(context.parameters) : context.parameters == null;

    }

    @Override
    public int hashCode() {
        return parameters != null ? parameters.hashCode() : 0;
    }

}
