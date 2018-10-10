package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;

import java.util.function.Supplier;

/**
 * 执行器注册
 */
public interface VerticleRegistration {

    /**
     * 获取执行器
     *
     * @return
     */
    default Verticle getVerticle() {
        return null;
    }

    /**
     * 返回执行器提供者
     *
     * @return
     */
    default Supplier<Verticle> getSupplier() {
        return null;
    }

    /**
     * 执行器名称
     *
     * @return
     */
    default String getVerticleName() {
        return null;
    }

    /**
     * 部署选项
     *
     * @return
     */
    default DeploymentOptions getDeploymentOptions() {
        return null;
    }
}
