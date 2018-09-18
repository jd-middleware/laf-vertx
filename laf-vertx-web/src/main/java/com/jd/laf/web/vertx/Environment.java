package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import io.vertx.core.Vertx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 系统上下文
 */
public class Environment {

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
     * 模板
     */
    public static final String TEMPLATE = "template";

    /**
     * 命令对象池大小
     */
    public static final String COMMAND_POOL_CAPACITY = "command.pool.capacity";

    /**
     * 命令对象池初始化大小
     */
    public static final String COMMAND_POOL_INITIALIZE_SIZE = "command.pool.initialize.size";

    /**
     * 本地会话
     */
    public static final String SESSION_LOCAL = "session.local";
    /**
     * 会话名称
     */
    public static final String SESSION_NAME = "session.name";
    /**
     * 会话的Cookie名称
     */
    public static final String SESSION_COOKIE_NAME = "session.cookie.name";
    /**
     * 会话超时时间
     */
    public static final String SESSION_TIMEOUT = "session.timeout";
    /**
     * 用户的键
     */
    public static final String USER_KEY = "userDetail";
    /**
     * 远程IP
     */
    public static final String REMOTE_IP = "remoteIp";

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
     * 模板引擎类型
     */
    public static final String TEMPLATE_TYPE = "template.type";

    /**
     * 模板目录
     */
    public static final String TEMPLATE_DIRECTORY = "template.directory";

    /**
     * 模板应答内容
     */
    public static final String TEMPLATE_CONTENT_TYPE = "template.contentType";

    /**
     * 模板文件扩展名
     */
    public static final String TEMPLATE_EXTENSION = "template.extension";

    /**
     * 模板文件缓存大小
     */
    public static final String TEMPLATE_CACHE_SIZE = "template.cache.size";

    // 参数
    protected Map<String, Object> parameters;

    protected Vertx vertx;

    public Environment() {
        this(null, null);
    }

    public Environment(Map<String, Object> parameters) {
        this(null, parameters);
    }

    public Environment(Vertx vertx, Map<String, Object> parameters) {
        this.vertx = vertx;
        this.parameters = parameters == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parameters);
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void foreach(final BiConsumer<String, Object> consumer) {
        if (parameters != null && consumer != null) {
            parameters.forEach(consumer);
        }
    }

    /**
     * 获取指定类型的参数
     *
     * @param name  参数名称
     * @param clazz 类型
     * @return 参数对象
     */
    public <T> T getObject(final String name, final Class<T> clazz) {
        return (T) getObject(name);
    }

    /**
     * 获取对象参数
     *
     * @param name 参数名称
     * @return 参数对象
     */
    public <T> T getObject(final String name) {
        return (T) (parameters == null ? null : parameters.get(name));
    }

    /**
     * 获取字符串参数
     *
     * @param name 名称
     * @return 字符串参数
     */
    public String getString(final String name) {
        Object result = getObject(name);
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    /**
     * 获取字符串参数，如果为空字符串则返回默认值
     *
     * @param name 名称
     * @param def  默认值
     * @return 字符串参数
     */
    public String getString(final String name, final String def) {
        String value = getString(name);
        return value == null || value.isEmpty() ? def : value;
    }

    /**
     * 获取字节参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
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

    /**
     * 获取字节参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    public Byte getByte(final String name, final Byte def) {
        Byte result = getByte(name);
        return result == null ? def : result;
    }

    /**
     * 获取短整数参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
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

    /**
     * 获取短整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    public Short getShort(final String name, final Short def) {
        Short result = getShort(name);
        return result == null ? def : result;
    }

    /**
     * 获取整数参数
     *
     * @param name 名称
     * @return 整数
     */
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

    /**
     * 获取整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 整数
     */
    public Integer getInteger(final String name, final Integer def) {
        Integer result = getInteger(name);
        return result == null ? def : result;
    }

    /**
     * 获取长整形参数
     *
     * @param name 名称
     * @return 长整形参数
     */
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

    /**
     * 获取长整形参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 长整形参数
     */
    public Long getLong(final String name, final Long def) {
        Long result = getLong(name);
        return result == null ? def : result;
    }

    /**
     * 获取单精度浮点数参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
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

    /**
     * 获取单精度浮点数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    public Float getFloat(final String name, final Float def) {
        Float result = getFloat(name);
        return result == null ? def : result;
    }

    /**
     * 获取双精度浮点数参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
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

    /**
     * 获取双精度浮点数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    public Double getDouble(final String name, final Double def) {
        Double result = getDouble(name);
        return result == null ? def : result;
    }

    /**
     * 获取布尔值
     *
     * @param name 名称
     * @return 布尔值
     */
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

    /**
     * 获取布尔值
     *
     * @param name 名称
     * @param def  默认值
     * @return 布尔值
     */
    public Boolean getBoolean(final String name, final Boolean def) {
        Boolean result = getBoolean(name);
        return result == null ? def : result;
    }

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param name 参数名称
     * @return 参数值
     */
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

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param name 参数名称
     * @param def  默认值
     * @return 参数值
     */
    public Date getDate(final String name, final Date def) {
        Date result = getDate(name);
        return result == null ? def : result;
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @return 参数值
     */
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

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    public Date getDate(final String name, final SimpleDateFormat format, final Date def) {
        Date result = getDate(name, format);
        return result == null ? def : result;
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    public Date getDate(final String name, final String format, final String def) {
        SimpleDateFormat sdf = format == null || format.isEmpty() ? null : new SimpleDateFormat(format);
        Date result = getDate(name, sdf);
        try {
            return result == null ? (sdf == null || def == null || def.isEmpty() ? null : sdf.parse(def)) : result;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    public Byte getPositive(final String name, final Byte def) {
        Byte result = getByte(name, def);
        return result != null && result <= 0 ? def : result;
    }

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    public Short getPositive(final String name, final Short def) {
        Short result = getShort(name, def);
        return result != null && result <= 0 ? def : result;
    }

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    public Integer getPositive(final String name, final Integer def) {
        Integer result = getInteger(name, def);
        return result != null && result <= 0 ? def : result;
    }

    /**
     * 获取长正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 长正整数
     */
    public Long getPositive(final String name, final Long def) {
        Long result = getLong(name, def);
        return result != null && result <= 0 ? def : result;
    }

    /**
     * 获取短整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    public Short getNatural(final String name, final Short def) {
        Short result = getShort(name, def);
        return result != null && result < 0 ? def : result;
    }

    /**
     * 获取短整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    public Byte getNatural(final String name, final Byte def) {
        Byte result = getByte(name, def);
        return result != null && result < 0 ? def : result;
    }

    /**
     * 获取整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    public Integer getNatural(final String name, final Integer def) {
        Integer result = getInteger(name, def);
        return result != null && result < 0 ? def : result;
    }

    /**
     * 获取长整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    public Long getNatural(final String name, final Long def) {
        Long result = getLong(name, def);
        return result != null && result < 0 ? def : result;
    }

    /**
     * 存取键值
     *
     * @param name
     * @param obj
     */
    public void put(final String name, final Object obj) {
        if (parameters != null) {
            parameters.put(name, obj);
        }
    }

    /**
     * 绑定并且验证
     *
     * @param target
     * @throws Exception
     */
    public <T> T setup(final T target) throws Exception {
        if (target != null) {
            Binding.bind(this, target);
            Validates.validate(target);
            if (target instanceof EnvironmentAware) {
                ((EnvironmentAware) target).setup(this);
            }
        }
        return target;
    }

}
