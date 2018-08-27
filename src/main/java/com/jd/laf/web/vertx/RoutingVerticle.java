package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.web.vertx.VertxConfig.Builder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.*;
import java.util.*;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {

    //配置
    private VertxConfig config;
    //参数
    private Map<String, Object> parameters;
    //资源文件
    private String file = "routing.properties";
    private ContextHandler contextHandler;
    //http选项
    private HttpServerOptions options;
    //验证器
    private Validator validator;


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
        Map<String, List<ErrorHandler>> errors = buildErros();
        //构建业务处理链
        buildHandlers(router, errors);
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
        for (Route route : config.getMessages()) {
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
     * @param errors 异常处理器
     */
    protected void buildHandlers(final Router router, final Map<String, List<ErrorHandler>> errors) {
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
                handler = RoutingHandlers.getPlugin(name);
                if (handler != null) {
                    webRoute.handler(handler);
                } else {
                    //命令
                    command = Commands.getPlugin(name);
                    if (command != null) {
                        webRoute.handler(new CommandHandler(command, validator));
                    }
                }
            }
        }
    }

    /**
     * 读取配置
     *
     * @throws IOException
     */
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
     * @return
     */
    protected Map<String, List<ErrorHandler>> buildErros() {
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
            String contentType = context.getAcceptableContentType();
            if (contentType == null || contentType.isEmpty()) {
                context.setAcceptableContentType("application/json;charset:utf-8");
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
                    //继续执行
                    if (result.getType() == Command.ResultType.CONTINUE) {
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
