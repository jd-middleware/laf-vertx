package com.jd.laf.web.vertx.spring.boot.metric;

import com.jd.laf.web.vertx.spring.boot.VertxAutoConfiguration;
import io.vertx.core.metrics.MetricsOptions;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureBefore(VertxAutoConfiguration.class)
@ConditionalOnMissingBean(MetricsOptions.class)
@Import({
        PrometheusMetricConfiguration.class,
        InfluxdbMetricConfiguration.class,
        DropwizardMetricConfiguration.class
})
public class MetricAutoConfiguration {

    static final String DROPWIZARD_METRIC = "io.vertx.ext.dropwizard.DropwizardMetricsOptions";
    static final String PROMETHEUS_METRIC = "io.micrometer.prometheus.PrometheusMeterRegistry";
    static final String INFLUXDB_METRIC = "io.micrometer.influx.InfluxMeterRegistry";

}
