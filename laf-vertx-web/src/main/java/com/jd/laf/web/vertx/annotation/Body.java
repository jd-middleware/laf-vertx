package com.jd.laf.web.vertx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Body {

    /**
     * 数据类型
     *
     * @return
     */
    BodyType type() default BodyType.DETECT;

    /**
     * 数据类型
     */
    enum BodyType {
        /**
         * 自动检查
         */
        DETECT,
        /**
         * 普通文本
         */
        TEXT,
        /**
         * JSON格式
         */
        JSON,
        /**
         * XML格式
         */
        XML,
    }
}

