package com.jd.laf.web.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;

import java.util.function.Supplier;

/**
 * 路由Verticle提供者
 */
public class RoutingVerticleProvider implements VerticleProvider {

    protected Supplier<Verticle> supplier;

    protected DeploymentOptions deploymentOptions;

    public RoutingVerticleProvider(Supplier<Verticle> supplier) {
        this.supplier = supplier;
    }

    public RoutingVerticleProvider(Supplier<Verticle> supplier, DeploymentOptions deploymentOptions) {
        this.supplier = supplier;
        this.deploymentOptions = deploymentOptions;
    }

    @Override
    public VerticleMeta getVerticleMeta() {
        return new VerticleMeta(supplier, deploymentOptions);
    }

}
