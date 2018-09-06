package com.jd.laf.web.vertx.message;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 消息编解码插件管理器
 */
public abstract class CustomCodecs {

    //类对应的绑定器
    protected static volatile List<CustomCodec> plugins;

    /**
     * 获取插件
     *
     * @return
     */
    public static List<CustomCodec> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (CustomCodecs.class) {
                if (plugins == null) {
                    List<CustomCodec> result = new ArrayList<>();
                    //加载插件
                    ServiceLoader<CustomCodec> loader = ServiceLoader.load(CustomCodec.class, CustomCodecs.class.getClassLoader());
                    loader.forEach(o -> result.add(o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }

}
