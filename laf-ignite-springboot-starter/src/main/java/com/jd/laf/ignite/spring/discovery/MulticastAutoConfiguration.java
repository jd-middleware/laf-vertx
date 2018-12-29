package com.jd.laf.ignite.spring.discovery;

import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder.*;

@Configuration
@ConditionalOnProperty(prefix = "ignite.discovery", name = "type", havingValue = "multicast", matchIfMissing = true)
@EnableConfigurationProperties(MulticastAutoConfiguration.MulticastProperties.class)
public class MulticastAutoConfiguration {

    @Bean
    public IgniteDiscoverySpi multicastDiscoverySpi(MulticastProperties properties) {
        TcpDiscoverySpi result = new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryMulticastIpFinder());
        properties.configure(result);
        return result;
    }

    @ConfigurationProperties(prefix = "ignite.discovery.multicast")
    public static class MulticastProperties extends TcpDiscoveryProperties {

        protected String multicastGroup = DFLT_MCAST_GROUP;
        protected int multicastPort = DFLT_MCAST_PORT;
        protected int responseWaitTime = DFLT_RES_WAIT_TIME;
        protected int addressRequestAttempts = DFLT_ADDR_REQ_ATTEMPTS;
        protected String localAddress;
        protected int timeToLive = -1;

        public String getMulticastGroup() {
            return multicastGroup;
        }

        public void setMulticastGroup(String multicastGroup) {
            this.multicastGroup = multicastGroup;
        }

        public int getMulticastPort() {
            return multicastPort;
        }

        public void setMulticastPort(int multicastPort) {
            this.multicastPort = multicastPort;
        }

        public int getResponseWaitTime() {
            return responseWaitTime;
        }

        public void setResponseWaitTime(int responseWaitTime) {
            this.responseWaitTime = responseWaitTime;
        }

        public int getAddressRequestAttempts() {
            return addressRequestAttempts;
        }

        public void setAddressRequestAttempts(int addressRequestAttempts) {
            this.addressRequestAttempts = addressRequestAttempts;
        }

        @Override
        public String getLocalAddress() {
            return localAddress;
        }

        @Override
        public void setLocalAddress(String localAddress) {
            this.localAddress = localAddress;
        }

        public int getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(int timeToLive) {
            this.timeToLive = timeToLive;
        }

        @Override
        public void configure(TcpDiscoverySpi spi) {
            super.configure(spi);

            TcpDiscoveryMulticastIpFinder ipFinder = (TcpDiscoveryMulticastIpFinder) spi.getIpFinder();
            ipFinder.setMulticastGroup(multicastGroup);
            ipFinder.setMulticastPort(multicastPort);
            ipFinder.setResponseWaitTime(responseWaitTime);
            ipFinder.setAddressRequestAttempts(addressRequestAttempts);
            ipFinder.setLocalAddress(localAddress);
            ipFinder.setTimeToLive(timeToLive);
        }
    }

}
