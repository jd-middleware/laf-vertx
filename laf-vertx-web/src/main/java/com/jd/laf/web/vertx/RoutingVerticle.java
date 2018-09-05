package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.RouteType;
import com.jd.laf.web.vertx.config.VertxConfig;
import com.jd.laf.web.vertx.render.Renders;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.templ.TemplateEngine;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jd.laf.web.vertx.SystemContext.*;
import static com.jd.laf.web.vertx.config.VertxConfig.Builder.build;
import static com.jd.laf.web.vertx.config.VertxConfig.Builder.inherit;
import static com.jd.laf.web.vertx.handler.RenderHandler.render;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(RoutingVerticle.class.getName());

    //配置
    protected VertxConfig config;
    //参数
    protected ConcurrentMap<String, Object> parameters = new ConcurrentHashMap<>();
    //资源文件
    protected String file = "routing.xml";
    //http选项
    protected HttpServerOptions options;
    //验证器
    protected Validator validator;
    //模板引擎
    protected TemplateEngine engine;
    protected HttpServer httpServer;

    @Override
    public void start() throws Exception {
        //构建配置数据
        config = config == null ? inherit(build(file)) : config;

        SystemContext context = new SystemContext(vertx, parameters);

        //创建验证器
        buildValidator(context);
        //创建模板引擎
        buildTemplateEngine(context);

        //初始化插件

        MessageHandlers.setup(context);
        RoutingHandlers.setup(context);
        Renders.setup(context);

        Router router = Router.router(vertx);
        //构建业务处理链
        buildHandlers(router, config);
        //构建消息处理链
        buildConsumers(config);
        //启动服务
        if (options == null) {
            options = new HttpServerOptions();
            options.setPort(8080);
        }
        httpServer = vertx.createHttpServer(options);
        httpServer.requestHandler(router::accept).listen(event -> {
            if (event.succeeded()) {
                logger.info(String.format("success starting http server on port %d", httpServer.actualPort()));
            } else {
                logger.log(Level.SEVERE, String.format("failed starting http server on port %d",
                        httpServer.actualPort()), event.cause());
            }
        });

    }

    @Override
    public void stop() throws Exception {
        if (httpServer != null) {
            httpServer.close(event -> {
                if (event.succeeded()) {
                    logger.info(String.format("success closing http server on port %d", httpServer.actualPort()));
                } else {
                    logger.log(Level.SEVERE, String.format("failed closing http server on port %d",
                            httpServer.actualPort()), event.cause());
                }
            });
        }
    }

    /**
     * 构建模板引擎
     *
     * @param context
     * @throws Exception
     */
    protected void buildTemplateEngine(SystemContext context) throws Exception {
        if (engine == null) {
            engine = (TemplateEngine) context.getObject(TEMPLATE_ENGINE);
            if (engine == null) {
                String type = (String) context.getString(TEMPLATE_TYPE);
                if (type != null && !type.isEmpty()) {
                    TemplateProvider provider = TemplateProviders.getPlugin(type);
                    if (provider != null) {
                        engine = provider.create(context);
                    }
                }
            }
        }
        if (engine != null) {
            context.put(TEMPLATE_ENGINE, engine);
        }
    }

    /**
     * 构建验证器
     */
    protected void buildValidator(final SystemContext context) {
        if (validator == null) {
            validator = (Validator) context.getObject(VALIDATOR);
            if (validator == null) {
                validator = Validation.buildDefaultValidatorFactory().getValidator();
            }
        }
        context.put(VALIDATOR, validator);
    }

    /**
     * 构造消息处理链
     *
     * @param config
     */
    protected void buildConsumers(final VertxConfig config) {
        EventBus eventBus = vertx.eventBus();
        MessageHandler handler;
        for (RouteConfig route : config.getMessages()) {
            //设置消息处理链
            for (String name : route.getHandlers()) {
                handler = MessageHandlers.getPlugin(name);
                if (handler != null && route.getPath() != null && !route.getPath().isEmpty()) {
                    eventBus.consumer(route.getPath(), handler);
                }
            }
        }
    }

    /**
     * 构造处理链
     *
     * @param router 路由
     * @param config 配置
     */
    protected void buildHandlers(final Router router, final VertxConfig config) {
        Route route;
        String path;
        RouteType type;
        for (RouteConfig info : config.getRoutes()) {
            // 过滤掉模板
            if (info.isRoute()) {
                continue;
            }
            type = info.getType();
            path = info.getPath();
            path = path != null ? path.trim() : path;
            //如果没有路径，则默认路由，如果没有请求方法，则默认匹配所有请求方法
            if (path == null || path.isEmpty()) {
                route = router.route();
            } else if (!info.isRegex()) {
                route = type == null ? router.route(path) : router.route(type.getMethod(), path);
            } else {
                route = type == null ? router.routeWithRegex(path) : router.routeWithRegex(type.getMethod(), path);
            }
            route.handler(BodyHandler.create());
            if (info.getOrder() != null) {
                route.order(info.getOrder());
            }
            // 设置能产生的内容
            buildProduces(route, info);
            // 设置能消费的内容
            buildConsumes(route, info);
            //设置异常处理链
            buildErrors(route, info);
            //设置业务处理链
            buildHandlers(route, info);
        }
    }

    /**
     * 构建路由的异常处理
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildErrors(final Route route, final RouteConfig config) {
        if (config.getErrors() != null) {
            ErrorHandler handler;
            for (String error : config.getErrors()) {
                handler = ErrorHandlers.getPlugin(error);
                if (handler != null) {
                    route.failureHandler(handler);
                } else {
                    logger.warning(String.format("error handler %s is not found. ignore.", error));
                }
            }
        }
    }

    /**
     * 构建路由处理器
     *
     * @param route  路由对象
     * @param config 路由配置
     */
    protected void buildHandlers(final Route route, final RouteConfig config) {
        RoutingHandler handler;
        Command command;
        //上下文处理
        for (String name : config.getHandlers()) {
            handler = RoutingHandlers.getPlugin(name);
            if (handler != null) {
                if (handler instanceof RouteAware) {
                    //感知路由配置，创建新对象
                    handler = ((RouteAware<RoutingHandler>) handler).create();
                    ((RouteAware) handler).setup(config);
                }
                route.handler(handler);
            } else {
                //命令
                command = Commands.getPlugin(name);
                if (command != null) {
                    route.handler(new CommandHandler(command, validator));
                } else {
                    logger.warning(String.format("handler %s is not found. ignore.", name));
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
        switch (config.getType()) {
            case PUT:
            case POST:
            case PATCH:
                if (config.getConsumes() != null) {
                    for (String type : config.getConsumes()) {
                        route.consumes(type);
                    }
                }
                break;
            default:
                break;
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

    public void setConfig(VertxConfig config) {
        this.config = config;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    public void setParameters(Map<String, Object> parameters) {
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setOptions(HttpServerOptions options) {
        this.options = options;
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
                Object obj = clone.execute();
                Command.Result result = null;
                if (obj != null) {
                    if (obj instanceof Command.Result) {
                        result = (Command.Result) obj;
                    } else {
                        result = new Command.Result(obj);
                    }
                }
                if (result != null) {
                    //有返回结果
                    if (result.getTemplate() != null && !result.getTemplate().isEmpty()) {
                        //存放模板
                        context.put(TEMPLATE, result.getTemplate());
                    }
                    if (result.getKey() != null) {
                        //存放实际的返回结果
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
