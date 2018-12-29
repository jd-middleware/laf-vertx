package com.jd.laf.ignite.spring;

import org.apache.ignite.binary.BinarySerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 二进制序列化器
 */
public interface BinaryMarshaller extends BinarySerializer {

    String name();


    /**
     * 路由处理插件管理器
     */
    abstract class Plugin {

        //类对应的绑定器
        protected static volatile List<BinaryMarshaller> plugins;

        /**
         * 构建插件
         *
         * @return
         */
        public static List<BinaryMarshaller> getPlugins() {
            if (plugins == null) {
                //加载插件
                synchronized (Plugin.class) {
                    if (plugins == null) {
                        List<BinaryMarshaller> result = new ArrayList<>();
                        //加载插件
                        ServiceLoader<BinaryMarshaller> loader = ServiceLoader.load(BinaryMarshaller.class);
                        loader.forEach(o -> result.add(o));
                        plugins = result;
                    }
                }
            }
            return plugins;
        }

    }

}
