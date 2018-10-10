package com.jd.laf.web.springboot.starter;

import com.jd.laf.web.vertx.Environment;
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

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.laf.web.vertx.RoutingVerticle.DEFAULT_PORT;


/**
 * Created by yangyang115 on 18-9-27.
 */
@Configuration
@EnableConfigurationProperties(VertxWebAutoConfiguration.VertxWebProperties.class)
@EnableVertx
public class VertxWebAutoConfiguration {

    @Resource
    protected VertxWebProperties webProperties;

    @Bean
    @ConditionalOnMissingBean(RoutingVerticle.class)
    public VerticleRegistrationBean routingVerticle(org.springframework.core.env.Environment environment, ApplicationContext context) {
        return new VerticleRegistrationBean(() -> new RoutingVerticle(new SpringEnvironment(environment, context), webProperties.http), webProperties.routing);
    }

    /**
     * Created by yangyang36 on 2018/9/28.
     */
    protected static class SpringEnvironment implements Environment {

        protected ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();

        protected org.springframework.core.env.Environment environment;
        protected ApplicationContext applicationContext;

        public SpringEnvironment(org.springframework.core.env.Environment environment, ApplicationContext applicationContext) {
            this.environment = environment;
            this.applicationContext = applicationContext;
        }

        @Override
        public <T> T getObject(final String name) {
            Object value = params.get(name);
            if (value == null) {
                value = environment.getProperty(name);
                if (value == null) {
                    value = applicationContext.containsBean(name) ? applicationContext.getBean(name) : null;
                }
                if (value != null) {
                    Object exists = params.put(name, value);
                    if (exists != null) {
                        value = exists;
                    }
                }
            }
            return (T) value;
        }

        @Override
        public void put(String name, Object obj) {
            params.put(name, obj);
        }
    }

    @ConfigurationProperties(prefix = "vertx")
    public static class VertxWebProperties {
        @NestedConfigurationProperty
        protected DeploymentOptions routing = new DeploymentOptions();

        @NestedConfigurationProperty
        protected HttpServerOptions http = new HttpServerOptions().setPort(DEFAULT_PORT);

        public DeploymentOptions getRouting() {
            return routing;
        }

        public void setRouting(DeploymentOptions routing) {
            this.routing = routing;
        }

        public HttpServerOptions getHttp() {
            return http;
        }

        public void setHttp(HttpServerOptions http) {
            this.http = http;
        }
    }

}
