package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.SystemContext;
import com.jd.laf.web.vertx.service.Daemon;
import com.jd.laf.web.vertx.service.Daemons;
import io.vertx.core.Vertx;

/**
 * 服务注册器
 */
public class DaemonRegister implements Register {

    @Override
    public void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        for (Daemon plugin : Daemons.getPlugins()) {
            initializer.accept(plugin);
            plugin.start(context);
        }
    }

    @Override
    public void deregister(final Vertx vertx) {
        Daemons.getPlugins().forEach(Daemon::stop);
    }

    @Override
    public int order() {
        return 1;
    }
}
