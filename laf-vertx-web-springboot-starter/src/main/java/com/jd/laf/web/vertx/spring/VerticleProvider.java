package com.jd.laf.web.vertx.spring;

import org.springframework.core.Ordered;

/**
 * Verticle提供者
 */
public interface VerticleProvider extends Ordered {

    /**
     * 获取Verticle元数据信息
     *
     * @return
     */
    VerticleMeta getVerticleMeta();

    default int getOrder() {
        return 0;
    }
}
