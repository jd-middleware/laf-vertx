package com.jd.laf.web.vertx.spring.boot.metric;

import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.impl.VertxMetricsFactoryImpl;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(DropwizardMetricsOptions.class)
@Conditional(DropwizardMetricConfiguration.DropwizardMetricCondition.class)
@ConditionalOnProperty(prefix = "vertx.metrics", name = "enabled", havingValue = "true")
public class DropwizardMetricConfiguration {

    @Bean
    public DropwizardMetricsOptions dropwizardMetricsOptions() {
        DropwizardMetricsOptions result = new DropwizardMetricsOptions().setEnabled(true);
        result.setFactory(new VertxMetricsFactoryImpl());
        return result;
    }

    static class DropwizardMetricCondition extends AnyNestedCondition {

        public DropwizardMetricCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", havingValue = "dropwizard")
        static class DropwizardPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.metrics", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                MetricAutoConfiguration.PROMETHEUS_METRIC,
                MetricAutoConfiguration.INFLUXDB_METRIC,
        })
        static class OnlyDropwizardOnClasspathCondition {
        }
    }
}
