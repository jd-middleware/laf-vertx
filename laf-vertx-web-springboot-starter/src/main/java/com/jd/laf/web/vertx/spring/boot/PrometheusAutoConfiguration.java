package com.jd.laf.web.vertx.spring.boot;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RouteProvider;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.RouteType;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.micrometer.VertxPrometheusOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unbrokendome.vertx.spring.boot.metric.MetricAutoConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@AutoConfigureAfter(MetricAutoConfiguration.class)
@AutoConfigureBefore(VertxWebAutoConfiguration.class)
@ConditionalOnClass({PrometheusMeterRegistry.class, VertxPrometheusOptions.class})
@ConditionalOnProperty(prefix = "vertx.metrics.prometheus", name = "route", havingValue = "true", matchIfMissing = true)
public class PrometheusAutoConfiguration {

    @Value("${vertx.metrics.prometheus.path:/metrics}")
    private String path = "/metrics";
    @Value("${vertx.metrics.registryName:default}")
    private String registryName = "default";

    @Bean
    public PrometheusHandler prometheusHandler() {
        return new PrometheusHandler(registryName);
    }

    @Bean
    public RouteProvider prometheusProvider() {

        //添加插件
        return () -> {
            List<RouteConfig> result = new ArrayList<>();
            RouteConfig config = new RouteConfig();
            config.setPath(path);
            config.setType(RouteType.GET);
            config.setHandlers(Arrays.asList(PrometheusHandler.PROMETHEUS));
            result.add(config);
            return result;
        };
    }

    public static class PrometheusHandler implements RoutingHandler, EnvironmentAware {
        public static final String PROMETHEUS = "prometheus";
        private Handler handler;
        private String registryName;

        public PrometheusHandler(String registryName) {
            this.registryName = registryName;
        }

        @Override
        public void setup(Vertx vertx, Environment environment) throws Exception {
            handler = PrometheusScrapingHandler.create(registryName);
        }

        @Override
        public String type() {
            return PROMETHEUS;
        }

        @Override
        public void handle(final RoutingContext event) {
            handler.handle(event);
        }
    }

}
