package org.unbrokendome.vertx.spring.boot.clustermanager;

import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(IgniteClusterManager.class)
@Conditional(IgniteClusterManagerConfiguration.IgniteClusterManagerCondition.class)
public class IgniteClusterManagerConfiguration {

    @Bean
    public IgniteClusterManager igniteClusterManager(
            ObjectProvider<IgniteConfiguration> configurationProvider,
            ObjectProvider<Ignite> igniteProvider) {
        Ignite ignite = igniteProvider.getIfAvailable();
        IgniteConfiguration configuration = configurationProvider.getIfAvailable();
        return ignite != null ? new IgniteClusterManager(ignite) :
                (configuration != null ? new IgniteClusterManager(configuration) : new IgniteClusterManager());
    }

    static class IgniteClusterManagerCondition extends AnyNestedCondition {

        public IgniteClusterManagerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "ignite")
        static class IgnitePropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterManagerAutoConfiguration.HAZELCAST_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.INFINISPAN_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.ZOOKEEPER_CLUSTER_MANAGER
        })
        static class OnlyIgniteOnClasspathCondition {
        }
    }
}
