package org.unbrokendome.vertx.spring;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * 在Spring的生命周期启动Vertx
 */
public class SpringVertx implements SmartLifecycle, BeanFactoryAware {

    public static final String DEFAULT_VERTICLE_FACTORY_PREFIX = "spring";

    private final Logger logger = LoggerFactory.getLogger(SpringVertx.class);

    protected final VertxFactory factory;
    protected final VertxOptions options;
    protected final List<VerticleRegistration> verticleRegistrations;
    protected final String verticleFactoryPrefix;
    protected final int startupPhase;
    protected final boolean autoStartup;

    protected final Object startMonitor = new Object();
    protected BeanFactory beanFactory;

    protected volatile Vertx vertx;

    public SpringVertx(VertxFactory factory, VertxOptions options,
                       Collection<VerticleRegistration> verticleRegistrations,
                       String verticleFactoryPrefix,
                       int startupPhase, boolean autoStartup) {
        this.factory = factory;
        this.options = new VertxOptions(options);
        this.verticleRegistrations = new ArrayList<>(verticleRegistrations);
        this.verticleFactoryPrefix = verticleFactoryPrefix;
        this.startupPhase = startupPhase;
        this.autoStartup = autoStartup;
    }

    @Override
    public final boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public final int getPhase() {
        return startupPhase;
    }

    @Override
    public final boolean isRunning() {
        return vertx != null;
    }

    @Override
    public synchronized void start() {
        if (vertx == null) {
            synchronized (startMonitor) {
                if (vertx == null) {
                    doStart();
                }
            }
        }
    }

    protected void doStart() {
        CompletableFuture<Vertx> vertxStartedFuture = new CompletableFuture<>();
        CompletableFuture<Vertx> vertxReadyFuture = vertxStartedFuture;

        //创建vertx
        if (options.isClustered()) {
            factory.clusteredVertx(options, ar -> {
                if (ar.succeeded()) {
                    vertxStartedFuture.complete(ar.result());
                } else {
                    vertxStartedFuture.completeExceptionally(ar.cause());
                }
            });
        } else {
            Vertx vertx = factory.vertx(options);
            vertxStartedFuture.complete(vertx);
        }

        //注册Spring的执行器工厂
        if (verticleFactoryPrefix != null) {
            vertxReadyFuture = vertxReadyFuture.thenApply(vertx -> {
                //基于Spring中的Bean的执行器工厂类，
                SpringVerticleFactory verticleFactory = new SpringVerticleFactory(verticleFactoryPrefix, beanFactory);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Registering VerticleFactory: %s", verticleFactory));
                }
                vertx.registerVerticleFactory(verticleFactory);
                return vertx;
            });
        }

        //按照顺序部署执行器
        if (!verticleRegistrations.isEmpty()) {
            // Group all verticle registrations by order. Verticles with the same order will be
            // deployed simultaneously.
            SortedMap<Integer, List<VerticleRegistration>> registrationGroups = verticleRegistrations.stream()
                    .collect(Collectors.groupingBy(SpringVertx::getVerticleOrder, TreeMap::new, Collectors.toList()));

            for (Map.Entry<Integer, List<VerticleRegistration>> entry : registrationGroups.entrySet()) {
                int order = entry.getKey();
                List<VerticleRegistration> registrations = entry.getValue();
                vertxReadyFuture = vertxReadyFuture.thenCompose(vertx ->
                        deployVerticleGroup(vertx, order, registrations));
            }
        } else {
            logger.debug("No verticle registrations set; no verticles will be deployed after startup.");
        }

        try {
            this.vertx = vertxReadyFuture.join();
            logger.info("Vert.x startup complete");
        } catch (CompletionException ex) {
            logger.error(ex.getMessage(), ex);
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            } else {
                throw ex;
            }
        }
    }

    /**
     * 获取执行器注册顺序
     *
     * @param registration
     * @return
     */
    protected static int getVerticleOrder(final VerticleRegistration registration) {
        if (registration instanceof Ordered) {
            return ((Ordered) registration).getOrder();
        } else {
            return 0;
        }
    }

    /**
     * 部署执行器
     *
     * @param vertx         vertx对象
     * @param order         顺序
     * @param registrations 该顺序下的执行器注册信息集合
     * @return
     */
    protected CompletableFuture<Vertx> deployVerticleGroup(final Vertx vertx, final int order,
                                                           final Collection<VerticleRegistration> registrations) {
        logger.info(String.format("Deploying verticles with order %s", order));

        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[registrations.size()];

        int i = 0;
        for (VerticleRegistration registration : registrations) {
            futures[i++] = deployVerticle(vertx, registration);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(any -> vertx);
    }

    /**
     * 部署单个执行器
     *
     * @param vertx
     * @param registration
     * @return
     */
    protected CompletableFuture<Void> deployVerticle(final Vertx vertx, final VerticleRegistration registration) {

        Supplier<Verticle> supplier = registration.getSupplier();
        String verticleName = registration.getVerticleName();

        if (supplier == null) {
            logger.error(String.format("Invalid VerticleRegistration %s: Either supplier or verticleName must be given", registration));
            return CompletableFuture.completedFuture(null);
        }

        DeploymentOptions deploymentOptions = registration.getDeploymentOptions();
        if (deploymentOptions == null) {
            deploymentOptions = new DeploymentOptions();
        }

        final CompletableFuture<Void> future = new CompletableFuture<>();
        Handler<AsyncResult<String>> resultHandler = ar -> {
            if (ar.succeeded()) {
                logger.info(String.format("Successfully deployed verticle %s with deployment ID %s", registration, ar.result()));
                future.complete(null);
            } else {
                logger.error(String.format("Failed to deploy verticle %s", registration), ar.cause());
                future.completeExceptionally(ar.cause());
            }
        };
        if (supplier != null) {
            //根据提供者部署
            vertx.deployVerticle(supplier, deploymentOptions, resultHandler);
        } else {
            //根据名称部署
            vertx.deployVerticle(verticleFactoryPrefix + ":" + verticleName, deploymentOptions, resultHandler);
        }
        return future;
    }


    @Override
    public void stop(final Runnable callback) {
        Vertx vertx = this.vertx;
        if (vertx != null) {
            logger.debug("Shutting down Vert.x instance");
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    logger.info("Vert.x instance shut down successfully");
                } else {
                    logger.error("Failed to shut down Vert.x instance", ar.cause());
                }
                this.vertx = null;
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }


    @Override
    public void stop() {
        if (vertx != null) {
            CountDownLatch latch = new CountDownLatch(1);
            stop(latch::countDown);
            try {
                latch.await();
            } catch (InterruptedException ex) {
                logger.error("Interrupted while waiting for Vert.x instance to stop", ex);
            }
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 构造器
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected VertxFactory factory = Vertx.factory;
        protected VertxOptions options = null;
        protected List<VerticleRegistration> verticleRegistrations = new ArrayList<>();
        protected List<VertxConfigurer> configurers = new ArrayList<>();
        protected String verticleFactoryPrefix = DEFAULT_VERTICLE_FACTORY_PREFIX;
        protected int startupPhase = 0;
        protected boolean autoStartup = true;

        public Builder factory(final VertxFactory factory) {
            if (factory != null) {
                this.factory = factory;
            }
            return this;
        }

        public Builder options(final Consumer<VertxOptions> consumer) {
            if (this.options == null) {
                this.options = new VertxOptions();
            }
            if (consumer != null) {
                consumer.accept(options);
            }
            return this;
        }

        public Builder options(final VertxOptions options) {
            if (options != null) {
                this.options = new VertxOptions(options);
            }
            return this;
        }

        public Builder clusterManager(final ClusterManager clusterManager) {
            return options(opt -> {
                if (opt.getClusterManager() == null) {
                    opt.setClusterManager(clusterManager);
                }
            });
        }

        public Builder verticleFactoryPrefix(final String prefix) {
            this.verticleFactoryPrefix = prefix;
            return this;
        }

        public Builder verticle(final Verticle verticle) {
            if (verticle != null) {
                return verticle(new VerticleRegistrationBean(verticle));
            }
            return this;
        }

        public Builder verticle(final Verticle verticle, final DeploymentOptions options) {
            if (verticle != null) {
                return verticle(new VerticleRegistrationBean(verticle, options));
            }
            return this;
        }

        public Builder verticle(final VerticleRegistration verticleRegistration) {
            if (verticleRegistration != null) {
                this.verticleRegistrations.add(verticleRegistration);
            }
            return this;
        }

        public Builder verticles(final Collection<? extends Verticle> verticles) {
            if (verticles != null) {
                for (Verticle verticle : verticles) {
                    this.verticle(verticle);
                }
            }
            return this;
        }

        public Builder verticles(final Verticle... verticles) {
            if (verticles != null) {
                return verticles(Arrays.asList(verticles));
            }
            return this;
        }

        public Builder verticleRegistrations(final Iterable<? extends VerticleRegistration> verticleRegistrations) {
            if (verticleRegistrations != null) {
                for (VerticleRegistration registration : verticleRegistrations) {
                    this.verticle(registration);
                }
            }
            return this;
        }

        public Builder verticleRegistrations(final VerticleRegistration... verticleRegistrations) {
            if (verticleRegistrations != null) {
                return verticleRegistrations(Arrays.asList(verticleRegistrations));
            }
            return this;
        }

        public Builder configurer(VertxConfigurer configurer) {
            if (configurer != null) {
                configurers.add(configurer);
            }
            return this;
        }

        public Builder configurer(Collection<VertxConfigurer> configurers) {
            if (configurers != null) {
                for (VertxConfigurer configurer : configurers) {
                    if (configurer != null) {
                        this.configurers.add(configurer);
                    }
                }
            }
            return this;
        }

        public Builder configure() {
            if (!configurers.isEmpty()) {
                AnnotationAwareOrderComparator.sort(configurers);
                for (VertxConfigurer configurer : configurers) {
                    configurer.configure(this);
                }
            }
            return this;
        }

        public Builder startupPhase(final int startupPhase) {
            this.startupPhase = startupPhase;
            return this;
        }

        public Builder autoStartup(final boolean autoStartup) {
            this.autoStartup = autoStartup;
            return this;
        }

        protected VertxOptions getOrCreateOptions() {
            if (options == null) {
                options = new VertxOptions();

            }
            if (options.getClusterHost() == null || EventBusOptions.DEFAULT_HOST.equals(options.getClusterHost())) {
                //localhost
                String localHost = NetUtils.getLocalHost();
                if (localHost != null) {
                    options.setClusterHost(localHost);
                }
            }
            return options;
        }

        public SpringVertx build() {

            VertxOptions options = getOrCreateOptions();
            return new SpringVertx(
                    factory,
                    options,
                    verticleRegistrations,
                    verticleFactoryPrefix,
                    startupPhase,
                    autoStartup);
        }
    }
}
