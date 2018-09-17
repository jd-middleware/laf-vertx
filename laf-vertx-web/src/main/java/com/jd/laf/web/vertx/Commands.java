package com.jd.laf.web.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 命令插件管理器
 */
public abstract class Commands {

    //类对应的绑定器
    protected static volatile Map<String, Command> plugins;

    /**
     * 获取绑定器
     *
     * @param name 类型
     * @return
     */
    public static Command getPlugin(final String name) {
        if (name == null) {
            return null;
        }
        //获取适合的插件
        return getPlugins().get(name);
    }

    /**
     * 获取插件
     *
     * @return
     */
    public static Map<String, Command> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (Commands.class) {
                if (plugins == null) {
                    Map<String, Command> result = new HashMap<String, Command>();
                    //加载插件
                    ServiceLoader<Command> loader = ServiceLoader.load(Command.class, Commands.class.getClassLoader());
                    loader.forEach(o -> result.put(o.type(), o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

}
