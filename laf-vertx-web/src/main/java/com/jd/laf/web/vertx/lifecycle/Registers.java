package com.jd.laf.web.vertx.lifecycle;

import com.jd.laf.web.vertx.SystemContext;
import io.vertx.core.Vertx;

import java.util.*;

/**
 * 注册器插件管理器
 */
public abstract class Registers {

    //类对应的绑定器
    protected static volatile List<Register> plugins;

    /**
     * 构建插件
     *
     * @return
     */
    protected static List<Register> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (Registers.class) {
                if (plugins == null) {
                    List<Register> result = new ArrayList();
                    //加载插件
                    ServiceLoader<Register> loader = ServiceLoader.load(Register.class, Registers.class.getClassLoader());
                    loader.forEach(o -> result.add(o));
                    //按照优先级排序
                    Collections.sort(result, Comparator.comparingInt(Register::order));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

    /**
     * 初始化
     *
     * @param vertx       vertx对象
     * @param context     上下文
     * @param initializer 消费者，绑定并且验证
     * @throws Exception
     */
    public static void register(final Vertx vertx, final SystemContext context, final Initializer initializer) throws Exception {
        for (Register plugin : getPlugins()) {
            plugin.register(vertx, context, initializer);
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
