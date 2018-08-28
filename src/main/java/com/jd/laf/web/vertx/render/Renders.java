package com.jd.laf.web.vertx.render;

import com.jd.laf.web.vertx.Context;
import com.jd.laf.web.vertx.ContextAware;

import java.util.*;

/**
 * 渲染器插件管理器
 */
public abstract class Renders {

    //类对应的绑定器
    protected static volatile Map<String, Render> plugins;

    /**
     * 获取绑定器
     *
     * @param name 类型
     * @return
     */
    public static Render getPlugin(final String name) {
        if (name == null) {
            return null;
        }
        return getPlugins().get(name);
    }

    /**
     * 获取插件散列
     *
     * @return
     */
    protected static Map<String, Render> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (Renders.class) {
                if (plugins == null) {
                    Map<String, Render> result = new HashMap<String, Render>();
                    List<Render> renders = new ArrayList<>();
                    //加载插件
                    ServiceLoader<Render> loader = ServiceLoader.load(Render.class, Renders.class.getClassLoader());
                    loader.forEach(o -> renders.add(o));
                    //排序
                    Collections.sort(renders, Comparator.comparing(Render::order));
                    renders.forEach(o -> result.put(o.type().toLowerCase(), o));
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

        Render render;
        for (Map.Entry<String, Render> entry : getPlugins().entrySet()) {
            render = entry.getValue();
            if (render instanceof ContextAware) {
                ((ContextAware) render).setup(context);
            }
        }
    }

}
