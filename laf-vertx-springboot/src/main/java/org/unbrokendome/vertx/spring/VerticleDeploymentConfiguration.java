package org.unbrokendome.vertx.spring;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Verticle部署配置
 */
@Configuration
public class VerticleDeploymentConfiguration implements VertxConfigurer {

    // Spring Framework 4.3 also introduces ObjectProvider, an extension of the existing ObjectFactory interface
    // with handy signatures such as getIfAvailable and getIfUnique to retrieve a bean only if it actually exists (optional support)
    // or if a single candidate can be determined (in particular: a primary candidate in case of multiple matching beans).
    private final ObjectProvider<List<VerticleRegistration>> verticleRegistrationsProvider;

    // You may use such an ObjectProvider handle for custom resolution purposes during initialization as shown above,
    // or store the handle in a field for late on-demand resolution (as you typically do with an ObjectFactory).
    public VerticleDeploymentConfiguration(ObjectProvider<List<VerticleRegistration>> verticleRegistrationsProvider) {
        this.verticleRegistrationsProvider = verticleRegistrationsProvider;
    }

    /**
     * 注册Bean后置处理器，部署Verticle
     *
     * @return
     */
    @Bean
    public static VerticleBeanPostProcessor verticleBeanPostProcessor() {
        return new VerticleBeanPostProcessor();
    }

    @Override
    public void configure(final SpringVertx.Builder builder) {
        builder.verticleRegistrations(verticleRegistrationsProvider.getIfAvailable());
    }
}
