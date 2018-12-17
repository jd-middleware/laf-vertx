package com.jd.laf.ignite.spring;

import com.jd.laf.ignite.spring.communication.CommunicationAutoConfiguration;
import com.jd.laf.ignite.spring.discovery.DiscoveryAutoConfiguration;
import com.jd.laf.ignite.spring.logger.LogAutoConfiguration;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(IgniteProperties.class)
@ConditionalOnProperty(prefix = "ignite", name = "enable", havingValue = "true", matchIfMissing = true)
@Import({
        DiscoveryAutoConfiguration.class,
        CommunicationAutoConfiguration.class,
        LogAutoConfiguration.class
})
public class IgniteAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IgniteConfiguration ignite(IgniteProperties igniteProperties,
                                      IgniteDiscoverySpi discoverySpi,
                                      CommunicationSpi communicationSpi,
                                      IgniteLogger igniteLogger
    ) throws Exception {
        IgniteConfiguration result = igniteProperties.build();
        result.setDiscoverySpi(discoverySpi);
        result.setCommunicationSpi(communicationSpi);
        result.setGridLogger(igniteLogger);
        return result;
    }

}
