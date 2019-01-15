package com.jd.laf.web.vertx.spring.boot.cluster;

import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_REMOVED;


@Configuration
@ConditionalOnClass(IgniteClusterManager.class)
@Conditional(IgniteClusterConfiguration.IgniteCondition.class)
public class IgniteClusterConfiguration {

    @Bean
    public IgniteClusterManager igniteClusterManager(
            ObjectProvider<IgniteConfiguration> igniteProvider) {
        System.setProperty("IGNITE_NO_SHUTDOWN_HOOK", "true");
        IgniteConfiguration ignite = igniteProvider.getIfAvailable();
        if (ignite.getIncludeEventTypes() == null) {
            //添加Vertx默认需要的监听事件
            ignite.setIncludeEventTypes(new int[]{EVT_CACHE_OBJECT_REMOVED});
        }
        return ignite != null ? new IgniteClusterManager(ignite) : new IgniteClusterManager();
    }

    static class IgniteCondition extends AnyNestedCondition {

        public IgniteCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "ignite")
        static class IgnitePropertyCondition {
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterAutoConfiguration.HAZELCAST,
                ClusterAutoConfiguration.ZOOKEEPER
        })
        static class OnlyIgniteCondition {
        }
    }
}
