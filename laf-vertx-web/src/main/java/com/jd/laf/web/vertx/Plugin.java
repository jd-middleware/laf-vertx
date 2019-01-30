package com.jd.laf.web.vertx;

import com.jd.laf.extension.ExtensionMeta;
import com.jd.laf.extension.ExtensionPoint;
import com.jd.laf.extension.ExtensionPointLazy;
import com.jd.laf.extension.ExtensionSelector;
import com.jd.laf.extension.Selector.CacheSelector;
import com.jd.laf.extension.Selector.MatchSelector;
import com.jd.laf.web.vertx.lifecycle.Registrar;
import com.jd.laf.web.vertx.message.CustomCodec;
import com.jd.laf.web.vertx.pool.PoolFactory;
import com.jd.laf.web.vertx.render.Render;
import com.jd.laf.web.vertx.response.ErrorSupplier;
import com.jd.laf.web.vertx.service.Daemon;

import java.util.Comparator;

public interface Plugin {

    //消息插件
    ExtensionPoint<MessageHandler, String> MESSAGE = new ExtensionPointLazy(MessageHandler.class);
    //异常处理插件
    ExtensionPoint<ErrorHandler, String> ERROR = new ExtensionPointLazy(ErrorHandler.class);
    //路由插件
    ExtensionPoint<RoutingHandler, String> ROUTING = new ExtensionPointLazy(RoutingHandler.class);
    //命令插件
    ExtensionPoint<Command, String> COMMAND = new ExtensionPointLazy(Command.class);
    //注册器插件
    ExtensionPoint<Registrar, String> REGISTRAR = new ExtensionPointLazy(Registrar.class);
    //渲染插件
    ExtensionPoint<Render, String> RENDER = new ExtensionPointLazy(Render.class);
    //对象工程插件
    ExtensionPoint<PoolFactory, String> POOL = new ExtensionPointLazy(PoolFactory.class);
    //模板插件
    ExtensionPoint<TemplateProvider, String> TEMPLATE = new ExtensionPointLazy(TemplateProvider.class);
    //守护服务插件
    ExtensionPoint<Daemon, String> DAEMON = new ExtensionPointLazy(Daemon.class);
    //编解码插件
    ExtensionPoint<CustomCodec, String> CODEC = new ExtensionPointLazy(CustomCodec.class);
    //连接处理器插件
    ExtensionPoint<ConnectionHandler, String> CONNECTION = new ExtensionPointLazy(ConnectionHandler.class);
    //连接异常处理器插件
    ExtensionPoint<ExceptionHandler, String> EXCEPTION = new ExtensionPointLazy(ExceptionHandler.class);
    //异常处理插件
    ExtensionSelector<ErrorSupplier, Class<?>, Class<?>, ErrorSupplier> THROWABLE = new ExtensionSelector<>(
            new ExtensionPointLazy(ErrorSupplier.class, (Comparator<ExtensionMeta<ErrorSupplier, Class<?>>>) (o1, o2) -> {
                ErrorSupplier e1 = o1.getTarget();
                ErrorSupplier e2 = o2.getTarget();
                if (e1.type() == e2.type()) {
                    return 0;
                } else if (e1.type().isAssignableFrom(e2.type())) {
                    return 1;
                } else if (e2.type().isAssignableFrom(e1.type())) {
                    return -1;
                }
                return 0;
            }),
            new CacheSelector<>(new MatchSelector<ErrorSupplier, Class<?>, Class<?>>() {

                @Override
                protected boolean match(final ErrorSupplier target, final Class<?> condition) {
                    return target.type().isAssignableFrom(condition);
                }
            })
    );
}
