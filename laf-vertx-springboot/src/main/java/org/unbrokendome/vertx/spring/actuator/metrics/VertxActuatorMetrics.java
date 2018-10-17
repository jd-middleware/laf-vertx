package org.unbrokendome.vertx.spring.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.*;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.*;
import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class VertxActuatorMetrics implements VertxMetricsFactory {

    protected final MeterRegistry meterRegistry;
    protected final VertxMetricsProperties properties;

    public VertxActuatorMetrics(MeterRegistry meterRegistry, VertxMetricsProperties properties) {
        this.meterRegistry = meterRegistry;
        this.properties = properties;
    }

    @Override
    public VertxMetrics metrics(final Vertx vertx, final VertxOptions options) {
        return new VertxMetricsImpl(meterRegistry, properties);
    }

    protected static abstract class AbstractPartMetrics<P extends AbstractPartMetricsProperties> implements Metrics {
        protected final P properties;
        protected final MeterRegistry meterRegistry;
        protected final String prefix;
        protected final LongAdder instancesActive;
        protected final Counter instancesTotal;

        public AbstractPartMetrics(final MeterRegistry meterRegistry, final P properties) {
            this.properties = properties;
            this.meterRegistry = meterRegistry;
            this.prefix = properties.getPrefix() + ".";
            this.instancesActive = meterRegistry.gauge(prefix + "instances.active", new LongAdder());
            this.instancesTotal = meterRegistry.counter(prefix + "instances.total");
            this.instancesActive.increment();
            this.instancesTotal.increment();
        }

        @Override
        public boolean isEnabled() {
            return properties.isEnabled();
        }

        @Override
        public void close() {
            instancesActive.decrement();
        }
    }

    protected static class VertxMetricsImpl extends AbstractPartMetrics<VertxMetricsProperties> implements VertxMetrics {

        protected final LongAdder verticlesDeployed;
        protected final Counter verticlesDeployedTotal;
        protected final LongAdder timersActive;
        protected final Counter timersActiveTotal;

        public VertxMetricsImpl(final MeterRegistry meterRegistry, final VertxMetricsProperties properties) {
            super(meterRegistry, properties);
            this.verticlesDeployed = meterRegistry.gauge(prefix + "verticles.deployed", new LongAdder());
            this.verticlesDeployedTotal = meterRegistry.counter(prefix + "verticles.deployed.total");
            this.timersActive = meterRegistry.gauge(prefix + "timers.active", new LongAdder());
            this.timersActiveTotal = meterRegistry.counter(prefix + "timers.created.total");
        }

        @Override
        public void verticleDeployed(final Verticle verticle) {
            verticlesDeployed.increment();
            verticlesDeployedTotal.increment();
        }

        @Override
        public void verticleUndeployed(final Verticle verticle) {
            verticlesDeployed.decrement();
        }

        @Override
        public void timerCreated(final long id) {
            timersActive.increment();
            timersActiveTotal.increment();
        }

        @Override
        public void timerEnded(final long id, final boolean cancelled) {
            timersActive.decrement();
        }

        @Override
        public EventBusMetrics createMetrics(final EventBus eventBus) {
            return new EventBusMetricsImpl(meterRegistry, properties.getEventBus());
        }

        @Override
        public HttpServerMetrics<?, ?, ?> createMetrics(final HttpServer server, final SocketAddress localAddress,
                                                        final HttpServerOptions options) {
            return new HttpServerMetricsImpl(meterRegistry, properties.getHttp().getServer());
        }

        @Override
        public HttpClientMetrics<?, ?, ?, ?, ?> createMetrics(final HttpClient client, final HttpClientOptions options) {
            return new HttpClientMetricsImpl(meterRegistry, properties.getHttp().getClient());
        }

        @Override
        public TCPMetrics<?> createMetrics(final SocketAddress localAddress, final NetServerOptions options) {
            return new NetServerMetricsImpl(meterRegistry, properties.getNet().getServer());
        }

        @Override
        public TCPMetrics<?> createMetrics(final NetClientOptions options) {
            return new NetClientMetricsImpl(meterRegistry, properties.getNet().getClient());
        }

        @Override
        public DatagramSocketMetrics createMetrics(final DatagramSocket socket, final DatagramSocketOptions options) {
            return new DatagramSocketMetricsImpl(meterRegistry, properties.getDatagramSocket());
        }

        @Override
        public <P> PoolMetrics<?> createMetrics(final P pool, final String poolType, final String poolName, final int maxPoolSize) {
            return new PoolMetricsImpl(meterRegistry, properties.getPool());
        }

        @Override
        public boolean isMetricsEnabled() {
            return true;
        }
    }

    protected static class EventBusMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.EventBusMetricsProperties>
            implements EventBusMetrics<Object> {

        protected Counter messagesScheduledTotal;
        protected Counter messagesScheduledLocal;
        protected Counter messagesScheduledRemote;
        protected Counter messagesHandledTotal;
        protected Counter messagesHandledLocal;
        protected Counter messagesHandledRemote;
        protected Counter messagesCompletedTotal;
        protected Counter messagesCompletedFailure;
        protected Counter messagesCompletedSuccess;
        protected Counter messagesPublishedTotal;
        protected Counter messagesPublishedLocal;
        protected Counter messagesPublishedRemote;
        protected Counter messagesSentTotal;
        protected Counter messagesSentLocal;
        protected Counter messagesSentRemote;
        protected Counter messagesReceivedPublishedTotal;
        protected Counter messagesReceivedPublishedLocal;
        protected Counter messagesReceivedPublishedRemote;
        protected Counter messagesReceivedSentTotal;
        protected Counter messagesReceivedSentLocal;
        protected Counter messagesReceivedSentRemote;
        protected Counter replyFailuresTimeout;
        protected Counter replyFailuresNoHandlers;
        protected Counter replyFailuresRecipientFailure;

        public EventBusMetricsImpl(final MeterRegistry meterRegistry,
                                   final VertxMetricsProperties.EventBusMetricsProperties properties) {
            super(meterRegistry, properties);
            this.messagesScheduledTotal = meterRegistry.counter(prefix + "messages.scheduled.total");
            this.messagesScheduledLocal = meterRegistry.counter(prefix + "messages.scheduled.local");
            this.messagesScheduledRemote = meterRegistry.counter(prefix + "messages.scheduled.remote");
            this.messagesHandledTotal = meterRegistry.counter(prefix + "messages.handled.total");
            this.messagesHandledLocal = meterRegistry.counter(prefix + "messages.handled.local");
            this.messagesHandledRemote = meterRegistry.counter(prefix + "messages.handled.remote");
            this.messagesCompletedTotal = meterRegistry.counter(prefix + "messages.completed.total");
            this.messagesCompletedFailure = meterRegistry.counter(prefix + "messages.completed.failure");
            this.messagesCompletedSuccess = meterRegistry.counter(prefix + "messages.completed.success");
            this.messagesPublishedTotal = meterRegistry.counter(prefix + "messages.published.total");
            this.messagesPublishedLocal = meterRegistry.counter(prefix + "messages.published.local");
            this.messagesPublishedRemote = meterRegistry.counter(prefix + "messages.published.remote");
            this.messagesSentTotal = meterRegistry.counter(prefix + "messages.sent.total");
            this.messagesSentLocal = meterRegistry.counter(prefix + "messages.sent.local");
            this.messagesSentRemote = meterRegistry.counter(prefix + "messages.sent.remote");
            this.messagesReceivedPublishedTotal = meterRegistry.counter(prefix + "messages.receivedPublished.total");
            this.messagesReceivedPublishedLocal = meterRegistry.counter(prefix + "messages.receivedPublished.local");
            this.messagesReceivedPublishedRemote = meterRegistry.counter(prefix + "messages.receivedPublished.remote");
            this.messagesReceivedSentTotal = meterRegistry.counter(prefix + "messages.receivedSent.total");
            this.messagesReceivedSentLocal = meterRegistry.counter(prefix + "messages.receivedSent.local");
            this.messagesReceivedSentRemote = meterRegistry.counter(prefix + "messages.receivedSent.remote");
            this.replyFailuresTimeout = meterRegistry.counter(prefix + "reply.failures.timeout");
            this.replyFailuresNoHandlers = meterRegistry.counter(prefix + "reply.failures.noHandlers");
            this.replyFailuresRecipientFailure = meterRegistry.counter(prefix + "reply.failures.recipientFailure");
        }

        @Override
        public Object handlerRegistered(final String address, final String repliedAddress) {
            return null;
        }

        @Override
        public void handlerUnregistered(final Object handler) {
        }

        @Override
        public void scheduleMessage(final Object handler, final boolean local) {
            messagesScheduledTotal.increment();
            if (local) {
                messagesScheduledLocal.increment();
            } else {
                messagesScheduledRemote.increment();
            }
        }

        @Override
        public void beginHandleMessage(final Object handler, final boolean local) {
            messagesHandledTotal.increment();
            if (local) {
                messagesHandledLocal.increment();
            } else {
                messagesHandledRemote.increment();
            }
        }

        @Override
        public void endHandleMessage(final Object handler, final Throwable failure) {
            messagesCompletedTotal.increment();
            if (failure != null) {
                messagesCompletedFailure.increment();
            } else {
                messagesCompletedSuccess.increment();
            }
        }

        @Override
        public void messageSent(final String address, final boolean publish, final boolean local, final boolean remote) {
            if (publish) {
                messagesPublishedTotal.increment();
                if (local) {
                    messagesPublishedLocal.increment();
                }
                if (remote) {
                    messagesPublishedRemote.increment();
                }
            } else {
                messagesSentTotal.increment();
                if (local) {
                    messagesSentLocal.increment();
                }
                if (remote) {
                    messagesSentRemote.increment();
                }
            }
        }

        @Override
        public void messageReceived(final String address, final boolean publish, final boolean local, final int handlers) {
            if (publish) {
                messagesReceivedPublishedTotal.increment();
                if (local) {
                    messagesReceivedPublishedLocal.increment();
                } else {
                    messagesReceivedPublishedRemote.increment();
                }
            } else {
                messagesReceivedSentTotal.increment();
                if (local) {
                    messagesReceivedSentLocal.increment();
                } else {
                    messagesReceivedSentRemote.increment();
                }
            }
        }

        @Override
        public void messageWritten(final String address, final int numberOfBytes) {
        }

        @Override
        public void messageRead(final String address, final int numberOfBytes) {
        }

        @Override
        public void replyFailure(final String address, final ReplyFailure failure) {
            switch (failure) {
                case TIMEOUT:
                    replyFailuresTimeout.increment();
                    break;
                case NO_HANDLERS:
                    replyFailuresNoHandlers.increment();
                    break;
                case RECIPIENT_FAILURE:
                    replyFailuresRecipientFailure.increment();
                    break;
            }
        }
    }

    protected static class PoolMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.PoolMetricsProperties>
            implements PoolMetrics<StopWatch> {
        protected final Counter tasksSubmitted;
        protected final Counter tasksStarted;
        protected final Counter tasksSucceeded;
        protected final Counter tasksFailed;
        protected final Counter tasksRejected;
        protected final AtomicLong tasksWaitTimeMs;
        protected final AtomicLong tasksDurationMs;


        public PoolMetricsImpl(final MeterRegistry meterRegistry,
                               final VertxMetricsProperties.PoolMetricsProperties properties) {
            super(meterRegistry, properties);
            this.tasksSubmitted = meterRegistry.counter(prefix + "tasks.submitted");
            this.tasksStarted = meterRegistry.counter(prefix + "tasks.started");
            this.tasksSucceeded = meterRegistry.counter(prefix + "tasks.succeeded");
            this.tasksFailed = meterRegistry.counter(prefix + "tasks.failed");
            this.tasksRejected = meterRegistry.counter(prefix + "tasks.rejected");
            this.tasksWaitTimeMs = meterRegistry.gauge(prefix + "tasks.waitTimeMs", new AtomicLong());
            this.tasksDurationMs = meterRegistry.gauge(prefix + "tasks.durationMs", new AtomicLong());
        }

        @Override
        public StopWatch submitted() {
            tasksSubmitted.increment();
            StopWatch stopWatch = new StopWatch();
            stopWatch.setKeepTaskList(false);
            stopWatch.start("submit");
            return stopWatch;
        }

        @Override
        public StopWatch begin(final StopWatch stopWatch) {
            stopWatch.stop();
            tasksStarted.increment();
            tasksWaitTimeMs.set(stopWatch.getLastTaskTimeMillis());
            stopWatch.start("task");
            return stopWatch;
        }

        @Override
        public void rejected(final StopWatch stopWatch) {
            stopWatch.stop();
            tasksRejected.increment();
            tasksWaitTimeMs.set(stopWatch.getLastTaskTimeMillis());
        }

        @Override
        public void end(final StopWatch stopWatch, final boolean succeeded) {
            stopWatch.stop();
            if (succeeded) {
                tasksSucceeded.increment();
            } else {
                tasksFailed.increment();
            }
            tasksDurationMs.set(stopWatch.getLastTaskTimeMillis());
        }
    }

    protected static abstract class AbstractNetworkPartMetrics<P extends AbstractPartMetricsProperties>
            extends AbstractPartMetrics<P> implements NetworkMetrics<StopWatch> {

        public AbstractNetworkPartMetrics(final MeterRegistry meterRegistry,
                                          final P properties) {
            super(meterRegistry, properties);
        }

        @Override
        public void bytesRead(final StopWatch socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {
        }

        @Override
        public void bytesWritten(final StopWatch socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {
        }

        @Override
        public void exceptionOccurred(final StopWatch socketMetric, final SocketAddress remoteAddress, final Throwable t) {
        }
    }

    protected static abstract class AbstractTcpPartMetrics<P extends AbstractPartMetricsProperties>
            extends AbstractNetworkPartMetrics<P> implements TCPMetrics<StopWatch> {
        protected final LongAdder socketNumConnected;
        protected final AtomicLong socketConnectionDurationMs;

        public AbstractTcpPartMetrics(final MeterRegistry meterRegistry, final P properties) {
            super(meterRegistry, properties);
            this.socketNumConnected = meterRegistry.gauge(prefix + "socket.numConnected", new LongAdder());
            this.socketConnectionDurationMs = meterRegistry.gauge(prefix + "socket.connectionDurationMs", new AtomicLong());
        }

        @Override
        public StopWatch connected(final SocketAddress remoteAddress, final String remoteName) {
            socketNumConnected.increment();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            return stopWatch;
        }

        @Override
        public void disconnected(final StopWatch stopWatch, final SocketAddress remoteAddress) {
            stopWatch.stop();
            socketNumConnected.decrement();
            socketConnectionDurationMs.set(stopWatch.getTotalTimeMillis());
        }
    }

    protected static class HttpServerMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.HttpServerMetricsProperties>
            implements HttpServerMetrics<StopWatch, StopWatch, StopWatch> {

        protected final Counter requestsCountTotal;
        protected final Counter requestsCountOptions;
        protected final Counter requestsCountGet;
        protected final Counter requestsCountHead;
        protected final Counter requestsCountPost;
        protected final Counter requestsCountPut;
        protected final Counter requestsCountPatch;
        protected final Counter requestsCountDelete;
        protected final Counter requestsCountTrace;
        protected final Counter requestsCountConnect;
        protected final Counter requestsReset;
        protected final Counter requestsPushed;
        protected final Counter requestsUpgraded;
        protected final Counter websocketsConnected;
        protected final Counter websocketsDisconnected;
        protected final AtomicLong websocketsConnectTimeMs;
        protected final Counter responsesCountTotal;
        protected final Counter responsesCountErrorTotal;
        protected final Counter responsesCountErrorServer;
        protected final Counter responsesCountErrorClient;
        protected final Counter responsesCountRedirect;
        protected final Counter responsesCountSuccess;
        protected final AtomicLong responsesTimeTotal;
        protected final AtomicLong responsesTimeErrorTotal;
        protected final AtomicLong responsesTimeErrorServer;
        protected final AtomicLong responsesTimeErrorClient;
        protected final AtomicLong responsesTimeRedirect;
        protected final AtomicLong responsesTimeSuccess;

        public HttpServerMetricsImpl(final MeterRegistry meterRegistry,
                                     final VertxMetricsProperties.HttpServerMetricsProperties properties) {
            super(meterRegistry, properties);
            this.requestsCountTotal = meterRegistry.counter(prefix + "requests.count.total");
            this.requestsCountOptions = meterRegistry.counter(prefix + "requests.count.options");
            this.requestsCountGet = meterRegistry.counter(prefix + "requests.count.get");
            this.requestsCountHead = meterRegistry.counter(prefix + "requests.count.head");
            this.requestsCountPost = meterRegistry.counter(prefix + "requests.count.post");
            this.requestsCountPut = meterRegistry.counter(prefix + "requests.count.put");
            this.requestsCountPatch = meterRegistry.counter(prefix + "requests.count.patch");
            this.requestsCountDelete = meterRegistry.counter(prefix + "requests.count.delete");
            this.requestsCountTrace = meterRegistry.counter(prefix + "requests.count.trace");
            this.requestsCountConnect = meterRegistry.counter(prefix + "requests.count.connect");
            this.requestsReset = meterRegistry.counter(prefix + "requests.reset");
            this.requestsPushed = meterRegistry.counter(prefix + "requests.pushed");
            this.requestsUpgraded = meterRegistry.counter(prefix + "requests.upgraded");
            this.websocketsConnected = meterRegistry.counter(prefix + "websockets.connected");
            this.websocketsDisconnected = meterRegistry.counter(prefix + "websockets.disconnected");
            this.websocketsConnectTimeMs = meterRegistry.gauge(prefix + "websockets.connectTimeMs", new AtomicLong());
            this.responsesCountTotal = meterRegistry.counter(prefix + "responses.count.total");
            this.responsesCountErrorTotal = meterRegistry.counter(prefix + "responses.count.error.total");
            this.responsesCountErrorServer = meterRegistry.counter(prefix + "responses.count.error.server");
            this.responsesCountErrorClient = meterRegistry.counter(prefix + "responses.count.error.client");
            this.responsesCountRedirect = meterRegistry.counter(prefix + "responses.count.redirect");
            this.responsesCountSuccess = meterRegistry.counter(prefix + "responses.count.success");
            this.responsesTimeTotal = meterRegistry.gauge(prefix + "responses.time.total", new AtomicLong());
            this.responsesTimeErrorTotal = meterRegistry.gauge(prefix + "responses.time.error.total", new AtomicLong());
            this.responsesTimeErrorServer = meterRegistry.gauge(prefix + "responses.time.error.server", new AtomicLong());
            this.responsesTimeErrorClient = meterRegistry.gauge(prefix + "responses.time.error.client", new AtomicLong());
            this.responsesTimeRedirect = meterRegistry.gauge(prefix + "responses.time.redirect", new AtomicLong());
            this.responsesTimeSuccess = meterRegistry.gauge(prefix + "responses.time.success", new AtomicLong());
        }

        @Override
        public StopWatch requestBegin(final StopWatch socketWatch, final HttpServerRequest request) {
            requestsCountTotal.increment();
            switch (request.method()) {
                case OPTIONS:
                    requestsCountOptions.increment();
                    break;
                case GET:
                    requestsCountGet.increment();
                    break;
                case HEAD:
                    requestsCountHead.increment();
                    break;
                case POST:
                    requestsCountPost.increment();
                    break;
                case PUT:
                    requestsCountPut.increment();
                    break;
                case PATCH:
                    requestsCountPatch.increment();
                    break;
                case DELETE:
                    requestsCountDelete.increment();
                    break;
                case TRACE:
                    requestsCountTrace.increment();
                    break;
                case CONNECT:
                    requestsCountConnect.increment();
                    break;
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("request");
            return stopWatch;
        }

        @Override
        public void requestReset(final StopWatch requestWatch) {
            requestWatch.stop();
            requestsReset.increment();
        }

        @Override
        public StopWatch responsePushed(final StopWatch socketWatch, final HttpMethod method, final String uri,
                                        final HttpServerResponse response) {
            requestsPushed.increment();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            return stopWatch;
        }

        @Override
        public void responseEnd(final StopWatch requestWatch, final HttpServerResponse response) {
            requestWatch.stop();
            responsesCountTotal.increment();
            int statusCode = response.getStatusCode();
            long totalTimeMillis = requestWatch.getTotalTimeMillis();
            responsesTimeTotal.set(totalTimeMillis);
            if (statusCode > 400) {
                responsesTimeErrorTotal.set(totalTimeMillis);
                responsesCountErrorTotal.increment();
                if (statusCode > 500) {
                    responsesCountErrorServer.increment();
                    responsesTimeErrorServer.set(totalTimeMillis);
                } else {
                    responsesCountErrorClient.increment();
                    responsesTimeErrorClient.set(totalTimeMillis);
                }
            } else if (statusCode > 300) {
                responsesCountRedirect.increment();
                responsesTimeRedirect.set(totalTimeMillis);
            } else if (statusCode > 200) {
                responsesCountSuccess.increment();
                responsesTimeSuccess.set(totalTimeMillis);
            }
        }

        @Override
        public StopWatch upgrade(StopWatch requestWatch, ServerWebSocket serverWebSocket) {
            requestWatch.stop();
            requestsUpgraded.increment();
            requestWatch.start("websocket");
            return requestWatch;
        }

        @Override
        public StopWatch connected(StopWatch socketMetric, ServerWebSocket serverWebSocket) {
            websocketsConnected.increment();
            StopWatch websocketWatch = new StopWatch();
            websocketWatch.start("websocket");
            return websocketWatch;
        }

        @Override
        public void disconnected(StopWatch websocketWatch) {
            websocketWatch.stop();
            websocketsDisconnected.increment();
            websocketsConnectTimeMs.set(websocketWatch.getLastTaskTimeMillis());
        }
    }

    protected static class HttpClientMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.HttpClientMetricsProperties>
            implements HttpClientMetrics<StopWatch, StopWatch, StopWatch, StopWatch, StopWatch> {

        protected final LongAdder endpointsActive;
        protected final Counter endpointsTotal;
        protected final LongAdder requestsQueuedActive;
        protected final Counter requestsQueuedTotal;
        protected final AtomicLong requestsQueueTime;
        protected final LongAdder endpointsConnected;
        protected final Counter requestsSent;
        protected final AtomicLong requestsSendTime;
        protected final AtomicLong requestsWaitTime;
        protected final Counter requestsReset;
        protected final Counter websocketsConnected;
        protected final Counter websocketsDisconnected;
        protected final AtomicLong websocketsConnectTimeMs;

        public HttpClientMetricsImpl(final MeterRegistry meterRegistry,
                                     final VertxMetricsProperties.HttpClientMetricsProperties properties) {
            super(meterRegistry, properties);
            this.endpointsActive = meterRegistry.gauge(prefix + "endpoints.active", new LongAdder());
            this.endpointsTotal = meterRegistry.counter(prefix + "endpoints.total");
            this.requestsQueuedActive = meterRegistry.gauge(prefix + "requests.queued.active", new LongAdder());
            this.requestsQueuedTotal = meterRegistry.counter(prefix + "requests.queued.total");
            this.requestsQueueTime = meterRegistry.gauge(prefix + "requests.queueTime", new AtomicLong());
            this.endpointsConnected = meterRegistry.gauge(prefix + "endpoints.connected", new LongAdder());
            this.requestsSent = meterRegistry.counter(prefix + "requests.sent");
            this.requestsSendTime = meterRegistry.gauge(prefix + "requests.sendTime", new AtomicLong());
            this.requestsWaitTime = meterRegistry.gauge(prefix + "requests.waitTime", new AtomicLong());
            this.requestsReset = meterRegistry.counter(prefix + "requests.reset");
            this.websocketsConnected = meterRegistry.counter(prefix + "websockets.connected");
            this.websocketsDisconnected = meterRegistry.counter(prefix + "websockets.disconnected");
            this.websocketsConnectTimeMs = meterRegistry.gauge(prefix + "websockets.connectTimeMs", new AtomicLong());
        }

        @Override
        public StopWatch createEndpoint(final String host, final int port, final int maxPoolSize) {
            endpointsActive.increment();
            endpointsTotal.increment();
            StopWatch endpointWatch = new StopWatch();
            endpointWatch.start();
            return endpointWatch;
        }

        @Override
        public void closeEndpoint(final String host, final int port, final StopWatch endpointWatch) {
            endpointWatch.stop();
            endpointsActive.decrement();
        }

        @Override
        public StopWatch enqueueRequest(final StopWatch endpointWatch) {
            requestsQueuedActive.increment();
            requestsQueuedTotal.increment();
            StopWatch taskWatch = new StopWatch();
            taskWatch.start();
            return taskWatch;
        }

        @Override
        public void dequeueRequest(final StopWatch endpointWatch, final StopWatch taskWatch) {
            taskWatch.stop();
            requestsQueuedActive.decrement();
            requestsQueueTime.set(taskWatch.getTotalTimeMillis());
        }

        @Override
        public void endpointConnected(final StopWatch endpointWatch, final StopWatch socketWatch) {
            endpointsConnected.increment();
        }

        @Override
        public void endpointDisconnected(final StopWatch endpointWatch, final StopWatch socketWatch) {
            endpointsConnected.decrement();
        }

        @Override
        public StopWatch requestBegin(final StopWatch endpointWatch, final StopWatch socketMetric, final SocketAddress localAddress,
                                      final SocketAddress remoteAddress, final HttpClientRequest request) {
            requestsSent.increment();
            StopWatch requestWatch = new StopWatch();
            requestWatch.start();
            return requestWatch;
        }

        @Override
        public void requestEnd(final StopWatch requestWatch) {
            requestWatch.stop();
            requestsSendTime.set(requestWatch.getLastTaskTimeMillis());
            requestWatch.start("awaitResponse");
        }

        @Override
        public void responseBegin(final StopWatch requestWatch, final HttpClientResponse response) {
            requestWatch.stop();
            requestsWaitTime.set(requestWatch.getLastTaskTimeMillis());
            requestWatch.start("readResponse");
        }

        @Override
        public StopWatch responsePushed(final StopWatch endpointWatch, final StopWatch socketWatch, final SocketAddress localAddress,
                                        final SocketAddress remoteAddress, final HttpClientRequest request) {
            return null;
        }

        @Override
        public void requestReset(final StopWatch requestWatch) {
            requestWatch.stop();
            requestsReset.increment();
        }

        @Override
        public void responseEnd(final StopWatch requestWatch, final HttpClientResponse response) {

        }

        @Override
        public StopWatch connected(final StopWatch endpointWatch, final StopWatch socketWatch, final WebSocket webSocket) {
            websocketsConnected.increment();
            StopWatch websocketWatch = new StopWatch();
            websocketWatch.start("websocket");
            return websocketWatch;
        }

        @Override
        public void disconnected(final StopWatch websocketWatch) {
            websocketWatch.stop();
            websocketsDisconnected.increment();
            websocketsConnectTimeMs.set(websocketWatch.getLastTaskTimeMillis());
        }
    }

    protected static class NetServerMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.NetServerMetricsProperties>
            implements TCPMetrics<StopWatch> {

        public NetServerMetricsImpl(final MeterRegistry meterRegistry,
                                    final VertxMetricsProperties.NetServerMetricsProperties properties) {
            super(meterRegistry, properties);
        }
    }

    protected static class NetClientMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.NetClientMetricsProperties>
            implements TCPMetrics<StopWatch> {

        public NetClientMetricsImpl(final MeterRegistry meterRegistry,
                                    final VertxMetricsProperties.NetClientMetricsProperties properties) {
            super(meterRegistry, properties);
        }
    }

    protected static class DatagramSocketMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.DatagramSocketProperties>
            implements DatagramSocketMetrics {

        protected final Counter datagramSocketsListening;

        public DatagramSocketMetricsImpl(final MeterRegistry meterRegistry,
                                         final VertxMetricsProperties.DatagramSocketProperties properties) {
            super(meterRegistry, properties);
            this.datagramSocketsListening = meterRegistry.counter(prefix + "datagram.sockets.listening");
        }

        @Override
        public void listening(final String localName, final SocketAddress localAddress) {
            datagramSocketsListening.increment();
        }

        @Override
        public void bytesRead(final Void socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {
        }

        @Override
        public void bytesWritten(final Void socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {
        }

        @Override
        public void exceptionOccurred(final Void socketMetric, final SocketAddress remoteAddress, final Throwable t) {
        }
    }
}
