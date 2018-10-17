package org.unbrokendome.vertx.spring.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(VertxMetricsProperties.class)
@ConditionalOnClass(MeterRegistry.class)
public class VertxMetricsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(MeterRegistry.class)
    @ConditionalOnClass(PrometheusMeterRegistry.class)
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    @ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true")
    @ConditionalOnBean(MeterRegistry.class)
    public VertxActuatorMetrics vertxActuatorMetrics(MeterRegistry meterRegistry, VertxMetricsProperties metricsProperties) {
        return new VertxActuatorMetrics(meterRegistry, metricsProperties);
    }
}
