package com.jd.laf.web.vertx;

import com.jd.laf.extension.ExtensionManager;
import com.jd.laf.web.vertx.VertxConfig.Builder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.validation.Validation;
import javax.validation.Validator;
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
    //验证器
    private Validator validator;


    @Override
    public void start() throws Exception {
        //构建配置数据
        buildConfig();
        //加载扩展点
        ExtensionManager em = extensionManager == null ? ExtensionManager.getInstance() : extensionManager;
        em.add(RoutingHandler.class);
        em.add(MessageHandler.class);
        em.add(ErrorHandler.class);
        em.add(Command.class);

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        //构建异常处理器
        Map<String, List<ErrorHandler>> errors = buildErros(em);
        //构建业务处理链
        buildHandlers(router, errors, em);
        //构建消息处理链
        buildConsumers(em);
        //启动服务
        HttpServerOptions serverOptions = options == null ? new HttpServerOptions() : options;
        vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen();
    }

    /**
     * 构造消息处理链
     *
     * @param em 插件管理器
     */
    protected void buildConsumers(final ExtensionManager em) {
        EventBus eventBus = vertx.eventBus();
        MessageHandler handler;
        for (Route route : config.getMessages()) {
            //设置消息处理链
            for (String name : route.getHandlers()) {
                handler = em.getExtension(MessageHandler.class, name);
                if (handler != null) {
                    eventBus.consumer(route.getPath(), handler);
                }
            }
        }
    }

    /**
     * 构造处理链
     *
     * @param router 路由
     * @param errors 异常处理器
     * @param em     插件管理器
     */
    protected void buildHandlers(final Router router, final Map<String, List<ErrorHandler>> errors, final ExtensionManager em) {
        io.vertx.ext.web.Route webRoute;
        List<ErrorHandler> errorHandlers;
        RoutingHandler handler;
        Command command;
        for (Route route : config.getRoutes()) {
            webRoute = router.route(route.getType().getMethod(), route.getPath()).handler(contextHandler);
            //设置异常处理链
            errorHandlers = errors.get(route.getPath());
            if (errorHandlers == null) {
                errorHandlers = errors.get("");
            }
            if (errorHandlers != null) {
                for (ErrorHandler errorHandler : errorHandlers) {
                    webRoute.failureHandler(errorHandler);
                }
            }
            //设置业务处理链
            for (String name : route.getHandlers()) {
                handler = em.getExtension(RoutingHandler.class, name);
                if (handler != null) {
                    webRoute.handler(handler);
                } else {
                    //命令
                    command = em.getExtension(Command.class, name);
                    if (command != null) {
                        webRoute.handler(new CommandHandler(command));
                    }
                }
            }
        }
    }

    protected void buildConfig() throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("file can not be empty.");
        }
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
            config = Builder.build(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    /**
     * 创建异常处理链
     *
     * @param em
     * @return
     */
    protected Map<String, List<ErrorHandler>> buildErros(final ExtensionManager em) {
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

    public void setValidator(Validator validator) {
        this.validator = validator;
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

    /**
     * 上下文处理器
     */
    protected class ContextHandler implements Handler<RoutingContext> {

        @Override
        public void handle(final RoutingContext context) {
            context.put(Command.VALIDATOR, validator);
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
