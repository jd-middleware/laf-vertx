package com.jd.laf.ignite.spring.communication;

import com.jd.laf.ignite.spring.SpiAdapterProperties;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi.*;

@Configuration
@ConditionalOnProperty(prefix = "ignite.communication", name = "type", havingValue = "tcp", matchIfMissing = true)
@EnableConfigurationProperties(TcpCommunicationAutoConfiguration.TcpCommunicationProperties.class)
public class TcpCommunicationAutoConfiguration {

    @Bean
    public CommunicationSpi tcpCommunication(TcpCommunicationProperties properties) {
        TcpCommunicationSpi result = new TcpCommunicationSpi();
        properties.configure(result);
        return result;
    }

    @ConfigurationProperties(prefix = "ignite.communication.tcp")
    public static class TcpCommunicationProperties extends SpiAdapterProperties<TcpCommunicationSpi> {

        protected int ackSendThreshold = DFLT_ACK_SND_THRESHOLD;
        protected int connectionsPerNode = DFLT_CONN_PER_NODE;
        protected long connectTimeout = DFLT_CONN_TIMEOUT;
        protected long maxConnectTimeout = DFLT_MAX_CONN_TIMEOUT;
        protected boolean directBuffer = true;
        protected boolean directSendBuffer;
        protected boolean filterReachableAddresses = DFLT_FILTER_REACHABLE_ADDRESSES;
        protected long idleConnectionTimeout = DFLT_IDLE_CONN_TIMEOUT;
        protected String localAddress;
        protected int localPort = DFLT_PORT;
        protected int localPortRange = DFLT_PORT_RANGE;
        protected int messageQueueLimit = DFLT_MSG_QUEUE_LIMIT;
        protected int reconnectCount = DFLT_RECONNECT_CNT;
        protected int sockSendBuffer = DFLT_SOCK_BUF_SIZE;
        protected int sockReceiveBuffer = DFLT_SOCK_BUF_SIZE;
        protected long sockWriteTimeout = DFLT_SOCK_WRITE_TIMEOUT;
        protected int selectorsCount = DFLT_SELECTORS_CNT;
        protected long selectorSpins = 0L;
        protected int sharedMemoryPort = DFLT_SHMEM_PORT;
        protected int slowClientQueueLimit;
        protected boolean tcpNoDelay = DFLT_TCP_NODELAY;
        protected int unacknowledgedMessagesBufferSize;
        protected boolean usePairedConnections;

        public int getAckSendThreshold() {
            return ackSendThreshold;
        }

        public void setAckSendThreshold(int ackSendThreshold) {
            this.ackSendThreshold = ackSendThreshold;
        }

        public int getConnectionsPerNode() {
            return connectionsPerNode;
        }

        public void setConnectionsPerNode(int connectionsPerNode) {
            this.connectionsPerNode = connectionsPerNode;
        }

        public boolean isDirectBuffer() {
            return directBuffer;
        }

        public void setDirectBuffer(boolean directBuffer) {
            this.directBuffer = directBuffer;
        }

        public boolean isDirectSendBuffer() {
            return directSendBuffer;
        }

        public void setDirectSendBuffer(boolean directSendBuffer) {
            this.directSendBuffer = directSendBuffer;
        }

        public boolean isFilterReachableAddresses() {
            return filterReachableAddresses;
        }

        public void setFilterReachableAddresses(boolean filterReachableAddresses) {
            this.filterReachableAddresses = filterReachableAddresses;
        }

        public long getIdleConnectionTimeout() {
            return idleConnectionTimeout;
        }

        public void setIdleConnectionTimeout(long idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public String getLocalAddress() {
            return localAddress;
        }

        public void setLocalAddress(String localAddress) {
            this.localAddress = localAddress;
        }

        public int getLocalPort() {
            return localPort;
        }

        public void setLocalPort(int localPort) {
            this.localPort = localPort;
        }

        public int getLocalPortRange() {
            return localPortRange;
        }

        public void setLocalPortRange(int localPortRange) {
            this.localPortRange = localPortRange;
        }

        public long getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public long getMaxConnectTimeout() {
            return maxConnectTimeout;
        }

        public void setMaxConnectTimeout(long maxConnectTimeout) {
            this.maxConnectTimeout = maxConnectTimeout;
        }

        public int getMessageQueueLimit() {
            return messageQueueLimit;
        }

        public void setMessageQueueLimit(int messageQueueLimit) {
            this.messageQueueLimit = messageQueueLimit;
        }

        public int getReconnectCount() {
            return reconnectCount;
        }

        public void setReconnectCount(int reconnectCount) {
            this.reconnectCount = reconnectCount;
        }

        public int getSockSendBuffer() {
            return sockSendBuffer;
        }

        public void setSockSendBuffer(int sockSendBuffer) {
            this.sockSendBuffer = sockSendBuffer;
        }

        public int getSockReceiveBuffer() {
            return sockReceiveBuffer;
        }

        public void setSockReceiveBuffer(int sockReceiveBuffer) {
            this.sockReceiveBuffer = sockReceiveBuffer;
        }

        public long getSockWriteTimeout() {
            return sockWriteTimeout;
        }

        public void setSockWriteTimeout(long sockWriteTimeout) {
            this.sockWriteTimeout = sockWriteTimeout;
        }

        public int getSelectorsCount() {
            return selectorsCount;
        }

        public void setSelectorsCount(int selectorsCount) {
            this.selectorsCount = selectorsCount;
        }

        public long getSelectorSpins() {
            return selectorSpins;
        }

        public void setSelectorSpins(long selectorSpins) {
            this.selectorSpins = selectorSpins;
        }

        public int getSharedMemoryPort() {
            return sharedMemoryPort;
        }

        public void setSharedMemoryPort(int sharedMemoryPort) {
            this.sharedMemoryPort = sharedMemoryPort;
        }

        public int getSlowClientQueueLimit() {
            return slowClientQueueLimit;
        }

        public void setSlowClientQueueLimit(int slowClientQueueLimit) {
            this.slowClientQueueLimit = slowClientQueueLimit;
        }

        public boolean isTcpNoDelay() {
            return tcpNoDelay;
        }

        public void setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
        }

        public int getUnacknowledgedMessagesBufferSize() {
            return unacknowledgedMessagesBufferSize;
        }

        public void setUnacknowledgedMessagesBufferSize(int unacknowledgedMessagesBufferSize) {
            this.unacknowledgedMessagesBufferSize = unacknowledgedMessagesBufferSize;
        }

        public boolean isUsePairedConnections() {
            return usePairedConnections;
        }

        public void setUsePairedConnections(boolean usePairedConnections) {
            this.usePairedConnections = usePairedConnections;
        }

        @Override
        public void configure(final TcpCommunicationSpi communicationSpi) {
            super.configure(communicationSpi);
            communicationSpi.setAckSendThreshold(ackSendThreshold);
            communicationSpi.setConnectionsPerNode(connectionsPerNode);
            communicationSpi.setDirectBuffer(directBuffer);
            communicationSpi.setDirectSendBuffer(directSendBuffer);
            communicationSpi.setConnectTimeout(connectTimeout);
            communicationSpi.setMaxConnectTimeout(maxConnectTimeout);
            communicationSpi.setFilterReachableAddresses(filterReachableAddresses);
            communicationSpi.setIdleConnectionTimeout(idleConnectionTimeout);
            communicationSpi.setLocalAddress(localAddress);
            communicationSpi.setLocalPort(localPort);
            communicationSpi.setLocalPortRange(localPortRange);
            communicationSpi.setMessageQueueLimit(messageQueueLimit);
            communicationSpi.setSlowClientQueueLimit(slowClientQueueLimit);
            communicationSpi.setReconnectCount(reconnectCount);
            communicationSpi.setSelectorsCount(selectorsCount);
            communicationSpi.setSelectorSpins(selectorSpins);
            communicationSpi.setSharedMemoryPort(sharedMemoryPort);
            communicationSpi.setSocketReceiveBuffer(sockReceiveBuffer);
            communicationSpi.setSocketSendBuffer(sockSendBuffer);
            communicationSpi.setSocketWriteTimeout(sockWriteTimeout);
            communicationSpi.setTcpNoDelay(tcpNoDelay);
            communicationSpi.setUnacknowledgedMessagesBufferSize(unacknowledgedMessagesBufferSize);
            communicationSpi.setUsePairedConnections(usePairedConnections);
        }
    }

}
