package com.jd.laf.ignite.spring.boot.discovery;

import com.jd.laf.ignite.spring.boot.SpiAdapterProperties;
import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi.DFLT_JOIN_TIMEOUT;
import static org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi.DFLT_ROOT_PATH;

@Configuration
@ConditionalOnProperty(prefix = "ignite.discovery", name = "type", havingValue = "zookeeper")
@ConditionalOnClass(ZookeeperDiscoverySpi.class)
@EnableConfigurationProperties(ZookeeperAutoConfiguration.ZookeeperDiscoveryProperties.class)
public class ZookeeperAutoConfiguration {

    @Bean
    public IgniteDiscoverySpi multicastDiscoverySpi(ZookeeperDiscoveryProperties properties) {
        ZookeeperDiscoverySpi result = new ZookeeperDiscoverySpi();
        properties.configure(result);
        return result;
    }

    @ConfigurationProperties(prefix = "ignite.discovery.zookeeper")
    public static class ZookeeperDiscoveryProperties extends SpiAdapterProperties<ZookeeperDiscoverySpi> {

        protected String rootPath = DFLT_ROOT_PATH;
        protected String connectionString;
        protected long joinTimeout = DFLT_JOIN_TIMEOUT;
        protected long sessionTimeout;
        protected boolean clientReconnectDisabled;

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getConnectionString() {
            return connectionString;
        }

        public void setConnectionString(String connectionString) {
            this.connectionString = connectionString;
        }

        public long getJoinTimeout() {
            return joinTimeout;
        }

        public void setJoinTimeout(long joinTimeout) {
            this.joinTimeout = joinTimeout;
        }

        public long getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(long sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public boolean isClientReconnectDisabled() {
            return clientReconnectDisabled;
        }

        public void setClientReconnectDisabled(boolean clientReconnectDisabled) {
            this.clientReconnectDisabled = clientReconnectDisabled;
        }

        @Override
        public void configure(ZookeeperDiscoverySpi spi) {
            super.configure(spi);
            spi.setJoinTimeout(joinTimeout);
            spi.setSessionTimeout(sessionTimeout);
            spi.setZkConnectionString(connectionString);
            spi.setZkRootPath(rootPath);
            spi.setClientReconnectDisabled(clientReconnectDisabled);
        }
    }
}
