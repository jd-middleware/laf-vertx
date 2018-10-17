package org.unbrokendome.vertx.spring.actuator.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vertx.metrics")
public class VertxMetricsProperties extends AbstractPartMetricsProperties {

    protected final EventBusMetricsProperties eventBus = new EventBusMetricsProperties();
    protected final PoolMetricsProperties pool = new PoolMetricsProperties();
    protected final NetMetricsProperties net = new NetMetricsProperties();
    protected final HttpMetricsProperties http = new HttpMetricsProperties();
    protected final DatagramSocketProperties datagramSocket = new DatagramSocketProperties();

    public VertxMetricsProperties() {
        super("vertx");
    }

    public EventBusMetricsProperties getEventBus() {
        return eventBus;
    }

    public PoolMetricsProperties getPool() {
        return pool;
    }

    public NetMetricsProperties getNet() {
        return net;
    }

    public HttpMetricsProperties getHttp() {
        return http;
    }

    public DatagramSocketProperties getDatagramSocket() {
        return datagramSocket;
    }

    public static class EventBusMetricsProperties extends AbstractPartMetricsProperties {

        public EventBusMetricsProperties() {
            super("eventbus");
        }
    }

    public static class PoolMetricsProperties extends AbstractPartMetricsProperties {

        public PoolMetricsProperties() {
            super("pool");
        }
    }

    public static class NetMetricsProperties {

        protected final NetClientMetricsProperties client = new NetClientMetricsProperties();
        protected final NetServerMetricsProperties server = new NetServerMetricsProperties();

        public NetClientMetricsProperties getClient() {
            return client;
        }

        public NetServerMetricsProperties getServer() {
            return server;
        }
    }

    public static class NetClientMetricsProperties extends AbstractPartMetricsProperties {

        public NetClientMetricsProperties() {
            super("net.client");
        }
    }

    public static class NetServerMetricsProperties extends AbstractPartMetricsProperties {
        public NetServerMetricsProperties() {
            super("net.server");
        }
    }

    public static class HttpMetricsProperties {
        protected final HttpClientMetricsProperties client = new HttpClientMetricsProperties();
        protected final HttpServerMetricsProperties server = new HttpServerMetricsProperties();

        public HttpClientMetricsProperties getClient() {
            return client;
        }

        public HttpServerMetricsProperties getServer() {
            return server;
        }
    }

    public static class HttpClientMetricsProperties extends AbstractPartMetricsProperties {

        public HttpClientMetricsProperties() {
            super("http.client");
        }
    }

    public static class HttpServerMetricsProperties extends AbstractPartMetricsProperties {

        public HttpServerMetricsProperties() {
            super("http.server");
        }
    }

    public static class DatagramSocketProperties extends AbstractPartMetricsProperties {

        public DatagramSocketProperties() {
            super("datagram");
        }
    }
}


