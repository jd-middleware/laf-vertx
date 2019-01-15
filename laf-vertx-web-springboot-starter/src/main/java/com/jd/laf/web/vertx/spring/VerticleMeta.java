package com.jd.laf.web.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;

import java.util.function.Supplier;


/**
 * Verticle元数据信息
 */
public class VerticleMeta<T extends Verticle> {

    protected Supplier<T> supplier;
    protected DeploymentOptions deploymentOptions;
    protected String name;

    public VerticleMeta() {
    }

    public VerticleMeta(T verticle) {
        this(verticle, null);
    }

    public VerticleMeta(T verticle, DeploymentOptions deploymentOptions) {
        this.supplier = verticle == null ? null : () -> verticle;
        this.deploymentOptions = deploymentOptions;
    }

    public VerticleMeta(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public VerticleMeta(Supplier<T> supplier, DeploymentOptions deploymentOptions) {
        this.supplier = supplier;
        this.deploymentOptions = deploymentOptions;
    }

    public VerticleMeta(String name) {
        this.name = name;
    }

    public VerticleMeta(String name, DeploymentOptions deploymentOptions) {
        this(name);
        this.deploymentOptions = deploymentOptions;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public DeploymentOptions getDeploymentOptions() {
        return deploymentOptions;
    }

    public void setDeploymentOptions(DeploymentOptions deploymentOptions) {
        this.deploymentOptions = deploymentOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
