package com.jd.laf.web.vertx.spring.boot.metric;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.impl.VertxMetricsFactoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass({PrometheusMeterRegistry.class, VertxPrometheusOptions.class})
@Conditional(PrometheusMetricConfiguration.PrometheusMetricCondition.class)
@ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(MicrometerMetricsOptions.class)
public class PrometheusMetricConfiguration {

    @Value("${vertx.metrics.prometheus.server.enabled:false}")
    private boolean embeddedServer;
    @Value("${vertx.metrics.prometheus.server.port:8080}")
    private int embeddedServerPort = 8080;
    @Value("${vertx.metrics.prometheus.server.endpoint:/metrics}")
    private String embeddedServerEndpoint = "/metrics";
    @Value("${vertx.metrics.prometheus.server.publishQuantiles:false}")
    private boolean publishQuantiles;
    @Value("${vertx.metrics.registryName:default}")
    private String registryName = "default";
    @Value("${vertx.metrics.jvm.enabled:false}")
    private boolean jvmMetricsEnabled;

    @Bean
    public MicrometerMetricsOptions micrometerMetricsOptions() {
        MicrometerMetricsOptions result = new MicrometerMetricsOptions().setRegistryName(registryName)
                .setJvmMetricsEnabled(jvmMetricsEnabled).setEnabled(true)
                .setPrometheusOptions(new VertxPrometheusOptions()
                        .setEnabled(true)
                        .setStartEmbeddedServer(embeddedServer)
                        .setEmbeddedServerEndpoint(embeddedServerEndpoint)
                        .setEmbeddedServerOptions(new HttpServerOptions().setPort(embeddedServerPort)));
        result.setFactory(new VertxMetricsFactoryImpl());
        return result;
    }

    public static class PrometheusMetricCondition extends AnyNestedCondition {

        public PrometheusMetricCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", havingValue = "prometheus")
        static class PrometheusPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                MetricAutoConfiguration.DROPWIZARD_METRIC,
                MetricAutoConfiguration.INFLUXDB_METRIC,
        })
        static class OnlyPrometheusCondition {
        }
    }
}
