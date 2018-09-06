package com.jd.laf.web.vertx.response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 异常转换插件管理器
 */
public abstract class ErrorSuppliers {

    //类对应的异常转换器
    protected static ConcurrentMap<Class, Optional<ErrorSupplier>> suppliers = new ConcurrentHashMap<>();
    //异常转换插件
    protected static volatile List<ErrorSupplier> plugins;

    /**
     * 获取绑定器
     *
     * @param type 类型
     * @return
     */
    public static ErrorSupplier getPlugin(final Class<?> type) {
        if (type == null) {
            return null;
        }
        Optional<ErrorSupplier> optional = suppliers.get(type);
        if (optional != null) {
            return optional.get();
        } else {
            List<ErrorSupplier> plugins = getPlugins();
            for (ErrorSupplier plugin : plugins) {
                if (plugin.type().isAssignableFrom(type)) {
                    optional = Optional.of(plugin);
                    Optional<ErrorSupplier> exists = suppliers.putIfAbsent(type, optional);
                    if (exists != null) {
                        optional = exists;
                    }
                    break;
                }
            }
        }
        //获取适合的插件
        return optional == null ? null : optional.get();
    }

    /**
     * 构建插件
     *
     * @return
     */
    protected static List<ErrorSupplier> getPlugins() {
        if (plugins == null) {
            //加载插件
            synchronized (ErrorSuppliers.class) {
                if (plugins == null) {
                    List<ErrorSupplier> result = new ArrayList<>();
                    //加载插件
                    ServiceLoader<ErrorSupplier> loader = ServiceLoader.load(ErrorSupplier.class, ErrorSuppliers.class.getClassLoader());
                    loader.forEach(o -> result.add(o));
                    Collections.sort(result, new ClassComparator());
                    plugins = result;
                }
            }
        }
        return plugins;
    }

    /**
     * 类型比较器
     */
    protected static class ClassComparator implements Comparator<ErrorSupplier> {
        @Override
        public int compare(final ErrorSupplier o1, final ErrorSupplier o2) {
            if (o1.type() == o2.type()) {
                return 0;
            } else if (o1.type().isAssignableFrom(o2.type())) {
                return 1;
            } else if (o2.type().isAssignableFrom(o1.type())) {
                return -1;
            }
            return 0;
        }
    }
}
