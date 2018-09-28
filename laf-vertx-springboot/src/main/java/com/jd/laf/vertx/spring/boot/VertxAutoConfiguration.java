package com.jd.laf.vertx.spring.boot;

import com.jd.laf.vertx.spring.SpringVertx;
import com.jd.laf.vertx.spring.VerticleDeploymentConfiguration;
import com.jd.laf.vertx.spring.VertxConfiguration;
import com.jd.laf.vertx.spring.actuator.metrics.VertxMetricsConfiguration;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


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
    @ConditionalOnBean({CounterService.class, GaugeService.class})
    @Import(VertxMetricsConfiguration.class)
    public static class VertxMetricsAutoConfiguration {
    }
}
