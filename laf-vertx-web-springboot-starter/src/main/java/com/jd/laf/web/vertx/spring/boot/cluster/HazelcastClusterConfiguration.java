package com.jd.laf.web.vertx.spring.boot.cluster;

import com.hazelcast.core.HazelcastInstance;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(HazelcastClusterManager.class)
@ConditionalOnBean(HazelcastInstance.class)
@Conditional(HazelcastClusterConfiguration.HazelcastCondition.class)
public class HazelcastClusterConfiguration {

    @Bean
    public HazelcastClusterManager hazelcastClusterManager(HazelcastInstance instance) {
        return new HazelcastClusterManager(instance);
    }

    /**
     * 条件
     */
    static class HazelcastCondition extends AnyNestedCondition {

        public HazelcastCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "hazelcast")
        static class HazelcastPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterAutoConfiguration.IGNITE,
                ClusterAutoConfiguration.ZOOKEEPER
        })
        static class OnlyHazelcastCondition {
        }
    }
}
