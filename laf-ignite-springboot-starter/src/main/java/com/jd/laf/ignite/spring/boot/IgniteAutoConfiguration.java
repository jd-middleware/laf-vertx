package com.jd.laf.ignite.spring.boot;

import com.jd.laf.ignite.spring.IgniteConfigurer;
import com.jd.laf.ignite.spring.boot.communication.CommunicationAutoConfiguration;
import com.jd.laf.ignite.spring.boot.discovery.DiscoveryAutoConfiguration;
import com.jd.laf.ignite.spring.boot.logger.LogAutoConfiguration;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

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
                                      IgniteLogger igniteLogger,
                                      ObjectProvider<List<IgniteConfigurer>> configurersProvider
    ) throws Exception {
        IgniteConfiguration result = igniteProperties.build();
        result.setDiscoverySpi(discoverySpi);
        result.setCommunicationSpi(communicationSpi);
        result.setGridLogger(igniteLogger);
        List<IgniteConfigurer> configurers = configurersProvider.getIfAvailable();
        if (configurers != null) {
            for (IgniteConfigurer configurer : configurers) {
                configurer.configure(result);
            }
        }
        return result;
    }

}
