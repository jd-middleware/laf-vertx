package org.unbrokendome.vertx.spring.boot;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.unbrokendome.vertx.spring.SpringVertx;
import org.unbrokendome.vertx.spring.VerticleDeploymentConfiguration;
import org.unbrokendome.vertx.spring.VertxConfiguration;
import org.unbrokendome.vertx.spring.actuator.metrics.VertxMetricsConfiguration;


@Configuration
@EnableConfigurationProperties(VertxProperties.class)
public class VertxAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(SpringVertx.class)
    @Import(VertxConfiguration.class)
    public static class SpringVertxAutoConfiguration {
    }


    @Configuration
    @ConditionalOnProperty(prefix = "vertx", name = "auto-deploy-verticles", matchIfMissing = true)
    @Import(VerticleDeploymentConfiguration.class)
    public static class VerticleDeploymentAutoConfiguration {
    }

    @Configuration
    @ConditionalOnClass(MeterRegistry.class)
    @Import(VertxMetricsConfiguration.class)
    public static class VertxMetricsAutoConfiguration {
    }

}
