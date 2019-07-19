/**
 *
 */
package com.jd.laf.web.vertx.parameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 参数工具类
 *
 * @author hexiaofeng
 */
public final class Parameter {


    // 参数
    protected final ParameterSupplier supplier;

    public Parameter(ParameterSupplier supplier) {
        this.supplier = supplier;
    }

    public static Parameter valueOf(final ParameterSupplier supplier) {
        return new Parameter(supplier);
    }

    /**
     * 获取字符串参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public String getString(final String key) {
        return supplier == null ? null : supplier.get(key);
    }

    /**
     * 获取字符串数组参数值
     *
     * @param key       参数名称
     * @param delimiter 分隔符
     * @return 参数值
     */
    public String[] getStrings(final String key, final char delimiter) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return null;
        }
        List<String> result = new LinkedList<String>();
        int len = value.length();
        int start = 0;
        for (int i = 0; i < len; i++) {
            if (value.charAt(i) == delimiter) {
                if (i > start) {
                    result.add(value.substring(start, i));
                }
                start = i + 1;
            }
        }
        if (start < len) {
            result.add(value.substring(start, len));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * 获取字符串参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public String getString(final String key, final String def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        return value;
    }

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Date getDate(final String key, final Date def) {
        Long value = getLong(key, null);
        if (value == null) {
            return def;
        }
        return new Date(value);
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param key    参数名称
     * @param format 日期格式
     * @return 参数值
     */
    public Date getDate(final String key, final SimpleDateFormat format) {
        return getDate(key, format, null);
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param key    参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    public Date getDate(final String key, final SimpleDateFormat format, final Date def) {
        String value = getString(key);
        if (value == null || value.isEmpty() || format == null) {
            return def;
        }
        try {
            return format.parse(key);
        } catch (ParseException e) {
            return def;
        }
    }

    /**
     * 获取单精度浮点数参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Float getFloat(final String key) {
        return getFloat(key, null);
    }

    /**
     * 获取单精度浮点数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Float getFloat(final String key, final Float def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取双精度浮点数参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Double getDouble(final String key) {
        return getDouble(key, null);
    }

    /**
     * 获取双精度浮点数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Double getDouble(final String key, final Double def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取长整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Long getLong(final String key) {
        return getLong(key, null);
    }

    /**
     * 获取长整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getLong(final String key, final Long def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Integer getInteger(final String key) {
        return getInteger(key, null);
    }

    /**
     * 获取整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getInteger(final String key, final Integer def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取短整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Short getShort(final String key) {
        return getShort(key, null);
    }

    /**
     * 获取短整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getShort(final String key, final Short def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取字节参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Byte getByte(final String key) {
        return getByte(key, null);
    }


    /**
     * 获取字节参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getByte(final String key, final Byte def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取不二参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Boolean getBoolean(final String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取不二参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Boolean getBoolean(final String key, final Boolean def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        } else if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            try {
                return Integer.parseInt(value) != 0;
            } catch (NumberFormatException e) {
            }
        }
        return def;
    }

    /**
     * 获取长整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getNatural(final String key, final Long def) {
        Long value = getLong(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getNatural(final String key, final Integer def) {
        Integer value = getInteger(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取短整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getNatural(final String key, final Short def) {
        Short value = getShort(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取字节自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getNatural(final String key, final Byte def) {
        Byte value = getByte(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取长整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getPositive(final String key, final Long def) {
        Long value = getLong(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getPositive(final String key, final Integer def) {
        Integer value = getInteger(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取短整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getPositive(final String key, final Short def) {
        Short value = getShort(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取字节正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getPositive(final String key, final Byte def) {
        Byte value = getByte(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 判断参数是否存在
     *
     * @param key 参数名称
     * @return true if exist or false
     */
    public boolean contains(final String key) {
        String value = getString(key);
        return value != null && !value.isEmpty();
    }

}
