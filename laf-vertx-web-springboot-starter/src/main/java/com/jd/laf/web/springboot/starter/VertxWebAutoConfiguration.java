package com.jd.laf.web.springboot.starter;

import com.jd.laf.web.vertx.RouteProvider;
import com.jd.laf.web.vertx.RoutingVerticle;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unbrokendome.vertx.spring.VerticleRegistrationBean;

import java.util.List;


/**
 * Created by yangyang115 on 18-9-27.
 */
@Configuration
@EnableConfigurationProperties(VertxWebProperties.class)
public class VertxWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoutingVerticle.class)
    public VerticleRegistrationBean routingVerticle(
            org.springframework.core.env.Environment environment,
            ApplicationContext context,
            VertxWebProperties webProperties,
            ObjectProvider<List<RouteProvider>> provider) {
        return new VerticleRegistrationBean(() -> new RoutingVerticle(
                new SpringEnvironment(environment, context),
                webProperties.getHttp().toHttpServerOptions(),
                webProperties.getFile(),
                provider.getIfAvailable()),
                webProperties.getRouting().toDeploymentOptions());
    }

}
