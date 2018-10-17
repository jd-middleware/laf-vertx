package org.unbrokendome.vertx.spring.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(VertxMetricsProperties.class)
public class VertxMetricsConfiguration {

    protected final MeterRegistry meterRegistry;
    protected final VertxMetricsProperties metricsProperties;

    public VertxMetricsConfiguration(ObjectProvider<MeterRegistry> meterRegistryProvider,
                                     VertxMetricsProperties metricsProperties) {
        MeterRegistry registry = meterRegistryProvider.getIfAvailable();
        this.meterRegistry = registry == null ? Metrics.globalRegistry : registry;
        this.metricsProperties = metricsProperties;
    }

    @Bean
    public VertxActuatorMetrics vertxActuatorMetrics() {
        return new VertxActuatorMetrics(meterRegistry, metricsProperties);
    }
}
