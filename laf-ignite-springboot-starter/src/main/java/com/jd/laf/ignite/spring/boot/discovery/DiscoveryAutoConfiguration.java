package com.jd.laf.ignite.spring.boot.discovery;

import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnMissingBean(IgniteDiscoverySpi.class)
@Import({
        MulticastAutoConfiguration.class,
        VmAutoConfiguration.class,
        ZookeeperAutoConfiguration.class
})
public class DiscoveryAutoConfiguration {

}
