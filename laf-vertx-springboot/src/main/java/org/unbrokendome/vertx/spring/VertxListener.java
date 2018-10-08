package org.unbrokendome.vertx.spring;

import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * 监听器
 */
public interface VertxListener {

    /**
     * 启动
     *
     * @param vertx
     * @param options
     */
    default void vertxStarted(Vertx vertx, VertxOptions options) {
    }

    /**
     * 停止
     *
     * @param vertx
     */
    default void vertxStopped(Vertx vertx) {
    }

    /**
     * 部署
     *
     * @param verticle
     * @param context
     */
    default void verticleDeployed(Verticle verticle, Context context) {
    }

    /**
     * 下线
     *
     * @param verticle
     * @param context
     */
    default void verticleUndeployed(Verticle verticle, Context context) {
    }
}
