package com.jd.laf.web.springboot.starter;

import com.jd.laf.web.vertx.RoutingVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unbrokendome.vertx.spring.EnableVertx;
import org.unbrokendome.vertx.spring.VerticleRegistrationBean;

import static com.jd.laf.web.vertx.RoutingVerticle.DEFAULT_PORT;
import static com.jd.laf.web.vertx.RoutingVerticle.DEFAULT_ROUTING_CONFIG_FILE;


/**
 * Created by yangyang115 on 18-9-27.
 */
@Configuration
@EnableConfigurationProperties(VertxWebAutoConfiguration.VertxWebProperties.class)
@EnableVertx
public class VertxWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoutingVerticle.class)
    public VerticleRegistrationBean routingVerticle(
            org.springframework.core.env.Environment environment,
            ApplicationContext context,
            VertxWebProperties webProperties) {
        return new VerticleRegistrationBean(() -> new RoutingVerticle(
                new SpringEnvironment(environment, context),
                webProperties.http, webProperties.file),
                webProperties.routing);
    }

    @ConfigurationProperties(prefix = "vertx")
    public static class VertxWebProperties {
        @NestedConfigurationProperty
        protected DeploymentOptions routing = new DeploymentOptions();

        protected String file = DEFAULT_ROUTING_CONFIG_FILE;

        @NestedConfigurationProperty
        protected HttpServerOptions http = new HttpServerOptions().setPort(DEFAULT_PORT);

        public DeploymentOptions getRouting() {
            return routing;
        }

        public void setRouting(DeploymentOptions routing) {
            this.routing = routing;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public HttpServerOptions getHttp() {
            return http;
        }

        public void setHttp(HttpServerOptions http) {
            this.http = http;
        }
    }

}
