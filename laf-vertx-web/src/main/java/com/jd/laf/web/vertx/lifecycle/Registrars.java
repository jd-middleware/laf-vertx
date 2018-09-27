package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.Vertx;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 注册器插件管理器
 */
public abstract class Registrars {

    protected static Logger logger = Logger.getLogger(Registrars.class.getName());

    //类对应的绑定器
    protected static volatile List<Registrar> plugins;

    /**
     * 构建插件
     *
     * @return
     */
    protected static List<Registrar> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (Registrars.class) {
                if (plugins == null) {
                    List<Registrar> result = new ArrayList();
                    //加载插件
                    ServiceLoader<Registrar> loader = ServiceLoader.load(Registrar.class, Registrars.class.getClassLoader());
                    loader.forEach(o -> result.add(o));
                    //按照优先级排序
                    Collections.sort(result, Comparator.comparingInt(Registrar::order).reversed());
                    plugins = result;
                }
            }
        }
        return plugins;
    }

    /**
     * 初始化
     *
     * @param vertx
     * @param environment 环境上下文
     * @throws Exception
     */
    public static void register(final Vertx vertx, final Environment environment) throws Exception {
        for (Registrar plugin : getPlugins()) {
            try {
                plugin.register(vertx, environment);
            } catch (Exception e) {
                logger.log(Level.SEVERE, String.format("register plugin %s error ", plugin.getClass()), e);
            }
        }
    }

    /**
     * 注销
     *
     * @param vertx vertx对象
     */
    public static void deregister(final Vertx vertx) {
        getPlugins().forEach(o -> o.deregister(vertx));
    }

}
