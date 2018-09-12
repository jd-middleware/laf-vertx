package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.service.Daemon;
import com.jd.laf.web.vertx.service.Daemons;
import io.vertx.core.Vertx;

/**
 * 服务注册器
 */
public class DaemonRegistrar implements Registrar {

    @Override
    public void register(final Vertx vertx, final Environment environment) throws Exception {
        for (Daemon plugin : Daemons.getPlugins()) {
            environment.setup(plugin).start(environment);
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
        Daemons.getPlugins().forEach(Daemon::stop);
    }

    @Override
    public int order() {
        return HANDLER + 2;
    }
}
