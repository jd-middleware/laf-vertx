package com.jd.laf.web.vertx.spring.boot.cluster;

import com.jd.laf.web.vertx.spring.boot.VertxAutoConfiguration;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureBefore(VertxAutoConfiguration.class)
@AutoConfigureAfter(HazelcastAutoConfiguration.class)
@ConditionalOnMissingBean(ClusterManager.class)
@Import({
        HazelcastAutoConfiguration.class,
        IgniteClusterConfiguration.class,
        ZookeeperClusterConfiguration.class
})
public class ClusterAutoConfiguration {

    static final String HAZELCAST = "io.vertx.spi.cluster.hazelcast.HazelcastClusterManager";
    static final String IGNITE = "io.vertx.spi.cluster.ignite.IgniteClusterManager";
    static final String ZOOKEEPER = "io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager";
}
