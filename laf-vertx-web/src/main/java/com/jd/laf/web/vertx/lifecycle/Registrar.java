package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.extension.Ordered;
import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.config.VertxConfig;
import io.vertx.core.Vertx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 插件声明周期管理，进行初始化和销毁，单个JVM只会调用一次
 */
public interface Registrar extends Ordered {

    int DAEMON_ORDER = ORDER - 2;
    int MESSAGE_ORDER = ORDER - 1;
    int ROUTING_ORDER = ORDER;
    /**
     * 渲染注册顺序，其用到的渲染引擎在路由里面初始化，需要在路由之后
     */
    int RENDER_ORDER = ORDER + 1;
    /**
     * 异常注册顺序，用到了渲染，需要在渲染之后
     */
    int ERROR_ORDER = ORDER + 2;

    /**
     * 计数器
     */
    AtomicLong counter = new AtomicLong(0);

    /**
     * 等到初始化完成
     */
    CountDownLatch latch = new CountDownLatch(1);


    /**
     * 初始化
     *
     * @param vertx
     * @param environment 环境上下文
     * @param config      路由配置
     * @throws Exception
     */
    void register(Vertx vertx, Environment environment, VertxConfig config) throws Exception;

    /**
     * 注销
     *
     * @param vertx vertx对象
     */
    default void deregister(final Vertx vertx) {
    }

    /**
     * 优先级升序排序
     *
     * @return
     */
    default int order() {
        return ORDER;
    }


}
