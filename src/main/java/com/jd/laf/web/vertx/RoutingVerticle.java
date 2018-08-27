package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.web.vertx.annotation.ErrorHandler;
import com.jd.laf.web.vertx.annotation.ErrorHandlers;
import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.VertxConfig;
import com.jd.laf.web.vertx.config.VertxConfig.Builder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

import static com.jd.laf.web.vertx.RenderHandler.render;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {

    public static final String DEFAULT_ERROR = "";
    //配置
    protected VertxConfig config;
    //参数
    protected Map<String, Object> parameters;
    //资源文件
    protected String file = "routing.xml";
    protected ContextHandler contextHandler;
    //http选项
    protected HttpServerOptions options;
    //验证器
    protected Validator validator;
    protected Map<String, List<ErrorHandler>> errors;


    @Override
    public void start() throws Exception {
        //构建配置数据
        buildConfig();

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        contextHandler = new ContextHandler(parameters, validator);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        //构建异常处理器
        errors = buildErros();
        //构建业务处理链
        buildHandlers(router);
        //构建消息处理链
        buildConsumers();
        //启动服务
        HttpServerOptions serverOptions = options == null ? new HttpServerOptions() : options;
        vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen();
    }

    /**
     * 构造消息处理链
     */
    protected void buildConsumers() {
        EventBus eventBus = vertx.eventBus();
        MessageHandler handler;
        for (RouteConfig route : config.getMessages()) {
            //设置消息处理链
            for (String name : route.getHandlers()) {
                handler = MessageHandlers.getPlugin(name);
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
     */
    protected void buildHandlers(final Router router) {
        Route route;
        for (RouteConfig info : config.getRoutes()) {
            route = router.route(info.getType().getMethod(), info.getPath()).handler(contextHandler);
            // 设置能产生的内容
            buildProduces(route, info);
            // 设置能消费的内容
            buildConsumes(route, info);
            //设置异常处理链
            buildErrors(route, info);
            //设置业务处理链
            buildHandler(route, info);
        }
    }

    /**
     * 构建路由的异常处理
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildErrors(final Route route, final RouteConfig config) {
        List<ErrorHandler> errorHandlers;
        errorHandlers = errors.get(config.getPath());
        if (errorHandlers == null) {
            errorHandlers = errors.get(DEFAULT_ERROR);
        }
        if (errorHandlers != null) {
            for (ErrorHandler errorHandler : errorHandlers) {
                route.failureHandler(errorHandler);
            }
        }
    }

    /**
     * 构建路由处理器
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildHandler(final Route route, final RouteConfig config) {
        RoutingHandler handler;
        Command command;
        for (String name : config.getHandlers()) {
            handler = RoutingHandlers.getPlugin(name);
            if (handler != null) {
                route.handler(handler);
            } else {
                //命令
                command = Commands.getPlugin(name);
                if (command != null) {
                    route.handler(new CommandHandler(command, validator));
                }
            }
        }
    }

    /**
     * 构建消费内容
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildConsumes(final Route route, final RouteConfig config) {
        if (config.getConsumes() != null) {
            for (String type : config.getConsumes()) {
                route.consumes(type);
            }
        }
    }

    /**
     * 构建生产内容
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildProduces(final Route route, final RouteConfig config) {
        if (config.getProduces() != null) {
            for (String type : config.getProduces()) {
                route.produces(type);
            }
        }
    }

    /**
     * 读取配置
     *
     * @throws IOException
     * @throws JAXBException
     */
    protected void buildConfig() throws IOException, JAXBException {
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
     * @return
     */
    protected Map<String, List<ErrorHandler>> buildErros() {
        Map<String, List<ErrorHandler>> errorMap = new HashMap<>();
        List<ErrorHandler> errorHandlers;
        ErrorHandler errorHandler;
        String path;
        for (RouteConfig r : config.getErrors()) {
            path = r.getPath() != null && !r.getPath().isEmpty() ? r.getPath() : DEFAULT_ERROR;
            errorHandlers = errorMap.get(path);
            if (errorHandlers == null) {
                errorHandlers = new ArrayList<>();
                errorMap.put(r.getPath(), errorHandlers);
            }
            for (String name : r.getHandlers()) {
                errorHandler = ErrorHandlers.getPlugin(name);
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
    protected static class ContextHandler implements Handler<RoutingContext> {

        private Map<String, Object> parameters;
        protected Validator validator;

        public ContextHandler(Map<String, Object> parameters, Validator validator) {
            this.parameters = parameters;
            this.validator = validator;
        }

        @Override
        public void handle(final RoutingContext context) {
            context.put(Command.VALIDATOR, validator);
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    context.put(entry.getKey(), entry.getValue());
                }
            }
            context.next();
        }
    }

    /**
     * 命令处理器
     */
    protected static class CommandHandler implements Handler<RoutingContext> {

        protected Command command;
        protected Validator validator;

        public CommandHandler(Command command, Validator validator) {
            this.command = command;
            this.validator = validator;
        }

        @Override
        public void handle(final RoutingContext context) {
            try {
                //克隆一份
                Command clone = command.getClass().newInstance();
                //绑定
                Binding.bind(context, clone);
                //验证
                validate(clone);
                //执行
                Command.Result result = clone.execute();
                if (result != null) {
                    //有返回结果
                    if (result.getKey() != null) {
                        context.put(result.getKey(), result.getResult());
                    }
                    switch (result.getType()) {
                        case CONTINUE:
                            //继续
                            context.next();
                            break;
                        case END:
                            //渲染输出
                            break;
                        case HOLD:
                            render(context);
                            //挂住
                            break;
                        default:
                            //默认继续
                            context.next();
                    }
                } else {
                    context.next();
                }
            } catch (Exception e) {
                context.fail(e);
            }
        }

        /**
         * 验证
         *
         * @param command
         */
        protected void validate(final Command command) {
            Set<ConstraintViolation<Command>> constraints = validator.validate(command);
            if (constraints != null && !constraints.isEmpty()) {
                StringBuilder builder = new StringBuilder(100);
                int count = 0;
                for (ConstraintViolation<Command> violation : constraints) {
                    if (count++ > 0) {
                        builder.append('\n');
                    }
                    builder.append(violation.getMessage());
                }
                throw new ValidationException(builder.toString());
            }
        }
    }
}
