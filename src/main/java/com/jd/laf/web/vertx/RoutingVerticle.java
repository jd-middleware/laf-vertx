package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.RouteType;
import com.jd.laf.web.vertx.config.VertxConfig;
import com.jd.laf.web.vertx.config.VertxConfig.Builder;
import com.jd.laf.web.vertx.render.Renders;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TimeoutHandler;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.jd.laf.web.vertx.Context.VALIDATOR;
import static com.jd.laf.web.vertx.config.RouteConfig.PLACE_HOLDER;
import static com.jd.laf.web.vertx.handler.RenderHandler.render;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(RoutingVerticle.class.getName());

    //配置
    protected VertxConfig config;
    //参数
    protected Map<String, Object> parameters;
    //资源文件
    protected String file = "routing.xml";
    //http选项
    protected HttpServerOptions options;
    //验证器
    protected Validator validator;
    protected HttpServer httpServer;

    @Override
    public void start() throws Exception {
        //构建配置数据
        config = config == null ? buildConfig(file) : config;

        if (validator == null) {
            if (parameters != null) {
                validator = (Validator) parameters.get(VALIDATOR);
            }
            if (validator == null) {
                validator = Validation.buildDefaultValidatorFactory().getValidator();
            }
        }
        if (parameters == null) {
            parameters = new HashMap<>();
            parameters.put(VALIDATOR, validator);
        }

        //初始化插件
        Context context = new Context(vertx, parameters);
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
                }
        );

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
     * @param config
     */
    protected void buildHandlers(final Router router, final VertxConfig config) {
        Route route;
        String path;
        RouteType type;
        for (RouteConfig info : config.getRoutes()) {
            // 过滤掉模板
            if (info.isTemplate()) {
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
        if (config.getErrors() != null) {
            ErrorHandler handler;
            for (String error : config.getErrors()) {
                handler = ErrorHandlers.getPlugin(error);
                if (handler != null) {
                    route.failureHandler(handler);
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
    protected void buildHandler(final Route route, final RouteConfig config) {
        RoutingHandler handler;
        Command command;
        //超时处理
        if (config.getTimeout() != null && config.getTimeout() > 0) {
            route.handler(TimeoutHandler.create(config.getTimeout()));
        }
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
    protected static VertxConfig buildConfig(String file) throws IOException, JAXBException {
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
                in = RoutingVerticle.class.getClassLoader().getResourceAsStream(file);
                if (in == null) {
                    throw new IOException("file is not found. " + file);
                }
            }
            reader = new BufferedReader(new InputStreamReader(in));
            return buildConfig(Builder.build(reader));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 构建配置，处理继承
     *
     * @param config
     * @return
     */
    protected static VertxConfig buildConfig(final VertxConfig config) {
        if (config == null) {
            return config;
        }
        List<RouteConfig> routes = config.getRoutes();
        if (routes == null || routes.isEmpty()) {
            return config;
        }
        LinkedList<RouteConfig> inherits = new LinkedList<>();
        for (RouteConfig cfg : config.getRoutes()) {
            if (cfg.getInherit() == null || cfg.getInherit().isEmpty() || cfg.isTemplate()) {
                continue;
            }
            inherits.add(cfg);
        }
        if (inherits == null || inherits.isEmpty()) {
            return config;
        }

        Map<String, RouteConfig> map = config.getRoutes().stream().filter(a -> a.getName() != null)
                .collect(Collectors.toMap(a -> a.getName(), a -> a));
        RouteConfig parent;
        List<String> result;
        boolean flag;
        //当前节点遍历过的节点，防止递归
        Set<RouteConfig> graph = new HashSet<>(map.size());
        for (RouteConfig cfg : inherits) {
            //获取当前节点
            parent = map.get(cfg.getInherit());
            //清理当前节点遍历过的节点
            graph.clear();
            graph.add(cfg);
            while (parent != null && graph.add(parent)) {
                if (cfg.getType() == null && parent.getType() != null) {
                    cfg.setType(parent.getType());
                }
                if (cfg.getConsumes() == null && parent.getConsumes() != null) {
                    cfg.setConsumes(parent.getConsumes());
                }
                if (cfg.getProduces() == null && cfg.getProduces() != null) {
                    cfg.setProduces(parent.getProduces());
                }
                //处理业务处理器
                if (parent.getHandlers() != null && !parent.getHandlers().isEmpty()) {
                    if (cfg.getHandlers() == null || cfg.getHandlers().isEmpty()) {
                        cfg.setHandlers(parent.getHandlers());
                    } else {
                        result = new ArrayList<>(parent.getHandlers().size()
                                + (cfg.getHandlers() == null ? 0 : cfg.getHandlers().size()));
                        flag = false;
                        for (String b : parent.getHandlers()) {
                            if (!PLACE_HOLDER.equals(b)) {
                                result.add(b);
                            } else if (!flag) {
                                flag = true;
                                result.addAll(cfg.getHandlers());
                            }
                        }
                        if (!flag) {
                            result.addAll(cfg.getHandlers());
                        }
                        cfg.setHandlers(result);
                    }
                }
                //处理异常处理器，直接覆盖
                if ((cfg.getErrors() == null || cfg.getErrors().isEmpty())
                        && parent.getErrors() != null && !parent.getErrors().isEmpty()) {
                    cfg.setErrors(parent.getHandlers());
                }
                parent = parent.getInherit() == null || parent.getInherit().isEmpty() ? null : map.get(parent.getInherit());
            }
        }
        return config;
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
