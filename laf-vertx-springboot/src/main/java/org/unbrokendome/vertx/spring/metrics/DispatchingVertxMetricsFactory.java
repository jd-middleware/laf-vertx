package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.VertxMetrics;

import java.util.ArrayList;
import java.util.List;

public class DispatchingVertxMetricsFactory implements VertxMetricsFactory {

    private final List<? extends VertxMetricsFactory> delegates;

    public DispatchingVertxMetricsFactory(List<? extends VertxMetricsFactory> delegates) {
        this.delegates = delegates;
    }

    @Override
    public VertxMetrics metrics(final Vertx vertx, final VertxOptions options) {
        List<VertxMetrics> allMetrics = new ArrayList<>(delegates.size());
        for (VertxMetricsFactory delegate : delegates) {
            VertxMetrics metrics = delegate.metrics(vertx, options);
            if (metrics != null) {
                allMetrics.add(metrics);
            }
        }
        int size = allMetrics.size();
        switch (size) {
            case 0:
                return new DummyVertxMetrics();
            case 1:
                return allMetrics.get(0);
            default:
                return new DispatchingVertxMetrics(allMetrics);
        }
    }
}
