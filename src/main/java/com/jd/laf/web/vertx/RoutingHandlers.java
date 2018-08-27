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
        if (plugins == null) {
            //加载插件
            synchronized (RoutingHandlers.class) {
                if (plugins == null) {
                    Map<String, RoutingHandler> result = new HashMap<String, RoutingHandler>();
                    //加载插件
                    ServiceLoader<RoutingHandler> loader = ServiceLoader.load(RoutingHandler.class, RoutingHandlers.class.getClassLoader());
                    for (RoutingHandler handler : loader) {
                        result.put(handler.type(), handler);
                    }
                    plugins = result;
                }
            }
        }

        //获取适合的插件
        return plugins.get(name);
    }

}
