package com.jd.laf.web.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 消息处理插件管理器
 */
public abstract class MessageHandlers {

    //类对应的绑定器
    protected static volatile Map<String, MessageHandler> plugins;

    /**
     * 获取绑定器
     *
     * @param name 类型
     * @return
     */
    public static MessageHandler getPlugin(final String name) {
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
    public static Map<String, MessageHandler> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (MessageHandlers.class) {
                if (plugins == null) {
                    Map<String, MessageHandler> result = new HashMap<String, MessageHandler>();
                    //加载插件
                    ServiceLoader<MessageHandler> loader = ServiceLoader.load(MessageHandler.class, MessageHandlers.class.getClassLoader());
                    loader.forEach(o -> result.put(o.type(), o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

}
