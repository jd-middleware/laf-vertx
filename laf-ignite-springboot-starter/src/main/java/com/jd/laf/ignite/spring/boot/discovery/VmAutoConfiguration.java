package com.jd.laf.ignite.spring.boot.discovery;

import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "ignite.discovery", name = "type", havingValue = "ip")
@EnableConfigurationProperties(VmAutoConfiguration.VmDiscoveryProperties.class)
public class VmAutoConfiguration {

    @Bean
    public IgniteDiscoverySpi multicastDiscoverySpi(VmDiscoveryProperties properties) {
        TcpDiscoverySpi result = new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryVmIpFinder());
        properties.configure(result);
        return result;
    }

    @ConfigurationProperties(prefix = "ignite.discovery.ip")
    public static class VmDiscoveryProperties extends TcpDiscoveryProperties {

        protected List<String> addresses;

        public List<String> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<String> addresses) {
            this.addresses = addresses;
        }

        @Override
        public void configure(TcpDiscoverySpi spi) {
            super.configure(spi);

            TcpDiscoveryVmIpFinder ipFinder = (TcpDiscoveryVmIpFinder) spi.getIpFinder();
            ipFinder.setAddresses(addresses);
            ipFinder.setShared(shared);
        }
    }
}
