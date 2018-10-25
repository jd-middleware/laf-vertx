package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import org.springframework.core.Ordered;

import java.util.function.Supplier;


/**
 * 执行器注册Bean
 */
public class VerticleRegistrationBean implements VerticleRegistration, Ordered {

    protected Supplier<Verticle> supplier;
    protected DeploymentOptions deploymentOptions;
    protected int order;
    protected String verticleName;
    protected Class verticleClass;

    public VerticleRegistrationBean() {
    }

    public VerticleRegistrationBean(Verticle verticle) {
        this(verticle, null);
    }

    public VerticleRegistrationBean(Verticle verticle, DeploymentOptions deploymentOptions) {
        setVerticle(verticle);
        this.deploymentOptions = deploymentOptions != null ? deploymentOptions : this.deploymentOptions;
    }

    public VerticleRegistrationBean(Supplier<Verticle> supplier) {
        this.supplier = supplier;
    }

    public VerticleRegistrationBean(Supplier<Verticle> supplier, DeploymentOptions deploymentOptions) {
        this.supplier = supplier;
        this.deploymentOptions = deploymentOptions;
    }

    public VerticleRegistrationBean(String verticleName) {
        this.verticleName = verticleName;
    }

    public VerticleRegistrationBean(String verticleName, DeploymentOptions deploymentOptions) {
        this(verticleName);
        this.deploymentOptions = deploymentOptions;
    }

    @Override
    public Supplier<Verticle> getSupplier() {
        return supplier;
    }

    public VerticleRegistrationBean setSupplier(Supplier<Verticle> supplier) {
        this.supplier = supplier;
        return this;
    }

    public VerticleRegistrationBean setVerticle(Verticle verticle) {
        this.supplier = verticle == null ? null : () -> verticle;
        this.deploymentOptions = deploymentOptions == null && verticle != null
                && verticle instanceof DeployableVerticle
                ? ((DeployableVerticle) verticle).getDeploymentOptions()
                : deploymentOptions;
        this.order = verticle != null && verticle instanceof Ordered ? ((Ordered) verticle).getOrder() : 0;
        this.verticleClass = verticle != null ? verticle.getClass() : null;
        return this;
    }

    @Override
    public String getVerticleName() {
        return verticleName;
    }

    public VerticleRegistrationBean setVerticleName(String verticleName) {
        this.verticleName = verticleName;
        return this;
    }

    @Override
    public DeploymentOptions getDeploymentOptions() {
        return deploymentOptions;
    }

    public VerticleRegistrationBean setDeploymentOptions(DeploymentOptions deploymentOptions) {
        this.deploymentOptions = deploymentOptions;
        return this;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public VerticleRegistrationBean setOrder(int order) {
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        if (verticleName != null) {
            return verticleName;
        } else if (verticleClass != null) {
            return verticleClass.getName();
        } else {
            return super.toString();
        }
    }
}
