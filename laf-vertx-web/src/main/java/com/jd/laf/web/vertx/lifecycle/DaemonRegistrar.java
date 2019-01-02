package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.service.Daemon;
import io.vertx.core.Vertx;

import static com.jd.laf.web.vertx.Plugin.DAEMON;

/**
 * 服务注册器
 */
public class DaemonRegistrar implements Registrar {

    public static final int DAEMON_ORDER = CodecRegistrar.CODEC_ORDER - 1;

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        //加载扩展点
        for (Daemon daemon : DAEMON.extensions()) {
            EnvironmentAware.setup(vertx, environment, daemon).start(environment);
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
