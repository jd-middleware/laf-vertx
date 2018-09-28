package com.jd.laf.vertx.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(VertxConfigurationImportSelector.class)
@SuppressWarnings("unused")
public @interface EnableVertx {
    boolean deployVerticles() default true;
}
