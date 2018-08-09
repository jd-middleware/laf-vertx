package com.jd.laf.web.vertx;

import com.jd.laf.extension.ExtensionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {

    //配置
    private VertxConfig config;
    //插件管理器
    private ExtensionManager extensionManager = ExtensionManager.getInstance();
    //参数
    private Map<String, Object> parameters;
    //资源文件
    private String file = "routing.properties";
    private ContextHandler contextHandler = new ContextHandler();
    //http选项
    private HttpServerOptions options;

    @Override
    public void start() throws Exception {
        //构建配置数据
        buildConfig();
        //加载扩展点
        ExtensionManager em = extensionManager == null ? ExtensionManager.getInstance() : extensionManager;
        em.add(RoutingHandler.class);
        em.add(MessageHandler.class);
        em.add(ErrorHandler.class);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        //异常处理器
        Map<String, List<ErrorHandler>> errorMap = buildErrorHandlers(em);
        //循环变量业务处理链
        for (Route r : config.getRoutes()) {
            buildHandler(router, r, errorMap, em);
        }
        //构建消息处理链
        EventBus eventBus = vertx.eventBus();
        for (Route route : config.getMessages()) {
            buildConsumer(route, eventBus, em);
        }
        HttpServerOptions serverOptions = options == null ? new HttpServerOptions() : options;
        vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen();
    }

    protected void buildConfig() throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("file can not be empty.");
        }
        config = new VertxConfig();
        InputStream in;
        BufferedReader reader = null;
        try {
            File f = new File(file);
            if (f.exists()) {
                in = new FileInputStream(f);
            } else {
                in = this.getClass().getClassLoader().getResourceAsStream(file);
                if (in == null) {
                    throw new IOException("file is not found. " + file);
                }
            }
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String path;
            RouteType type;
            String[] handlers;
            int typeEnd;
            int pathEnd;
            Route route;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    //注释
                    continue;
                }
                typeEnd = line.indexOf(' ');
                if (typeEnd <= 0) {
                    //没有类型
                    continue;
                }
                path = null;
                type = RouteType.valueOf(line.substring(0, typeEnd).toUpperCase());
                if (type == null) {
                    //类型不支持
                    continue;
                }
                pathEnd = line.indexOf('=', typeEnd + 1);
                if (pathEnd > 0) {
                    path = line.substring(typeEnd + 1, pathEnd);
                    handlers = line.substring(pathEnd + 1).split(",");
                } else if (type != RouteType.ERROR) {
                    //非异常都需要路径，异常没有路径则是默认异常处理
                    continue;
                } else {
                    handlers = line.substring(typeEnd + 1).split(",");
                }
                if (handlers != null && handlers.length > 0) {
                    route = new Route(type, path);
                    for (String handler : handlers) {
                        handler = handler.trim();
                        if (handler != null) {
                            route.add(handler);
                        }
                    }
                    if (!route.isEmpty()) {
                        config.add(route);
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 构建消息链
     *
     * @param route
     * @param eventBus
     * @param em
     */
    protected void buildConsumer(final Route route, final EventBus eventBus, final ExtensionManager em) {
        MessageHandler handler;
        //设置消息处理链
        for (String name : route.getHandlers()) {
            handler = em.getExtension(MessageHandler.class, name);
            if (handler != null) {
                eventBus.consumer(route.getPath(), handler);
            }
        }
    }

    /**
     * 构建业务处理链
     *
     * @param router   路由器
     * @param route    路由配置
     * @param errorMap 异常处理链
     * @param em       插件管理器
     */
    protected void buildHandler(final Router router, final Route route, final Map<String, List<ErrorHandler>> errorMap,
                                final ExtensionManager em) {
        io.vertx.ext.web.Route webRoute = router.route(route.getType().getMethod(), route.getPath()).handler(contextHandler);
        //设置异常处理链
        List<ErrorHandler> errorHandlers = errorMap.get(route.getPath());
        if (errorHandlers == null) {
            errorHandlers = errorMap.get("");
        }
        if (errorHandlers != null) {
            for (ErrorHandler errorHandler : errorHandlers) {
                webRoute.failureHandler(errorHandler);
            }
        }
        //设置业务处理链
        for (String name : route.getHandlers()) {
            RoutingHandler handler = em.getExtension(RoutingHandler.class, name);
            if (handler != null) {
                webRoute.handler(handler);
            }
        }
    }

    /**
     * 创建异常处理链
     *
     * @param em
     * @return
     */
    protected Map<String, List<ErrorHandler>> buildErrorHandlers(final ExtensionManager em) {
        Map<String, List<ErrorHandler>> errorMap = new HashMap<>();
        List<ErrorHandler> errorHandlers;
        ErrorHandler errorHandler;
        String path;
        for (Route r : config.getErrors()) {
            path = r.getPath() != null && !r.getPath().isEmpty() ? r.getPath() : "";
            errorHandlers = errorMap.get(path);
            if (errorHandlers == null) {
                errorHandlers = new ArrayList<>();
                errorMap.put(r.getPath(), errorHandlers);
            }
            for (String name : r.getHandlers()) {
                errorHandler = em.getExtension(ErrorHandler.class, name);
                if (errorHandler != null) {
                    errorHandlers.add(errorHandler);
                }
            }
        }
        return errorMap;
    }

    public void setConfig(VertxConfig config) {
        this.config = config;
    }

    public void setExtensionManager(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setOptions(HttpServerOptions options) {
        this.options = options;
    }

    protected class ContextHandler implements Handler<RoutingContext> {

        @Override
        public void handle(final RoutingContext context) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
            String contentType = context.getAcceptableContentType();
            if (contentType == null || contentType.isEmpty()) {
                context.setAcceptableContentType("application/json;charset:utf-8");
            }
            context.next();
        }
    }
}
