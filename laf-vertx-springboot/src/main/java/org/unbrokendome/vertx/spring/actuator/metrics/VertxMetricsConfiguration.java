package org.unbrokendome.vertx.spring.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引入micrometer-registry-prometheus依赖后，会自动注册PrometheusMeterRegistry
 */
@Configuration
@EnableConfigurationProperties(VertxMetricsProperties.class)
public class VertxMetricsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnBean(MeterRegistry.class)
    public VertxActuatorMetrics vertxActuatorMetrics(MeterRegistry meterRegistry, VertxMetricsProperties metricsProperties) {
        return new VertxActuatorMetrics(meterRegistry, metricsProperties);
    }
}
