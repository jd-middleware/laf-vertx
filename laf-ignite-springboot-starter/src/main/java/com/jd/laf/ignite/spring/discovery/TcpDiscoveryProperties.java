package com.jd.laf.ignite.spring.discovery;

import com.jd.laf.ignite.spring.SpiAdapterProperties;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

import static org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi.*;

public abstract class TcpDiscoveryProperties extends SpiAdapterProperties<TcpDiscoverySpi> {
    protected boolean forceServerMode;
    protected boolean clientReconnectDisabled;
    protected long ipFinderCleanFrequency = DFLT_IP_FINDER_CLEAN_FREQ;
    protected long sockTimeout = DFLT_SOCK_TIMEOUT;
    protected long networkTimeout = DFLT_NETWORK_TIMEOUT;
    protected long joinTimeout = DFLT_JOIN_TIMEOUT;
    protected long ackTimeout = DFLT_ACK_TIMEOUT;
    protected long maxAckTimeout = DFLT_MAX_ACK_TIMEOUT;
    protected String localAddress;
    protected int localPort = DFLT_PORT;
    protected int localPortRange = DFLT_PORT_RANGE;
    protected int reconnectCount = DFLT_RECONNECT_CNT;
    protected long reconnectDelay = DFLT_RECONNECT_DELAY;
    protected long statisticsPrintFrequency = DFLT_STATS_PRINT_FREQ;
    protected int threadPriority = DFLT_THREAD_PRI;
    protected int topHistorySize = DFLT_TOP_HISTORY_SIZE;
    protected boolean shared;

    public boolean isForceServerMode() {
        return forceServerMode;
    }

    public void setForceServerMode(boolean forceServerMode) {
        this.forceServerMode = forceServerMode;
    }

    public boolean isClientReconnectDisabled() {
        return clientReconnectDisabled;
    }

    public void setClientReconnectDisabled(boolean clientReconnectDisabled) {
        this.clientReconnectDisabled = clientReconnectDisabled;
    }

    public long getIpFinderCleanFrequency() {
        return ipFinderCleanFrequency;
    }

    public void setIpFinderCleanFrequency(long ipFinderCleanFrequency) {
        this.ipFinderCleanFrequency = ipFinderCleanFrequency;
    }

    public long getSockTimeout() {
        return sockTimeout;
    }

    public void setSockTimeout(long sockTimeout) {
        this.sockTimeout = sockTimeout;
    }

    public long getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(long networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public long getJoinTimeout() {
        return joinTimeout;
    }

    public void setJoinTimeout(long joinTimeout) {
        this.joinTimeout = joinTimeout;
    }

    public long getAckTimeout() {
        return ackTimeout;
    }

    public void setAckTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
    }

    public long getMaxAckTimeout() {
        return maxAckTimeout;
    }

    public void setMaxAckTimeout(long maxAckTimeout) {
        this.maxAckTimeout = maxAckTimeout;
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

    public int getReconnectCount() {
        return reconnectCount;
    }

    public void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    public long getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public long getStatisticsPrintFrequency() {
        return statisticsPrintFrequency;
    }

    public void setStatisticsPrintFrequency(long statisticsPrintFrequency) {
        this.statisticsPrintFrequency = statisticsPrintFrequency;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public int getTopHistorySize() {
        return topHistorySize;
    }

    public void setTopHistorySize(int topHistorySize) {
        this.topHistorySize = topHistorySize;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isFailureDetectionTimeoutEnabled() {
        return failureDetectionTimeoutEnabled;
    }

    public void setFailureDetectionTimeoutEnabled(boolean failureDetectionTimeoutEnabled) {
        this.failureDetectionTimeoutEnabled = failureDetectionTimeoutEnabled;
    }

    @Override
    public void configure(final TcpDiscoverySpi spi) {
        super.configure(spi);
        spi.setForceServerMode(forceServerMode);
        spi.setClientReconnectDisabled(clientReconnectDisabled);
        spi.setIpFinderCleanFrequency(ipFinderCleanFrequency);
        spi.setSocketTimeout(sockTimeout);
        spi.setNetworkTimeout(networkTimeout);
        spi.setJoinTimeout(joinTimeout);
        spi.setAckTimeout(ackTimeout);
        spi.setMaxAckTimeout(maxAckTimeout);
        spi.setLocalAddress(localAddress);
        spi.setLocalPort(localPort);
        spi.setLocalPortRange(localPortRange);
        spi.setReconnectCount(reconnectCount);
        spi.setReconnectDelay((int) reconnectDelay);
        spi.setStatisticsPrintFrequency(statisticsPrintFrequency);
        spi.setThreadPriority(threadPriority);
        spi.setTopHistorySize(topHistorySize);
    }
}
