package org.unbrokendome.vertx.spring.boot.metric;

import io.vertx.core.metrics.MetricsOptions;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.unbrokendome.vertx.spring.boot.VertxAutoConfiguration;


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
