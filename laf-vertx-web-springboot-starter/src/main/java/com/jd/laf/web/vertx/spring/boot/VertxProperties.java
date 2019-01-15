package com.jd.laf.web.vertx.spring.boot;

import io.vertx.core.VertxOptions;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.http.ClientAuth;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.vertx.core.VertxOptions.*;
import static io.vertx.core.dns.AddressResolverOptions.*;
import static io.vertx.core.eventbus.EventBusOptions.*;
import static io.vertx.core.file.FileSystemOptions.DEFAULT_CLASS_PATH_RESOLVING_ENABLED;
import static io.vertx.core.file.FileSystemOptions.DEFAULT_FILE_CACHING_ENABLED;


@ConfigurationProperties(prefix = "vertx")
// As this class will *replace* the VertxOptions, we use "highest precedence" so this configurer is called first
// in the chain, giving other configurers a chance to modify the options
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VertxProperties {

    private String factoryPrefix = "spring";
    private boolean autoStartup = true;
    private int startupPhase = 0;
    private boolean clustered = DEFAULT_CLUSTERED;
    private String clusterPublicHost = DEFAULT_CLUSTER_PUBLIC_HOST;
    private int clusterPublicPort = DEFAULT_CLUSTER_PUBLIC_PORT;
    private long clusterPingInterval = DEFAULT_CLUSTER_PING_INTERVAL;
    private long clusterPingReplyInterval = DEFAULT_CLUSTER_PING_REPLY_INTERVAL;
    private int eventLoopPoolSize = DEFAULT_EVENT_LOOP_POOL_SIZE;
    private int workerPoolSize = DEFAULT_WORKER_POOL_SIZE;
    private int internalBlockingPoolSize = DEFAULT_INTERNAL_BLOCKING_POOL_SIZE;
    private long blockedThreadCheckInterval = DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL;
    private long maxEventLoopExecuteTime = DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME;
    private long maxWorkerExecuteTime = DEFAULT_MAX_WORKER_EXECUTE_TIME;
    private long warningExceptionTime = TimeUnit.SECONDS.toNanos(5);
    private boolean preferNativeTransport = DEFAULT_PREFER_NATIVE_TRANSPORT;
    private TimeUnit maxEventLoopExecuteTimeUnit = DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME_UNIT;
    private TimeUnit maxWorkerExecuteTimeUnit = DEFAULT_MAX_WORKER_EXECUTE_TIME_UNIT;
    private TimeUnit warningExceptionTimeUnit = DEFAULT_WARNING_EXCEPTION_TIME_UNIT;
    private TimeUnit blockedThreadCheckIntervalUnit = DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL_UNIT;
    private boolean classPathResolvingEnabled = DEFAULT_CLASS_PATH_RESOLVING_ENABLED;
    private boolean fileCachingEnabled = DEFAULT_FILE_CACHING_ENABLED;
    private HaProperties ha = new HaProperties();
    private EventBusProperties eventBus = new EventBusProperties();
    private AddressResolverProperties addressResolver = new AddressResolverProperties();

    public String getFactoryPrefix() {
        return factoryPrefix;
    }

    public void setFactoryPrefix(String factoryPrefix) {
        this.factoryPrefix = factoryPrefix;
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public int getStartupPhase() {
        return startupPhase;
    }

    public void setStartupPhase(int startupPhase) {
        this.startupPhase = startupPhase;
    }

    public boolean isClustered() {
        return clustered;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public String getClusterPublicHost() {
        return clusterPublicHost;
    }

    public void setClusterPublicHost(String clusterPublicHost) {
        this.clusterPublicHost = clusterPublicHost;
    }

    public int getClusterPublicPort() {
        return clusterPublicPort;
    }

    public void setClusterPublicPort(int clusterPublicPort) {
        this.clusterPublicPort = clusterPublicPort;
    }

    public long getClusterPingInterval() {
        return clusterPingInterval;
    }

    public void setClusterPingInterval(long clusterPingInterval) {
        this.clusterPingInterval = clusterPingInterval;
    }

    public long getClusterPingReplyInterval() {
        return clusterPingReplyInterval;
    }

    public void setClusterPingReplyInterval(long clusterPingReplyInterval) {
        this.clusterPingReplyInterval = clusterPingReplyInterval;
    }

    public int getEventLoopPoolSize() {
        return eventLoopPoolSize;
    }

    public void setEventLoopPoolSize(int eventLoopPoolSize) {
        this.eventLoopPoolSize = eventLoopPoolSize;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }

    public int getInternalBlockingPoolSize() {
        return internalBlockingPoolSize;
    }

    public void setInternalBlockingPoolSize(int internalBlockingPoolSize) {
        this.internalBlockingPoolSize = internalBlockingPoolSize;
    }

    public long getBlockedThreadCheckInterval() {
        return blockedThreadCheckInterval;
    }

    public void setBlockedThreadCheckInterval(long blockedThreadCheckInterval) {
        this.blockedThreadCheckInterval = blockedThreadCheckInterval;
    }

    public long getMaxEventLoopExecuteTime() {
        return maxEventLoopExecuteTime;
    }

    public void setMaxEventLoopExecuteTime(long maxEventLoopExecuteTime) {
        this.maxEventLoopExecuteTime = maxEventLoopExecuteTime;
    }

    public long getMaxWorkerExecuteTime() {
        return maxWorkerExecuteTime;
    }

    public void setMaxWorkerExecuteTime(long maxWorkerExecuteTime) {
        this.maxWorkerExecuteTime = maxWorkerExecuteTime;
    }

    public long getWarningExceptionTime() {
        return warningExceptionTime;
    }

    public void setWarningExceptionTime(long warningExceptionTime) {
        this.warningExceptionTime = warningExceptionTime;
    }

    public boolean isPreferNativeTransport() {
        return preferNativeTransport;
    }

    public void setPreferNativeTransport(boolean preferNativeTransport) {
        this.preferNativeTransport = preferNativeTransport;
    }

    public TimeUnit getMaxEventLoopExecuteTimeUnit() {
        return maxEventLoopExecuteTimeUnit;
    }

    public void setMaxEventLoopExecuteTimeUnit(TimeUnit maxEventLoopExecuteTimeUnit) {
        this.maxEventLoopExecuteTimeUnit = maxEventLoopExecuteTimeUnit;
    }

    public TimeUnit getMaxWorkerExecuteTimeUnit() {
        return maxWorkerExecuteTimeUnit;
    }

    public void setMaxWorkerExecuteTimeUnit(TimeUnit maxWorkerExecuteTimeUnit) {
        this.maxWorkerExecuteTimeUnit = maxWorkerExecuteTimeUnit;
    }

    public TimeUnit getWarningExceptionTimeUnit() {
        return warningExceptionTimeUnit;
    }

    public void setWarningExceptionTimeUnit(TimeUnit warningExceptionTimeUnit) {
        this.warningExceptionTimeUnit = warningExceptionTimeUnit;
    }

    public TimeUnit getBlockedThreadCheckIntervalUnit() {
        return blockedThreadCheckIntervalUnit;
    }

    public void setBlockedThreadCheckIntervalUnit(TimeUnit blockedThreadCheckIntervalUnit) {
        this.blockedThreadCheckIntervalUnit = blockedThreadCheckIntervalUnit;
    }

    public boolean isClassPathResolvingEnabled() {
        return classPathResolvingEnabled;
    }

    public void setClassPathResolvingEnabled(boolean classPathResolvingEnabled) {
        this.classPathResolvingEnabled = classPathResolvingEnabled;
    }

    public boolean isFileCachingEnabled() {
        return fileCachingEnabled;
    }

    public void setFileCachingEnabled(boolean fileCachingEnabled) {
        this.fileCachingEnabled = fileCachingEnabled;
    }

    public HaProperties getHa() {
        return ha;
    }

    public void setHa(HaProperties ha) {
        this.ha = ha;
    }

    public EventBusProperties getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBusProperties eventBus) {
        this.eventBus = eventBus;
    }

    public AddressResolverProperties getAddressResolver() {
        return addressResolver;
    }

    public void setAddressResolver(AddressResolverProperties addressResolver) {
        this.addressResolver = addressResolver;
    }

    public static class HaProperties {

        private boolean enabled = DEFAULT_HA_ENABLED;
        private int quorumSize = DEFAULT_QUORUM_SIZE;
        private String group = DEFAULT_HA_GROUP;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getQuorumSize() {
            return quorumSize;
        }

        public void setQuorumSize(int quorumSize) {
            this.quorumSize = quorumSize;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }

    public static class EventBusProperties {

        private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
        private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
        private int trafficClass = DEFAULT_TRAFFIC_CLASS;
        private boolean reuseAddress = DEFAULT_REUSE_ADDRESS;
        private boolean reusePort = DEFAULT_REUSE_PORT;
        private boolean logActivity = DEFAULT_LOG_ENABLED;
        private boolean tcpNoDelay = DEFAULT_TCP_NO_DELAY;
        private boolean tcpKeepAlive = DEFAULT_TCP_KEEP_ALIVE;
        private int soLinger = DEFAULT_SO_LINGER;
        private boolean usePooledBuffers = DEFAULT_USE_POOLED_BUFFERS;
        private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
        private TimeUnit idleTimeoutUnit = DEFAULT_IDLE_TIMEOUT_TIME_UNIT;
        private boolean useAlpn = DEFAULT_USE_ALPN;
        private boolean tcpFastOpen = DEFAULT_TCP_FAST_OPEN;
        private boolean tcpCork = DEFAULT_TCP_CORK;
        private boolean tcpQuickAck = DEFAULT_TCP_QUICKACK;

        private int port = DEFAULT_PORT;
        private String host = DEFAULT_HOST;
        private int acceptBacklog = DEFAULT_ACCEPT_BACKLOG;
        private ClientAuth clientAuth = DEFAULT_CLIENT_AUTH;
        private int reconnectAttempts = DEFAULT_RECONNECT_ATTEMPTS;
        private long reconnectInterval = DEFAULT_RECONNECT_INTERVAL;
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

        public int getSendBufferSize() {
            return sendBufferSize;
        }

        public void setSendBufferSize(int sendBufferSize) {
            this.sendBufferSize = sendBufferSize;
        }

        public int getReceiveBufferSize() {
            return receiveBufferSize;
        }

        public void setReceiveBufferSize(int receiveBufferSize) {
            this.receiveBufferSize = receiveBufferSize;
        }

        public int getTrafficClass() {
            return trafficClass;
        }

        public void setTrafficClass(int trafficClass) {
            this.trafficClass = trafficClass;
        }

        public boolean isReuseAddress() {
            return reuseAddress;
        }

        public void setReuseAddress(boolean reuseAddress) {
            this.reuseAddress = reuseAddress;
        }

        public boolean isReusePort() {
            return reusePort;
        }

        public void setReusePort(boolean reusePort) {
            this.reusePort = reusePort;
        }

        public boolean isLogActivity() {
            return logActivity;
        }

        public void setLogActivity(boolean logActivity) {
            this.logActivity = logActivity;
        }

        public boolean isTcpNoDelay() {
            return tcpNoDelay;
        }

        public void setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
        }

        public boolean isTcpKeepAlive() {
            return tcpKeepAlive;
        }

        public void setTcpKeepAlive(boolean tcpKeepAlive) {
            this.tcpKeepAlive = tcpKeepAlive;
        }

        public int getSoLinger() {
            return soLinger;
        }

        public void setSoLinger(int soLinger) {
            this.soLinger = soLinger;
        }

        public boolean isUsePooledBuffers() {
            return usePooledBuffers;
        }

        public void setUsePooledBuffers(boolean usePooledBuffers) {
            this.usePooledBuffers = usePooledBuffers;
        }

        public int getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public TimeUnit getIdleTimeoutUnit() {
            return idleTimeoutUnit;
        }

        public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
            this.idleTimeoutUnit = idleTimeoutUnit;
        }

        public boolean isUseAlpn() {
            return useAlpn;
        }

        public void setUseAlpn(boolean useAlpn) {
            this.useAlpn = useAlpn;
        }

        public boolean isTcpFastOpen() {
            return tcpFastOpen;
        }

        public void setTcpFastOpen(boolean tcpFastOpen) {
            this.tcpFastOpen = tcpFastOpen;
        }

        public boolean isTcpCork() {
            return tcpCork;
        }

        public void setTcpCork(boolean tcpCork) {
            this.tcpCork = tcpCork;
        }

        public boolean isTcpQuickAck() {
            return tcpQuickAck;
        }

        public void setTcpQuickAck(boolean tcpQuickAck) {
            this.tcpQuickAck = tcpQuickAck;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getAcceptBacklog() {
            return acceptBacklog;
        }

        public void setAcceptBacklog(int acceptBacklog) {
            this.acceptBacklog = acceptBacklog;
        }

        public ClientAuth getClientAuth() {
            return clientAuth;
        }

        public void setClientAuth(ClientAuth clientAuth) {
            this.clientAuth = clientAuth;
        }

        public int getReconnectAttempts() {
            return reconnectAttempts;
        }

        public void setReconnectAttempts(int reconnectAttempts) {
            this.reconnectAttempts = reconnectAttempts;
        }

        public long getReconnectInterval() {
            return reconnectInterval;
        }

        public void setReconnectInterval(long reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public EventBusOptions toEventBusOptions() {
            EventBusOptions result = new EventBusOptions();
            result.setSendBufferSize(sendBufferSize);
            result.setReceiveBufferSize(receiveBufferSize);
            result.setTrafficClass(trafficClass);
            result.setReuseAddress(reuseAddress);
            result.setReusePort(reusePort);
            result.setLogActivity(logActivity);
            result.setTcpNoDelay(tcpNoDelay);
            result.setTcpKeepAlive(tcpKeepAlive);
            result.setTcpFastOpen(tcpFastOpen);
            result.setTcpCork(tcpCork);
            result.setTcpQuickAck(tcpQuickAck);
            result.setSoLinger(soLinger);
            result.setUsePooledBuffers(usePooledBuffers);
            result.setIdleTimeout(idleTimeout);
            result.setIdleTimeoutUnit(idleTimeoutUnit);
            result.setUseAlpn(useAlpn);
            result.setPort(port);
            result.setHost(host);
            result.setAcceptBacklog(acceptBacklog);
            result.setClientAuth(clientAuth);
            result.setReconnectAttempts(reconnectAttempts);
            result.setReconnectInterval(reconnectInterval);
            result.setConnectTimeout(connectTimeout);

            return result;
        }

    }

    public static class AddressResolverProperties {

        private String hostsPath;
        private List<String> servers;
        private boolean optResourceEnabled = DEFAULT_OPT_RESOURCE_ENABLED;
        private int cacheMinTimeToLive = DEFAULT_CACHE_MIN_TIME_TO_LIVE;
        private int cacheMaxTimeToLive = DEFAULT_CACHE_MAX_TIME_TO_LIVE;
        private int cacheNegativeTimeToLive = DEFAULT_CACHE_NEGATIVE_TIME_TO_LIVE;
        private long queryTimeout = DEFAULT_QUERY_TIMEOUT;
        private int maxQueries = DEFAULT_MAX_QUERIES;
        private boolean rdFlag = DEFAULT_RD_FLAG;
        private List<String> searchDomains;
        private int ndots = DEFAULT_NDOTS;
        private boolean rotateServers = DEFAULT_ROTATE_SERVERS;

        public String getHostsPath() {
            return hostsPath;
        }

        public void setHostsPath(String hostsPath) {
            this.hostsPath = hostsPath;
        }

        public List<String> getServers() {
            return servers;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }

        public boolean isOptResourceEnabled() {
            return optResourceEnabled;
        }

        public void setOptResourceEnabled(boolean optResourceEnabled) {
            this.optResourceEnabled = optResourceEnabled;
        }

        public int getCacheMinTimeToLive() {
            return cacheMinTimeToLive;
        }

        public void setCacheMinTimeToLive(int cacheMinTimeToLive) {
            this.cacheMinTimeToLive = cacheMinTimeToLive;
        }

        public int getCacheMaxTimeToLive() {
            return cacheMaxTimeToLive;
        }

        public void setCacheMaxTimeToLive(int cacheMaxTimeToLive) {
            this.cacheMaxTimeToLive = cacheMaxTimeToLive;
        }

        public int getCacheNegativeTimeToLive() {
            return cacheNegativeTimeToLive;
        }

        public void setCacheNegativeTimeToLive(int cacheNegativeTimeToLive) {
            this.cacheNegativeTimeToLive = cacheNegativeTimeToLive;
        }

        public long getQueryTimeout() {
            return queryTimeout;
        }

        public void setQueryTimeout(long queryTimeout) {
            this.queryTimeout = queryTimeout;
        }

        public int getMaxQueries() {
            return maxQueries;
        }

        public void setMaxQueries(int maxQueries) {
            this.maxQueries = maxQueries;
        }

        public boolean isRdFlag() {
            return rdFlag;
        }

        public void setRdFlag(boolean rdFlag) {
            this.rdFlag = rdFlag;
        }

        public List<String> getSearchDomains() {
            return searchDomains;
        }

        public void setSearchDomains(List<String> searchDomains) {
            this.searchDomains = searchDomains;
        }

        public int getNdots() {
            return ndots;
        }

        public void setNdots(int ndots) {
            this.ndots = ndots;
        }

        public boolean isRotateServers() {
            return rotateServers;
        }

        public void setRotateServers(boolean rotateServers) {
            this.rotateServers = rotateServers;
        }

        public AddressResolverOptions toAddressResolverOptions() {
            AddressResolverOptions result = new AddressResolverOptions();
            result.setCacheMaxTimeToLive(cacheMaxTimeToLive);
            result.setCacheMinTimeToLive(cacheMinTimeToLive);
            result.setCacheNegativeTimeToLive(cacheNegativeTimeToLive);
            result.setHostsPath(hostsPath);
            result.setMaxQueries(maxQueries);
            result.setNdots(ndots);
            result.setQueryTimeout(queryTimeout);
            result.setRdFlag(rdFlag);
            result.setOptResourceEnabled(optResourceEnabled);
            result.setRotateServers(rotateServers);
            result.setSearchDomains(searchDomains);
            result.setServers(servers);
            return result;
        }
    }

    public VertxOptions toVertxOptions() {
        VertxOptions result = new VertxOptions();
        result.setEventLoopPoolSize(eventLoopPoolSize);
        result.setWorkerPoolSize(workerPoolSize);
        result.setInternalBlockingPoolSize(internalBlockingPoolSize);
        result.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
        result.setBlockedThreadCheckIntervalUnit(blockedThreadCheckIntervalUnit);
        result.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
        result.setMaxEventLoopExecuteTimeUnit(maxEventLoopExecuteTimeUnit);
        result.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
        result.setMaxWorkerExecuteTimeUnit(maxWorkerExecuteTimeUnit);
        result.setWarningExceptionTime(warningExceptionTime);
        result.setWarningExceptionTimeUnit(warningExceptionTimeUnit);
        result.setPreferNativeTransport(preferNativeTransport);
        FileSystemOptions fileSystemOptions = result.getFileSystemOptions();
        fileSystemOptions.setFileCachingEnabled(fileCachingEnabled);
        fileSystemOptions.setClassPathResolvingEnabled(classPathResolvingEnabled);
        if (ha != null) {
            result.setHAEnabled(ha.enabled);
            result.setQuorumSize(ha.quorumSize);
            result.setHAGroup(ha.group);
        }
        if (addressResolver != null) {
            result.setAddressResolverOptions(addressResolver.toAddressResolverOptions());
        }
        if (eventBus != null) {
            EventBusOptions options = eventBus.toEventBusOptions();
            options.setClustered(clustered);
            options.setClusterPublicHost(clusterPublicHost);
            if (clusterPublicPort >= 0) {
                options.setClusterPublicPort(clusterPublicPort);
            }
            options.setClusterPingInterval(clusterPingInterval);
            options.setClusterPingReplyInterval(clusterPingReplyInterval);
            result.setEventBusOptions(options);
        }
        return result;
    }
}
