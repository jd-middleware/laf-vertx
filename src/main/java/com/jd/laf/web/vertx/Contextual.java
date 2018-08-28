package com.jd.laf.web.vertx;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上下文接口
 */
public interface Contextual {

    /**
     * 获取指定类型的参数
     *
     * @param name  参数名称
     * @param clazz 类型
     * @return 参数对象
     */
    <T> T getObject(String name, Class<T> clazz);

    /**
     * 获取对象参数
     *
     * @param name 参数名称
     * @return 参数对象
     */
    Object getObject(String name);

    /**
     * 获取字符串参数
     *
     * @param name 名称
     * @return 字符串参数
     */
    String getString(String name);

    /**
     * 获取字符串参数，如果为空字符串则返回默认值
     *
     * @param name 名称
     * @param def  默认值
     * @return 字符串参数
     */
    String getString(String name, String def);

    /**
     * 获取字节参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
    Byte getByte(String name);

    /**
     * 获取字节参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    Byte getByte(String name, Byte def);

    /**
     * 获取短整数参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
    Short getShort(String name);

    /**
     * 获取短整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    Short getShort(String name, Short def);

    /**
     * 获取整数参数
     *
     * @param name 名称
     * @return 整数
     */
    Integer getInteger(String name);

    /**
     * 获取整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 整数
     */
    Integer getInteger(String name, Integer def);

    /**
     * 获取长整形参数
     *
     * @param name 名称
     * @return 长整形参数
     */
    Long getLong(String name);

    /**
     * 获取长整形参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 长整形参数
     */
    Long getLong(String name, Long def);

    /**
     * 获取单精度浮点数参数
     *
     * @param name 名称
     * @return 浮点数参数
     */
    Float getFloat(String name);

    /**
     * 获取单精度浮点数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    Float getFloat(String name, Float def);

    /**
     * 获取双精度浮点数参数
     * z
     *
     * @param name 名称
     * @return 浮点数参数
     */
    Double getDouble(String name);

    /**
     * 获取双精度浮点数参数
     * z
     *
     * @param name 名称
     * @param def  默认值
     * @return 浮点数参数
     */
    Double getDouble(String name, Double def);

    /**
     * 获取布尔值
     *
     * @param name 名称
     * @return 布尔值
     */
    Boolean getBoolean(String name);

    /**
     * 获取布尔值
     *
     * @param name 名称
     * @param def  默认值
     * @return 布尔值
     */
    Boolean getBoolean(String name, Boolean def);

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param name 参数名称
     * @return 参数值
     */
    Date getDate(String name);

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param name 参数名称
     * @param def  默认值
     * @return 参数值
     */
    Date getDate(String name, Date def);

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @return 参数值
     */
    Date getDate(String name, SimpleDateFormat format);

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    Date getDate(String name, SimpleDateFormat format, Date def);

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param name   参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    Date getDate(String name, String format, String def);

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    Byte getPositive(String name, Byte def);

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    Short getPositive(String name, Short def);

    /**
     * 获取正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 正整数
     */
    Integer getPositive(String name, Integer def);

    /**
     * 获取长正整数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 长正整数
     */
    Long getPositive(String name, Long def);

    /**
     * 获取短整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    Short getNatural(String name, Short def);

    /**
     * 获取短整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    Byte getNatural(String name, Byte def);

    /**
     * 获取整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    Integer getNatural(String name, Integer def);

    /**
     * 获取长整数自然数参数
     *
     * @param name 名称
     * @param def  默认值
     * @return 自然数
     */
    Long getNatural(String name, Long def);
}
