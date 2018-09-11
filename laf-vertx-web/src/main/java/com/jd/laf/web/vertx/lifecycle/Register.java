package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.SystemContext;
import io.vertx.core.Vertx;

/**
 * 注册器
 */
public interface Register {

    /**
     * 初始化
     *
     * @param vertx       vertx对象
     * @param context     上下文
     * @param initializer 初始化，绑定并且验证
     * @throws Exception
     */
    void register(Vertx vertx, SystemContext context, Initializer initializer) throws Exception;

    /**
     * 注销
     *
     * @param vertx vertx对象
     */
    default void deregister(Vertx vertx) {
    }

    /**
     * 顺序，值越小优先执行
     *
     * @return
     */
    default int order() {
        return 0;
    }


}
