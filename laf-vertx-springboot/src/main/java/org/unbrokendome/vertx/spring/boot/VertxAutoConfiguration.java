package org.unbrokendome.vertx.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.unbrokendome.vertx.spring.SpringVertx;
import org.unbrokendome.vertx.spring.VerticleDeploymentConfiguration;
import org.unbrokendome.vertx.spring.VertxConfiguration;

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
}
