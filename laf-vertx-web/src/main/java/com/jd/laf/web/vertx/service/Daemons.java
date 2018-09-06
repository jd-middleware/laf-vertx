package com.jd.laf.web.vertx.service;

import com.jd.laf.web.vertx.SystemContext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 服务插件管理器
 */
public abstract class Daemons {

    //类对应的绑定器
    protected static volatile List<Daemon> plugins;

    /**
     * 获取插件
     *
     * @return
     */
    public static List<Daemon> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (Daemons.class) {
                if (plugins == null) {
                    List<Daemon> result = new ArrayList<>();
                    //加载插件
                    ServiceLoader<Daemon> loader = ServiceLoader.load(Daemon.class, Daemons.class.getClassLoader());
                    loader.forEach(o -> result.add(o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

    /**
     * 启动Daemon
     *
     * @param context
     * @throws Exception
     */
    public static void start(final SystemContext context) throws Exception {
        for (Daemon daemon : getPlugins()) {
            daemon.start(context);
        }
    }

    /**
     * 停止Daemon
     */
    public static void stop() {
        for (Daemon daemon : getPlugins()) {
            daemon.stop();
        }
    }

}
