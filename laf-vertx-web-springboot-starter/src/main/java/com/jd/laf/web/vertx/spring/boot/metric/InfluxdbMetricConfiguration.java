package com.jd.laf.web.vertx.spring.boot.metric;

import io.micrometer.influx.InfluxMeterRegistry;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxInfluxDbOptions;
import io.vertx.micrometer.impl.VertxMetricsFactoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass({InfluxMeterRegistry.class, VertxInfluxDbOptions.class})
@Conditional(InfluxdbMetricConfiguration.InfluxdbMetricCondition.class)
@ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(MicrometerMetricsOptions.class)
public class InfluxdbMetricConfiguration {

    @Value("${vertx.metrics.influxdb.url}")
    private String url = "http://localhost:8086";
    @Value("${vertx.metrics.influxdb.db:default}")
    private String db = "default";
    @Value("${vertx.metrics.influxdb.user}")
    private String user;
    @Value("${vertx.metrics.influxdb.password}")
    private String password;
    @Value("${vertx.metrics.influxdb.retentionPolicy}")
    private String retentionPolicy;
    @Value("${vertx.metrics.influxdb.compressed:true}")
    private boolean compressed = true;
    @Value("${vertx.metrics.influxdb.step:10}")
    private int step = 10;
    @Value("${vertx.metrics.influxdb.numThreads:2}")
    private int numThreads = 2;
    @Value("${vertx.metrics.influxdb.connectTimeout:1}")
    private int connectTimeout = 1;
    @Value("${vertx.metrics.influxdb.readTimeout:10}")
    private int readTimeout = 10;
    @Value("${vertx.metrics.influxdb.batchSize:10000}")
    private int batchSize = 10000;
    @Value("${vertx.metrics.registryName:default}")
    private String registryName = "default";
    @Value("${vertx.metrics.jvm.enabled:false}")
    private boolean jvmMetricsEnabled;

    @Bean
    public MicrometerMetricsOptions micrometerMetricsOptions() {
        MicrometerMetricsOptions result = new MicrometerMetricsOptions().setRegistryName(registryName)
                .setJvmMetricsEnabled(true).setEnabled(true)
                .setInfluxDbOptions(new VertxInfluxDbOptions().setEnabled(true)
                        .setUri(url).setDb(db).setUserName(user).setPassword(password)
                        .setRetentionPolicy(retentionPolicy).setCompressed(compressed)
                        .setStep(step).setNumThreads(numThreads).setConnectTimeout(connectTimeout)
                        .setReadTimeout(readTimeout).setBatchSize(batchSize));
        result.setFactory(new VertxMetricsFactoryImpl());
        return result;
    }

    static class InfluxdbMetricCondition extends AnyNestedCondition {

        public InfluxdbMetricCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", havingValue = "influxdb")
        static class InfluxdbPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                MetricAutoConfiguration.DROPWIZARD_METRIC,
                MetricAutoConfiguration.PROMETHEUS_METRIC,
        })
        static class OnlyInfluxdbOnClasspathCondition {
        }
    }
}
