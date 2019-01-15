package com.jd.laf.web.vertx.spring.boot;

import com.jd.laf.web.vertx.spring.NetUtils;
import com.jd.laf.web.vertx.spring.SpringVertx;
import com.jd.laf.web.vertx.spring.VerticleProvider;
import com.jd.laf.web.vertx.spring.VertxConfigurer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(VertxProperties.class)
public class VertxAutoConfiguration {

    /**
     * 注册SpringVertx
     *
     * @param properties
     * @param vertxFactoryProvider
     * @param clusterManagerProvider
     * @param metricsOptionsProvider
     * @param configurersProvider
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SpringVertx.class)
    public SpringVertx vertx(
            VertxProperties properties,
            ObjectProvider<VertxFactory> vertxFactoryProvider,
            ObjectProvider<ClusterManager> clusterManagerProvider,
            ObjectProvider<MetricsOptions> metricsOptionsProvider,
            ObjectProvider<List<VertxConfigurer>> configurersProvider,
            ObjectProvider<List<VerticleProvider>> verticlesProvider) {

        VertxFactory vertxFactory = vertxFactoryProvider.getIfAvailable();
        vertxFactory = vertxFactory == null ? Vertx.factory : vertxFactory;
        VertxOptions vertxOptions = properties.toVertxOptions();
        if (vertxOptions.getClusterHost() == null || EventBusOptions.DEFAULT_HOST.equals(vertxOptions.getClusterHost())) {
            //localhost
            String localHost = NetUtils.getLocalHost();
            if (localHost != null) {
                vertxOptions.setClusterHost(localHost);
            }
        }
        MetricsOptions metricsOptions = metricsOptionsProvider.getIfAvailable();
        if (metricsOptions != null) {
            vertxOptions.setMetricsOptions(metricsOptions);
        }
        ClusterManager clusterManager = clusterManagerProvider.getIfAvailable();
        if (clusterManager != null) {
            vertxOptions.setClusterManager(clusterManager);
        }
        List<VertxConfigurer> configurers = configurersProvider.getIfAvailable();
        if (configurers != null) {
            for (VertxConfigurer configurer : configurers) {
                configurer.configure(vertxOptions);
            }
        }
        return new SpringVertx(vertxFactory, vertxOptions, verticlesProvider.getIfAvailable(),
                properties.getFactoryPrefix(), properties.getStartupPhase(), properties.isAutoStartup());
    }
}
