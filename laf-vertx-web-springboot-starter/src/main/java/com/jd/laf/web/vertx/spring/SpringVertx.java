package com.jd.laf.web.vertx.spring;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.core.spi.VertxFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;


/**
 * 在Spring的生命周期启动Vertx
 */
public class SpringVertx implements SmartLifecycle, BeanFactoryAware {

    private final Logger logger = LoggerFactory.getLogger(SpringVertx.class);

    protected final VertxFactory vertxFactory;
    protected final String factoryPrefix;
    protected final VertxOptions options;
    protected final List<VerticleProvider> providers;
    protected final int startupPhase;
    protected final boolean autoStartup;
    protected BeanFactory beanFactory;

    protected volatile Vertx vertx;

    public SpringVertx(VertxFactory vertxFactory, VertxOptions options, List<VerticleProvider> providers,
                       String factoryPrefix, int startupPhase, boolean autoStartup) {
        this.vertxFactory = vertxFactory;
        this.options = options;
        this.providers = providers;
        this.factoryPrefix = factoryPrefix;
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
        //在bean初始化完成之后开始调用
        CompletableFuture<Vertx> startedFuture = new CompletableFuture<>();
        CompletableFuture<Vertx> readyFuture = startedFuture;

        //创建vertx
        startVertx(startedFuture);

        //注册Spring工厂
        if (factoryPrefix != null && !factoryPrefix.isEmpty()) {
            readyFuture = readyFuture.thenApply(vertx -> {
                //基于Spring中的Bean的执行器工厂类，
                vertx.registerVerticleFactory(new VerticleFactory() {
                    @Override
                    public String prefix() {
                        return factoryPrefix;
                    }

                    @Override
                    public Verticle createVerticle(final String verticleName, final ClassLoader classLoader) throws Exception {
                        return beanFactory.getBean(verticleName, Verticle.class);
                    }
                });
                return vertx;
            });
        }

        //按照顺序部署执行器
        if (providers != null && !providers.isEmpty()) {
            Collections.sort(providers, Comparator.comparingInt(Ordered::getOrder));
            for (VerticleProvider provider : providers) {
                readyFuture.thenCompose(vertx -> deployVerticle(vertx, provider.getVerticleMeta()));
            }
        } else {
            logger.info("there is not any verticle to be deployed after startup.");
        }

        try {
            this.vertx = readyFuture.join();
            logger.info("success starting Vert.x");
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
     * 启动vertx
     *
     * @param startedFuture
     */
    protected void startVertx(final CompletableFuture<Vertx> startedFuture) {
        if (options.isClustered()) {
            vertxFactory.clusteredVertx(options, r -> {
                if (r.succeeded()) {
                    startedFuture.complete(r.result());
                } else {
                    startedFuture.completeExceptionally(r.cause());
                }
            });
        } else {
            Vertx vertx = vertxFactory.vertx(options);
            startedFuture.complete(vertx);
        }
    }

    /**
     * 部署单个执行器
     *
     * @param vertx
     * @param meta
     * @return
     */
    protected CompletableFuture<Vertx> deployVerticle(final Vertx vertx, final VerticleMeta meta) {

        Supplier<Verticle> supplier = meta.getSupplier();
        String name = meta.getName();
        if (supplier == null && (name == null || name.isEmpty())) {
            return CompletableFuture.completedFuture(vertx);
        }

        final DeploymentOptions options = meta.getDeploymentOptions() == null ? new DeploymentOptions() : meta.getDeploymentOptions();

        final CompletableFuture<Vertx> future = new CompletableFuture<>();
        final Handler<AsyncResult<String>> handler = r -> {
            if (r.succeeded()) {
                logger.info(String.format("success deploying verticle %s with deployment id %s", meta, r.result()));
                future.complete(vertx);
            } else {
                logger.error(String.format("failed deploying verticle %s", meta), r.cause());
                future.completeExceptionally(r.cause());
            }
        };
        //根据提供者部署
        if (supplier != null) {
            vertx.deployVerticle(supplier, options, handler);
        } else {
            //根据名称部署
            String beanName = name.startsWith(factoryPrefix + ":") ? name.substring(factoryPrefix.length() + 1) : name;
            if (!beanFactory.containsBean(beanName)) {
                future.completeExceptionally(new IllegalArgumentException("No such bean: " + beanName));
            } else if (!beanFactory.isTypeMatch(beanName, Verticle.class)) {
                future.completeExceptionally(new IllegalArgumentException("Bean \"" + beanName + "\" is not of type Verticle"));
            } else {
                if (beanFactory.isSingleton(beanName)) {
                    options.setInstances(1);
                }
                vertx.deployVerticle(beanName, options, handler);
            }
        }
        return future;
    }


    @Override
    public synchronized void stop(final Runnable callback) {
        Vertx vertx = this.vertx;
        if (vertx != null) {
            logger.info("stopping Vert.x instance");
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    logger.info("success stopping Vert.x instance");
                } else {
                    logger.error("failed stopping Vert.x instance", ar.cause());
                }
                this.vertx = null;
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }


    @Override
    public synchronized void stop() {
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

}
