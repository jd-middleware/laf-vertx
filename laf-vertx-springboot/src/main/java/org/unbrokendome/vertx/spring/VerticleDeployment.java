package org.unbrokendome.vertx.spring;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static io.vertx.core.DeploymentOptions.*;
import static io.vertx.core.VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;
import static io.vertx.core.VertxOptions.DEFAULT_WORKER_POOL_SIZE;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerticleDeployment {

    @AliasFor("autoDeploy")
    boolean value() default true;

    @AliasFor("value")
    boolean autoDeploy() default true;

    boolean worker() default DEFAULT_WORKER;

    boolean ha() default DEFAULT_HA;

    int instances() default DEFAULT_INSTANCES;

    String workerPoolName() default "";

    int workerPoolSize() default DEFAULT_WORKER_POOL_SIZE;

    long maxWorkerExecuteTime() default DEFAULT_MAX_WORKER_EXECUTE_TIME;
}
