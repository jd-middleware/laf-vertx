package com.jd.laf.ignite.spring;

import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * Ignite配置器
 */
public interface IgniteConfigurer {
    /**
     * 配置构造器
     *
     * @param configuration
     */
    void configure(IgniteConfiguration configuration);
}
