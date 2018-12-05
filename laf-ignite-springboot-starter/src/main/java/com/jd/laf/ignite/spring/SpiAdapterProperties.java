package com.jd.laf.ignite.spring;

import org.apache.ignite.spi.IgniteSpiAdapter;

public abstract class SpiAdapterProperties<T extends IgniteSpiAdapter> {

    protected boolean failureDetectionTimeoutEnabled = true;

    public boolean isFailureDetectionTimeoutEnabled() {
        return failureDetectionTimeoutEnabled;
    }

    public void setFailureDetectionTimeoutEnabled(boolean failureDetectionTimeoutEnabled) {
        this.failureDetectionTimeoutEnabled = failureDetectionTimeoutEnabled;
    }

    /**
     * 配置
     *
     * @param spi 插件
     */
    public void configure(T spi) {
        spi.failureDetectionTimeoutEnabled(failureDetectionTimeoutEnabled);
    }
}
