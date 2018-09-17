package com.jd.laf.web.vertx.pool;

import com.jd.laf.binding.Ordered;
import com.jd.laf.binding.marshaller.JsonProviders;

import java.util.*;

/**
 * 对象池插件管理器
 */
public abstract class PoolFactories {

    //插件集合
    protected static volatile List<PoolFactory> plugins;

    /**
     * 获取Json序列化插件
     *
     * @return
     */
    public static List<PoolFactory> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (JsonProviders.class) {
                if (plugins == null) {
                    List<PoolFactory> result = new ArrayList<PoolFactory>();
                    //加载插件
                    ServiceLoader<PoolFactory> loader = ServiceLoader.load(PoolFactory.class, JsonProviders.class.getClassLoader());
                    for (PoolFactory provider : loader) {
                        result.add(provider);
                    }
                    //降序排序，序号越大，优先级越高
                    Collections.sort(result, new Comparator<PoolFactory>() {
                        @Override
                        public int compare(PoolFactory o1, PoolFactory o2) {
                            int order1 = o1 != null && o1 instanceof Ordered ? ((Ordered) o1).order() : 0;
                            int order2 = o2 != null && o2 instanceof Ordered ? ((Ordered) o2).order() : 0;
                            return order2 - order1;
                        }
                    });
                    plugins = result;
                }
            }
        }

        //获取适合的插件
        return plugins;
    }

    /**
     * 获取默认对象池插件
     *
     * @return
     */
    public static PoolFactory getPlugin() {
        List<PoolFactory> plugins = getPlugins();
        //获取适合的插件
        return plugins.isEmpty() ? null : plugins.get(0);
    }

}
