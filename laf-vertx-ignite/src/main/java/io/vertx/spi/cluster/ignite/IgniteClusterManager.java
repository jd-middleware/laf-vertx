/*
 * Copyright (c) 2015 The original author or authors
 * ---------------------------------
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.spi.cluster.ignite;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.spi.cluster.AsyncMultiMap;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeListener;
import io.vertx.spi.cluster.ignite.impl.AsyncMapImpl;
import io.vertx.spi.cluster.ignite.impl.AsyncMultiMapImpl;
import io.vertx.spi.cluster.ignite.impl.MapImpl;
import org.apache.ignite.*;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.lang.IgnitePredicate;

import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static javax.cache.expiry.Duration.ETERNAL;
import static org.apache.ignite.events.EventType.*;

/**
 * Apache Ignite based cluster manager.
 *
 * @author Andrey Gura
 */
public class IgniteClusterManager implements ClusterManager {

    protected static final Logger log = LoggerFactory.getLogger(IgniteClusterManager.class);

    // Default Ignite configuration file
    protected static final String DEFAULT_CONFIG_FILE = "default-ignite.xml";

    // User defined Ignite configuration file
    protected static final String CONFIG_FILE = "ignite.xml";

    public static final String VERTX_CACHE_TEMPLATE_NAME = "*";

    protected static final String VERTX_NODE_PREFIX = "vertx.ignite.node.";

    // Workaround for https://github.com/vert-x3/vertx-ignite/issues/63
    protected static final ExpiryPolicy DEFAULT_EXPIRY_POLICY = new ClearExpiryPolicy();

    protected final Queue<String> pendingLocks = new ConcurrentLinkedQueue<>();

    protected Vertx vertx;

    protected IgniteConfiguration cfg;
    protected Ignite ignite;
    protected boolean customIgnite;

    protected String nodeID = UUID.randomUUID().toString();
    protected NodeListener nodeListener;
    protected IgnitePredicate<Event> eventListener;

    protected volatile boolean active;

    protected final Object monitor = new Object();

    protected CollectionConfiguration collectionCfg;

    /**
     * Default constructor. Cluster manager will get configuration from classpath.
     */
    @SuppressWarnings("unused")
    public IgniteClusterManager() {
        System.setProperty("IGNITE_NO_SHUTDOWN_HOOK", "true");
    }

    /**
     * Creates cluster manager instance with given Ignite configuration.
     * Use this constructor in order to configure cluster manager programmatically.
     *
     * @param cfg {@code IgniteConfiguration} instance.
     */
    @SuppressWarnings("unused")
    public IgniteClusterManager(IgniteConfiguration cfg) {
        this.cfg = cfg;
        setNodeID(cfg);
    }

    /**
     * Creates cluster manager instance with given Spring XML configuration file.
     * Use this constructor in order to configure cluster manager programmatically.
     *
     * @param configFile {@code URL} path to Spring XML configuration file.
     */
    @SuppressWarnings("unused")
    public IgniteClusterManager(URL configFile) {
        this.cfg = loadConfiguration(configFile);
    }

    /**
     * Creates cluster manager instance with given {@code Ignite} instance.
     *
     * @param ignite {@code Ignite} instance.
     */
    public IgniteClusterManager(Ignite ignite) {
        Objects.requireNonNull(ignite, "Ignite instance can't be null.");
        this.ignite = ignite;
        this.customIgnite = true;
    }

    /**
     * Returns instance of {@code Ignite}.
     *
     * @return {@code Ignite} instance.
     */
    public Ignite getIgniteInstance() {
        return ignite;
    }

    @Override
    public void setVertx(final Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void nodeListener(NodeListener nodeListener) {
        this.nodeListener = nodeListener;
    }

    @Override
    public <K, V> void getAsyncMultiMap(final String name, final Handler<AsyncResult<AsyncMultiMap<K, V>>> handler) {
        vertx.executeBlocking(
                fut -> fut.complete(new AsyncMultiMapImpl<>(this.<K, Set<V>>getCache(name), vertx)), handler
        );
    }

    @Override
    public <K, V> void getAsyncMap(final String name, final Handler<AsyncResult<AsyncMap<K, V>>> handler) {
        vertx.executeBlocking(
                fut -> fut.complete(new AsyncMapImpl<>(getCache(name), vertx)), handler
        );
    }

    @Override
    public <K, V> Map<K, V> getSyncMap(final String name) {
        return new MapImpl<>(getCache(name));
    }

    @Override
    public void getLockWithTimeout(final String name, final long timeout, final Handler<AsyncResult<Lock>> handler) {
        vertx.executeBlocking(fut -> {
            boolean locked;
            try {
                IgniteQueue<String> queue = getQueue(name, true);
                pendingLocks.offer(name);
                locked = queue.offer(getNodeID(), timeout, TimeUnit.MILLISECONDS);

                if (!locked) {
                    // EVT_NODE_LEFT/EVT_NODE_FAILED event might be already handled, so trying get lock again if
                    // node left topology.
                    // Use IgniteSempahore when it will be fixed.
                    String ownerId = queue.peek();
                    ClusterNode ownerNode = ignite.cluster().forNodeId(UUID.fromString(ownerId)).node();
                    if (ownerNode == null) {
                        queue.remove(ownerId);
                        locked = queue.offer(getNodeID(), timeout, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (Exception e) {
                throw new VertxException("Error during getting lock " + name, e);
            } finally {
                pendingLocks.remove(name);
            }

            if (locked) {
                fut.complete(new LockImpl(name));
            } else {
                throw new VertxException("Timed out waiting to get lock " + name);
            }
        }, false, handler);
    }

    @Override
    public void getCounter(final String name, final Handler<AsyncResult<Counter>> handler) {
        vertx.executeBlocking(fut -> fut.complete(new CounterImpl(ignite.atomicLong(name, 0, true))), handler);
    }

    @Override
    public String getNodeID() {
        return nodeID;
    }

    @Override
    public List<String> getNodes() {
        return ignite.cluster().nodes().stream()
                .map(IgniteClusterManager::nodeId).collect(Collectors.toList());
    }

    @Override
    public void join(final Handler<AsyncResult<Void>> handler) {
        synchronized (monitor) {
            vertx.executeBlocking(fut -> {
                if (!active) {
                    active = true;

                    if (!customIgnite) {
                        ignite = cfg == null ? Ignition.start(loadConfiguration()) : Ignition.start(cfg);
                    }
                    nodeID = nodeId(ignite.cluster().localNode());

                    for (CacheConfiguration cacheCfg : ignite.configuration().getCacheConfiguration()) {
                        if (cacheCfg.getName().equals(VERTX_CACHE_TEMPLATE_NAME)) {
                            collectionCfg = new CollectionConfiguration();
                            collectionCfg.setAtomicityMode(cacheCfg.getAtomicityMode());
                            collectionCfg.setBackups(cacheCfg.getBackups());
                            break;
                        }
                    }

                    if (collectionCfg == null) {
                        collectionCfg = new CollectionConfiguration();
                    }

                    eventListener = event -> {
                        if (!active) {
                            return false;
                        }

                        if (nodeListener != null) {
                            vertx.executeBlocking(f -> {
                                if (isActive()) {
                                    switch (event.type()) {
                                        case EVT_NODE_JOINED:
                                            nodeListener.nodeAdded(nodeId(((DiscoveryEvent) event).eventNode()));
                                            break;
                                        case EVT_NODE_LEFT:
                                        case EVT_NODE_FAILED:
                                            String nodeId = nodeId(((DiscoveryEvent) event).eventNode());
                                            nodeListener.nodeLeft(nodeId);
                                            releasePendingLocksForFailedNode(nodeId);
                                            break;
                                    }
                                }
                                fut.complete();
                            }, null);
                        }

                        return true;
                    };

                    ignite.events().localListen(eventListener, EVT_NODE_JOINED, EVT_NODE_LEFT, EVT_NODE_FAILED);

                    fut.complete();
                }
            }, handler);
        }
    }

    /**
     * @param nodeId ID of node that left topology
     */
    private void releasePendingLocksForFailedNode(final String nodeId) {
        Set<String> processed = new HashSet<>();

        pendingLocks.forEach(name -> {
            if (processed.add(name)) {
                IgniteQueue<String> queue = getQueue(name, false);

                if (queue != null && nodeId.equals(queue.peek())) {
                    queue.remove(nodeId);
                }
            }
        });
    }

    @Override
    public void leave(final Handler<AsyncResult<Void>> handler) {
        synchronized (monitor) {
            vertx.executeBlocking(fut -> {
                if (active) {
                    active = false;
                    try {
                        if (!customIgnite) {
                            ignite.close();
                        } else if (eventListener != null) {
                            ignite.events().stopLocalListen(eventListener, EVT_NODE_JOINED, EVT_NODE_LEFT, EVT_NODE_FAILED);
                        }
                    } catch (Exception e) {
                        log.error(e);
                    }
                }

                fut.complete();
            }, handler);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * 加载配置
     *
     * @param config
     * @return
     */
    protected IgniteConfiguration loadConfiguration(final URL config) {
        try {
            IgniteConfiguration cfg = F.first(IgnitionEx.loadConfigurations(config).get1());
            setNodeID(cfg);
            return cfg;
        } catch (IgniteCheckedException e) {
            throw new RuntimeException("Configuration loading error:", e);
        }
    }

    /**
     * 加载配置
     *
     * @return
     */
    protected IgniteConfiguration loadConfiguration() {
        ClassLoader ctxClsLoader = Thread.currentThread().getContextClassLoader();

        InputStream is = null;
        if (ctxClsLoader != null) {
            is = ctxClsLoader.getResourceAsStream(CONFIG_FILE);
        }
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
                log.info("Using default configuration.");
            }
        }
        try {
            IgniteConfiguration cfg = F.first(IgnitionEx.loadConfigurations(is).get1());
            setNodeID(cfg);
            return cfg;
        } catch (IgniteCheckedException e) {
            throw new RuntimeException("Configuration loading error:", e);
        }
    }

    protected void setNodeID(final IgniteConfiguration cfg) {
        UUID uuid = UUID.fromString(nodeID);
        cfg.setNodeId(uuid);
        cfg.setIgniteInstanceName(VERTX_NODE_PREFIX + uuid);
    }

    protected <K, V> IgniteCache<K, V> getCache(final String name) {
        IgniteCache<K, V> cache = ignite.getOrCreateCache(name);
        return cache.withExpiryPolicy(DEFAULT_EXPIRY_POLICY);
    }

    protected <T> IgniteQueue<T> getQueue(final String name, final boolean create) {
        return ignite.queue(name, 1, create ? collectionCfg : null);
    }

    protected static String nodeId(final ClusterNode node) {
        return node.id().toString();
    }

    /**
     * 锁
     */
    protected class LockImpl implements Lock {
        protected final String name;

        public LockImpl(String name) {
            this.name = name;
        }

        @Override
        public void release() {
            vertx.executeBlocking(future -> {
                IgniteQueue<String> queue = getQueue(name, true);
                String ownerId = queue.poll();

                if (ownerId == null) {
                    throw new VertxException("Inconsistent lock state " + name);
                }
                future.complete();
            }, false, null);
        }
    }

    /**
     * 计数器
     */
    protected class CounterImpl implements Counter {
        protected final IgniteAtomicLong cnt;

        public CounterImpl(IgniteAtomicLong cnt) {
            this.cnt = cnt;
        }

        @Override
        public void get(final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.get()), handler);
        }

        @Override
        public void incrementAndGet(final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.incrementAndGet()), handler);
        }

        @Override
        public void getAndIncrement(final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.getAndIncrement()), handler);
        }

        @Override
        public void decrementAndGet(final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.decrementAndGet()), handler);
        }

        @Override
        public void addAndGet(final long value, final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.addAndGet(value)), handler);
        }

        @Override
        public void getAndAdd(final long value, final Handler<AsyncResult<Long>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.getAndAdd(value)), handler);
        }

        @Override
        public void compareAndSet(final long expected, final long value, final Handler<AsyncResult<Boolean>> handler) {
            Objects.requireNonNull(handler, "handler");
            vertx.executeBlocking(fut -> fut.complete(cnt.compareAndSet(expected, value)), handler);
        }
    }

    protected static class ClearExpiryPolicy implements ExpiryPolicy, Serializable {
        @Override
        public Duration getExpiryForCreation() {
            return ETERNAL;
        }

        @Override
        public Duration getExpiryForAccess() {
            return ETERNAL;
        }

        @Override
        public Duration getExpiryForUpdate() {
            return ETERNAL;
        }
    }
}
