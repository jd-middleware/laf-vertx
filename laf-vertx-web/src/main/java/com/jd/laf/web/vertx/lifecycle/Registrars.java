package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.Vertx;

import java.util.*;

/**
 * 注册器插件管理器
 */
public abstract class Registrars {

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
     * @param environment 环境上下文
     * @throws Exception
     */
    public static void register(final Environment environment) throws Exception {
        for (Registrar plugin : getPlugins()) {
            plugin.register(environment);
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
