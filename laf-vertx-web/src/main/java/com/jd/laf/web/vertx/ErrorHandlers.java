package com.jd.laf.web.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 异常处理插件管理器
 */
public abstract class ErrorHandlers {

    //类对应的绑定器
    protected static volatile Map<String, ErrorHandler> plugins;

    /**
     * 获取绑定器
     *
     * @param name 类型
     * @return
     */
    public static ErrorHandler getPlugin(final String name) {
        if (name == null) {
            return null;
        }
        if (plugins == null) {
            //加载插件
            synchronized (ErrorHandlers.class) {
                if (plugins == null) {
                    Map<String, ErrorHandler> result = new HashMap<String, ErrorHandler>();
                    //加载插件
                    ServiceLoader<ErrorHandler> loader = ServiceLoader.load(ErrorHandler.class, ErrorHandlers.class.getClassLoader());
                    loader.forEach(o -> result.put(o.type(), o));
                    plugins = result;
                }
            }
        }

        //获取适合的插件
        return plugins.get(name);
    }

}
