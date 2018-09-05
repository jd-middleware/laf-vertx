package com.jd.laf.web.vertx.parameter;

/**
 * 参数提供者
 */
public interface ParameterSupplier {

    /**
     * 获取参数
     *
     * @param name 参数名称
     * @return
     */
    String get(String name);
}
