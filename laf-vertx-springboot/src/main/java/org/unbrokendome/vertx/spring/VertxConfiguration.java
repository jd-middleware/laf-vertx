package org.unbrokendome.vertx.spring;


import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.unbrokendome.vertx.spring.events.EventPublishingVertxListener;

import java.util.List;

/**
 * Vertx配置
 */
@Configuration
public class VertxConfiguration {

    /**
     * 注册Vertx事件监听器
     *
     * @return
     */
    @Bean
    public EventPublishingVertxListener vertxListener() {
        return new EventPublishingVertxListener();
    }

    /**
     * 注册SpringVertx
     *
     * @param vertxFactoryProvider
     * @param optionsProvider
     * @param clusterManagerProvider
     * @param listenersProvider
     * @param configurersProvider
     * @param metricsFactoryProvider
     * @return
     */
    @Bean
    public SpringVertx vertx(
            ObjectProvider<VertxFactory> vertxFactoryProvider,
            ObjectProvider<VertxOptions> optionsProvider,
            ObjectProvider<ClusterManager> clusterManagerProvider,
            ObjectProvider<List<VertxListener>> listenersProvider,
            ObjectProvider<List<VertxConfigurer>> configurersProvider,
            ObjectProvider<List<VertxMetricsFactory>> metricsFactoryProvider) {

        ClusterManager clusterManager = clusterManagerProvider.getIfAvailable();

        //如果配置了VertxOptions bean，放在最后替换所有可能的配置
        SpringVertx.Builder builder = SpringVertx.builder()
                .factory(vertxFactoryProvider.getIfAvailable())
                .listener(listenersProvider.getIfAvailable())
                .metricsFactory(metricsFactoryProvider.getIfAvailable())
                .configurer(clusterManager == null ? null : new ClusterManagerConfigurer(clusterManager))
                .configurer(configurersProvider.getIfAvailable())
                .configure()
                .options(optionsProvider.getIfAvailable());

        return builder.build();
    }


    protected static class ClusterManagerConfigurer implements VertxConfigurer, Ordered {

        protected final ClusterManager clusterManager;

        public ClusterManagerConfigurer(ClusterManager clusterManager) {
            this.clusterManager = clusterManager;
        }

        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE + 1;
        }

        @Override
        public void configure(SpringVertx.Builder builder) {
            builder.clusterManager(clusterManager);
        }
    }
}
