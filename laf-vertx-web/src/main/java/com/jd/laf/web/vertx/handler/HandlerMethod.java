package com.jd.laf.web.vertx.handler;

import java.lang.reflect.Method;

/**
 * 命令处理器执行方法
 */
public class HandlerMethod {

    /**
     * 处理器类型
     */
    public Type type;
    /**
     * 处理器完整名称
     */
    public String name;
    /**
     * 执行方法
     */
    public Method method;

    HandlerMethod() {

    }

    public HandlerMethod(String name, Type type, Method method, String path) {
        this.name = name;
        this.type = type;
        this.method = method;
    }

    public HandlerMethod(String name) {
        this.name = name;
        this.type = Type.DEFAULT;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public enum Type {
        /** 默认为execute方法 */
        DEFAULT,
        /** 根据@path定位方法 */
        PATH
    }
}
