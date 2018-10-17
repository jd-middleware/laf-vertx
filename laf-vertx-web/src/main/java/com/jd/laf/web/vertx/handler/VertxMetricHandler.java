package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 * 系统指标
 */
public class VertxMetricHandler implements RoutingHandler, EnvironmentAware {

    protected PrometheusMeterRegistry registry;

    @Override
    public void setup(final Vertx vertx, final Environment environment) throws Exception {
        registry = environment.getObject("prometheusMeterRegistry", PrometheusMeterRegistry.class);
    }

    @Override
    public String type() {
        return "vertxMetric";
    }

    @Override
    public void handle(final RoutingContext context) {
        if (registry != null) {
            String response = registry.scrape();
            context.response().end(response);
        }
    }
}
