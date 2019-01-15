package com.jd.laf.web.vertx.spring.boot.cluster;

import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
@ConditionalOnClass(ZookeeperClusterManager.class)
@Conditional(ZookeeperClusterConfiguration.ZookeeperClusterManagerCondition.class)
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperClusterConfiguration {

    @Bean
    public ZookeeperClusterManager zookeeperClusterManager(ZookeeperProperties properties) throws IOException {
        return new ZookeeperClusterManager(properties.toJson());
    }


    static class ZookeeperClusterManagerCondition extends AnyNestedCondition {

        public ZookeeperClusterManagerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "zookeeper")
        static class ZookeeperPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterAutoConfiguration.HAZELCAST,
                ClusterAutoConfiguration.IGNITE
        })
        static class OnlyZookeeperCondition {
        }
    }
}
