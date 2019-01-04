package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.config.VertxConfig;
import com.jd.laf.web.vertx.service.Daemon;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.DAEMON;

/**
 * 服务注册器
 */
public class DaemonRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment, final VertxConfig config) throws Exception {
        //初始化扩展点
        EnvironmentAware.setup(vertx, environment, DAEMON.extensions());
        //启动
        for (Daemon daemon : DAEMON.extensions()) {
            daemon.start(environment);
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
        DAEMON.extensions().forEach(Daemon::stop);
    }

    @Override
    public int order() {
        //在CodecRegistrar之前执行
        return DAEMON_ORDER;
    }
}
