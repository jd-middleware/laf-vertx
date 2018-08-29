package com.jd.laf.web.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 模板引擎提供者插件管理器
 */
public abstract class TemplateProviders {

    //类对应的绑定器
    protected static volatile Map<String, TemplateProvider> plugins;

    /**
     * 获取模板引擎提供者
     *
     * @param name 类型
     * @return
     */
    public static TemplateProvider getPlugin(final String name) {
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
    protected static Map<String, TemplateProvider> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (TemplateProviders.class) {
                if (plugins == null) {
                    Map<String, TemplateProvider> result = new HashMap<String, TemplateProvider>();
                    //加载插件
                    ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, TemplateProviders.class.getClassLoader());
                    loader.forEach(o -> result.put(o.type(), o));
                    plugins = result;
                }
            }
        }
        return plugins;
    }
}
