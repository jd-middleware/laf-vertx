package com.jd.laf.web.springboot.starter;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.RoutingVerticle;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by yangyang115 on 18-9-27.
 */
@Configuration
public class RoutingVerticleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RoutingVerticle routingVerticle(org.springframework.core.env.Environment environment, ApplicationContext context) {
        RoutingVerticle routingVerticle = new RoutingVerticle();
        routingVerticle.setEnv(new SpringEnvironment(environment, context));
        return routingVerticle;
    }

    /**
     * Created by yangyang36 on 2018/9/28.
     */
    public static class SpringEnvironment implements Environment {

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

}
