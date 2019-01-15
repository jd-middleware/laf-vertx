package com.jd.laf.web.vertx.spring.boot;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpVersion;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.laf.web.vertx.RoutingVerticle.DEFAULT_ROUTING_CONFIG_FILE;
import static io.vertx.core.DeploymentOptions.*;
import static io.vertx.core.VertxOptions.*;
import static io.vertx.core.http.HttpServerOptions.*;
import static io.vertx.core.net.NetworkOptions.DEFAULT_LOG_ENABLED;
import static io.vertx.core.net.TCPSSLOptions.DEFAULT_TCP_QUICKACK;

@ConfigurationProperties(prefix = "vertx")
public class VertxWebProperties {

    protected DeploymentProperties routing = new DeploymentProperties();

    protected String file = DEFAULT_ROUTING_CONFIG_FILE;

    protected HttpServerProperties http = new HttpServerProperties();

    public DeploymentProperties getRouting() {
        return routing;
    }

    public void setRouting(DeploymentProperties routing) {
        this.routing = routing;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public HttpServerProperties getHttp() {
        return http;
    }

    public void setHttp(HttpServerProperties http) {
        this.http = http;
    }

    public static class DeploymentProperties {
        private boolean worker = DEFAULT_WORKER;
        private String workerPoolName;
        private int workerPoolSize = DEFAULT_WORKER_POOL_SIZE;
        private long maxWorkerExecuteTime = DEFAULT_MAX_WORKER_EXECUTE_TIME;
        private TimeUnit maxWorkerExecuteTimeUnit = DEFAULT_MAX_WORKER_EXECUTE_TIME_UNIT;
        private boolean ha = DEFAULT_HA;
        private List<String> extraClasspath;
        private int instances = DEFAULT_INSTANCES;
        private String isolationGroup = DEFAULT_ISOLATION_GROUP;
        private List<String> isolatedClasses;

        public boolean isWorker() {
            return worker;
        }

        public void setWorker(boolean worker) {
            this.worker = worker;
        }

        public String getIsolationGroup() {
            return isolationGroup;
        }

        public void setIsolationGroup(String isolationGroup) {
            this.isolationGroup = isolationGroup;
        }

        public String getWorkerPoolName() {
            return workerPoolName;
        }

        public void setWorkerPoolName(String workerPoolName) {
            this.workerPoolName = workerPoolName;
        }

        public int getWorkerPoolSize() {
            return workerPoolSize;
        }

        public void setWorkerPoolSize(int workerPoolSize) {
            this.workerPoolSize = workerPoolSize;
        }

        public long getMaxWorkerExecuteTime() {
            return maxWorkerExecuteTime;
        }

        public void setMaxWorkerExecuteTime(long maxWorkerExecuteTime) {
            this.maxWorkerExecuteTime = maxWorkerExecuteTime;
        }

        public TimeUnit getMaxWorkerExecuteTimeUnit() {
            return maxWorkerExecuteTimeUnit;
        }

        public void setMaxWorkerExecuteTimeUnit(TimeUnit maxWorkerExecuteTimeUnit) {
            this.maxWorkerExecuteTimeUnit = maxWorkerExecuteTimeUnit;
        }

        public boolean isHa() {
            return ha;
        }

        public void setHa(boolean ha) {
            this.ha = ha;
        }

        public List<String> getExtraClasspath() {
            return extraClasspath;
        }

        public void setExtraClasspath(List<String> extraClasspath) {
            this.extraClasspath = extraClasspath;
        }

        public int getInstances() {
            return instances;
        }

        public void setInstances(int instances) {
            this.instances = instances;
        }

        public List<String> getIsolatedClasses() {
            return isolatedClasses;
        }

        public void setIsolatedClasses(List<String> isolatedClasses) {
            this.isolatedClasses = isolatedClasses;
        }

        public DeploymentOptions toDeploymentOptions() {
            DeploymentOptions result = new DeploymentOptions();
            result.setWorker(worker);
            result.setWorkerPoolName(workerPoolName);
            result.setWorkerPoolSize(workerPoolSize);
            result.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
            result.setMaxWorkerExecuteTimeUnit(maxWorkerExecuteTimeUnit);
            result.setHa(ha);
            result.setInstances(instances);
            result.setExtraClasspath(extraClasspath);
            result.setIsolationGroup(isolationGroup);
            result.setIsolatedClasses(isolatedClasses);
            return result;
        }
    }

    public static class HttpServerProperties {

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
        private boolean sni = DEFAULT_SNI;

        private boolean compressionSupported = DEFAULT_COMPRESSION_SUPPORTED;
        private int compressionLevel = DEFAULT_COMPRESSION_LEVEL;
        private int maxWebsocketFrameSize = DEFAULT_MAX_WEBSOCKET_FRAME_SIZE;
        private int maxWebsocketMessageSize = DEFAULT_MAX_WEBSOCKET_MESSAGE_SIZE;
        private String websocketSubProtocols;
        private boolean handle100ContinueAutomatically = DEFAULT_HANDLE_100_CONTINE_AUTOMATICALLY;
        private int maxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
        private int maxInitialLineLength = DEFAULT_MAX_INITIAL_LINE_LENGTH;
        private int maxHeaderSize = DEFAULT_MAX_HEADER_SIZE;
        private Http2Settings initialSettings = new Http2Settings().setMaxConcurrentStreams(DEFAULT_INITIAL_SETTINGS_MAX_CONCURRENT_STREAMS);
        private List<HttpVersion> alpnVersions = new ArrayList<>(DEFAULT_ALPN_VERSIONS);
        private int http2ConnectionWindowSize = DEFAULT_HTTP2_CONNECTION_WINDOW_SIZE;
        private boolean decompressionSupported = DEFAULT_DECOMPRESSION_SUPPORTED;
        private boolean acceptUnmaskedFrames = DEFAULT_ACCEPT_UNMASKED_FRAMES;
        private int decoderInitialBufferSize = DEFAULT_DECODER_INITIAL_BUFFER_SIZE;
        private boolean websocketPerFrameCompressionSupported = DEFAULT_WEBSOCKET_SUPPORT_DEFLATE_FRAME_COMPRESSION;
        private boolean websocketPerMessageCompressionSupported = DEFAULT_WEBSOCKET_SUPPORT_PERMESSAGE_DEFLATE_COMPRESSION;
        private int websocketCompressionLevel = DEFAULT_WEBSOCKET_COMPRESSION_LEVEL;
        private boolean websocketAllowServerNoContext = DEFAULT_WEBSOCKET_COMPRESSION_PREFERRED_CLIENT_NO_CONTEXT;
        private boolean websocketCompressionPreferredClientNoContext = DEFAULT_WEBSOCKET_COMPRESSION_ALLOW_SERVER_NO_CONTEXT;

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

        public boolean isSni() {
            return sni;
        }

        public void setSni(boolean sni) {
            this.sni = sni;
        }

        public boolean isCompressionSupported() {
            return compressionSupported;
        }

        public void setCompressionSupported(boolean compressionSupported) {
            this.compressionSupported = compressionSupported;
        }

        public int getCompressionLevel() {
            return compressionLevel;
        }

        public void setCompressionLevel(int compressionLevel) {
            this.compressionLevel = compressionLevel;
        }

        public int getMaxWebsocketFrameSize() {
            return maxWebsocketFrameSize;
        }

        public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
            this.maxWebsocketFrameSize = maxWebsocketFrameSize;
        }

        public int getMaxWebsocketMessageSize() {
            return maxWebsocketMessageSize;
        }

        public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
            this.maxWebsocketMessageSize = maxWebsocketMessageSize;
        }

        public String getWebsocketSubProtocols() {
            return websocketSubProtocols;
        }

        public void setWebsocketSubProtocols(String websocketSubProtocols) {
            this.websocketSubProtocols = websocketSubProtocols;
        }

        public boolean isHandle100ContinueAutomatically() {
            return handle100ContinueAutomatically;
        }

        public void setHandle100ContinueAutomatically(boolean handle100ContinueAutomatically) {
            this.handle100ContinueAutomatically = handle100ContinueAutomatically;
        }

        public int getMaxChunkSize() {
            return maxChunkSize;
        }

        public void setMaxChunkSize(int maxChunkSize) {
            this.maxChunkSize = maxChunkSize;
        }

        public int getMaxInitialLineLength() {
            return maxInitialLineLength;
        }

        public void setMaxInitialLineLength(int maxInitialLineLength) {
            this.maxInitialLineLength = maxInitialLineLength;
        }

        public int getMaxHeaderSize() {
            return maxHeaderSize;
        }

        public void setMaxHeaderSize(int maxHeaderSize) {
            this.maxHeaderSize = maxHeaderSize;
        }

        public Http2Settings getInitialSettings() {
            return initialSettings;
        }

        public void setInitialSettings(Http2Settings initialSettings) {
            this.initialSettings = initialSettings;
        }

        public List<HttpVersion> getAlpnVersions() {
            return alpnVersions;
        }

        public void setAlpnVersions(List<HttpVersion> alpnVersions) {
            this.alpnVersions = alpnVersions;
        }

        public int getHttp2ConnectionWindowSize() {
            return http2ConnectionWindowSize;
        }

        public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
            this.http2ConnectionWindowSize = http2ConnectionWindowSize;
        }

        public boolean isDecompressionSupported() {
            return decompressionSupported;
        }

        public void setDecompressionSupported(boolean decompressionSupported) {
            this.decompressionSupported = decompressionSupported;
        }

        public boolean isAcceptUnmaskedFrames() {
            return acceptUnmaskedFrames;
        }

        public void setAcceptUnmaskedFrames(boolean acceptUnmaskedFrames) {
            this.acceptUnmaskedFrames = acceptUnmaskedFrames;
        }

        public int getDecoderInitialBufferSize() {
            return decoderInitialBufferSize;
        }

        public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
            this.decoderInitialBufferSize = decoderInitialBufferSize;
        }

        public boolean isWebsocketPerFrameCompressionSupported() {
            return websocketPerFrameCompressionSupported;
        }

        public void setWebsocketPerFrameCompressionSupported(boolean websocketPerFrameCompressionSupported) {
            this.websocketPerFrameCompressionSupported = websocketPerFrameCompressionSupported;
        }

        public boolean isWebsocketPerMessageCompressionSupported() {
            return websocketPerMessageCompressionSupported;
        }

        public void setWebsocketPerMessageCompressionSupported(boolean websocketPerMessageCompressionSupported) {
            this.websocketPerMessageCompressionSupported = websocketPerMessageCompressionSupported;
        }

        public int getWebsocketCompressionLevel() {
            return websocketCompressionLevel;
        }

        public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
            this.websocketCompressionLevel = websocketCompressionLevel;
        }

        public boolean isWebsocketAllowServerNoContext() {
            return websocketAllowServerNoContext;
        }

        public void setWebsocketAllowServerNoContext(boolean websocketAllowServerNoContext) {
            this.websocketAllowServerNoContext = websocketAllowServerNoContext;
        }

        public boolean isWebsocketCompressionPreferredClientNoContext() {
            return websocketCompressionPreferredClientNoContext;
        }

        public void setWebsocketCompressionPreferredClientNoContext(boolean websocketCompressionPreferredClientNoContext) {
            this.websocketCompressionPreferredClientNoContext = websocketCompressionPreferredClientNoContext;
        }

        public HttpServerOptions toHttpServerOptions() {
            HttpServerOptions result = new HttpServerOptions();

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
            result.setSni(sni);
            result.setCompressionSupported(compressionSupported);
            result.setCompressionLevel(compressionLevel);
            result.setDecompressionSupported(decompressionSupported);
            result.setMaxChunkSize(maxChunkSize);
            result.setMaxInitialLineLength(maxInitialLineLength);
            result.setMaxHeaderSize(maxHeaderSize);
            result.setHandle100ContinueAutomatically(handle100ContinueAutomatically);
            result.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
            result.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
            result.setWebsocketSubProtocols(websocketSubProtocols);
            result.setWebsocketAllowServerNoContext(websocketAllowServerNoContext);
            result.setWebsocketCompressionLevel(websocketCompressionLevel);
            result.setWebsocketPreferredClientNoContext(websocketCompressionPreferredClientNoContext);
            result.setPerFrameWebsocketCompressionSupported(websocketPerFrameCompressionSupported);
            result.setPerMessageWebsocketCompressionSupported(websocketPerMessageCompressionSupported);
            result.setInitialSettings(initialSettings);
            result.setAlpnVersions(alpnVersions);
            result.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
            result.setAcceptUnmaskedFrames(acceptUnmaskedFrames);
            result.setDecoderInitialBufferSize(decoderInitialBufferSize);

            return result;

        }
    }
}
