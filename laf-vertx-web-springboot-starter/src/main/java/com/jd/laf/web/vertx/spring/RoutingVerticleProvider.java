package com.jd.laf.web.vertx.spring;

import com.jd.laf.web.vertx.RoutingVerticle;
import io.vertx.core.DeploymentOptions;

import java.util.function.Supplier;

/**
 * 路由Verticle提供者
 */
public class RoutingVerticleProvider implements VerticleProvider {

    protected Supplier<RoutingVerticle> supplier;

    protected DeploymentOptions deploymentOptions;

    public RoutingVerticleProvider(Supplier<RoutingVerticle> supplier) {
        this.supplier = supplier;
    }

    public RoutingVerticleProvider(Supplier<RoutingVerticle> supplier, DeploymentOptions deploymentOptions) {
        this.supplier = supplier;
        this.deploymentOptions = deploymentOptions;
    }

    @Override
    public VerticleMeta getVerticleMeta() {
        return new VerticleMeta(supplier, deploymentOptions);
    }

}
