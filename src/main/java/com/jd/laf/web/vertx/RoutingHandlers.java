package com.jd.laf.web.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 路由处理插件管理器
 */
public abstract class RoutingHandlers {

    //类对应的绑定器
    protected static volatile Map<String, RoutingHandler> plugins;

    /**
     * 获取绑定器
     *
     * @param name 类型
     * @return
     */
    public static RoutingHandler getPlugin(final String name) {
        if (name == null) {
            return null;
        }
        //获取适合的插件
        return getPlugins().get(name);
    }

    /**
     * 构建插件
     *
     * @return
     */
    protected static Map<String, RoutingHandler> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (RoutingHandlers.class) {
                if (plugins == null) {
                    Map<String, RoutingHandler> result = new HashMap();
                    //加载插件
                    ServiceLoader<RoutingHandler> loader = ServiceLoader.load(RoutingHandler.class, RoutingHandlers.class.getClassLoader());
                    loader.forEach(o -> result.put(o.type(), o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

    /**
     * 构建上下文
     *
     * @param context
     */
    public static void setup(final Context context) {
        if (context == null) {
            return;
        }

        RoutingHandler handler;
        for (Map.Entry<String, RoutingHandler> entry : getPlugins().entrySet()) {
            handler = entry.getValue();
            if (handler instanceof ContextAware) {
                ((ContextAware) handler).setup(context);
            }
        }
    }

}
