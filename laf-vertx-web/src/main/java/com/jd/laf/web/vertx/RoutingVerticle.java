package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.RouteType;
import com.jd.laf.web.vertx.config.VertxConfig;
import com.jd.laf.web.vertx.lifecycle.Registrar;
import com.jd.laf.web.vertx.message.RouteMessage;
import com.jd.laf.web.vertx.pool.Pool;
import com.jd.laf.web.vertx.pool.Poolable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.MyRoute;
import io.vertx.ext.web.impl.MyRouter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.jd.laf.web.vertx.Environment.COMMAND_POOL_CAPACITY;
import static com.jd.laf.web.vertx.Environment.COMMAND_POOL_INITIALIZE_SIZE;
import static com.jd.laf.web.vertx.Plugin.*;
import static com.jd.laf.web.vertx.config.VertxConfig.Builder.build;
import static com.jd.laf.web.vertx.config.VertxConfig.Builder.inherit;
import static com.jd.laf.web.vertx.handler.RenderHandler.render;

/**
 * 路由装配件
 */
public class RoutingVerticle extends AbstractVerticle {
    public static final String ROUTING_CONFIG_FILE = "vertx.file";
    public static final String DEFAULT_ROUTING_CONFIG_FILE = "routing.xml";
    public static final int DEFAULT_PORT = 8080;
    protected static final Logger logger = LoggerFactory.getLogger(RoutingVerticle.class);
    protected static final AtomicLong counter = new AtomicLong(0);

    //注入的环境
    protected Environment env;
    //HTTP选项
    protected HttpServerOptions httpOptions;
    //资源文件
    protected String file;
    //路由配置提供者
    protected List<RouteProvider> providers;
    protected MessageConsumer consumer;

    //配置
    protected VertxConfig config;
    //Http服务
    protected HttpServer httpServer;
    //路由信息
    protected Router router;
    //ID
    protected long id;

    public RoutingVerticle() {
        this(new Environment.MapEnvironment(), new HttpServerOptions().setPort(DEFAULT_PORT), DEFAULT_ROUTING_CONFIG_FILE, null);
    }

    public RoutingVerticle(final Environment env) {
        this(env, new HttpServerOptions().setPort(DEFAULT_PORT), DEFAULT_ROUTING_CONFIG_FILE, null);
    }

    public RoutingVerticle(final Environment env, final HttpServerOptions httpOptions) {
        this(env, httpOptions, DEFAULT_ROUTING_CONFIG_FILE, null);
    }

    public RoutingVerticle(final Environment env, final HttpServerOptions httpOptions, final String file) {
        this(env, httpOptions, file, null);
    }

    public RoutingVerticle(final Environment env, final HttpServerOptions httpOptions, final String file, final List<RouteProvider> providers) {
        this.env = env != null ? env : new Environment.MapEnvironment();
        this.httpOptions = httpOptions == null ? new HttpServerOptions().setPort(DEFAULT_PORT) : httpOptions;
        this.file = file != null ? file : env.getString(ROUTING_CONFIG_FILE, DEFAULT_ROUTING_CONFIG_FILE);
        this.providers = providers;
        this.id = counter.incrementAndGet();
    }

    public RoutingVerticle(final Map<String, Object> parameters) {
        this(new Environment.MapEnvironment(parameters), new HttpServerOptions().setPort(DEFAULT_PORT), DEFAULT_ROUTING_CONFIG_FILE, null);
    }

    public RoutingVerticle(final Map<String, Object> parameters, final HttpServerOptions httpOptions) {
        this(new Environment.MapEnvironment(parameters), httpOptions, DEFAULT_ROUTING_CONFIG_FILE, null);
    }

    public RoutingVerticle(final Map<String, Object> parameters, final HttpServerOptions httpOptions, final String file) {
        this(new Environment.MapEnvironment(parameters), httpOptions, file, null);
    }

    public RoutingVerticle(final Map<String, Object> parameters, final HttpServerOptions httpOptions, final String file, final List<RouteProvider> providers) {
        this(new Environment.MapEnvironment(parameters), httpOptions, file, providers);
    }

    @Override
    public void start() throws Exception {
        try {
            //构建配置数据
            config = inherit(build(file));

            //初始化插件
            register(vertx, env, config);

            router = createRouter(env);
            //通过配置文件构建路由
            addRoutes(router, config.getRoutes(), env);
            //通过路由提供者构建路由
            if (providers != null) {
                for (RouteProvider provider : providers) {
                    addRoutes(router, provider.getRoutes(), env);
                }
            }
            //启动服务
            httpServer = vertx.createHttpServer(httpOptions);
            httpServer.requestHandler(router).listen(event -> {
                if (event.succeeded()) {
                    logger.info(String.format("success binding http listener %d on port %d", id, httpServer.actualPort()));
                } else {
                    logger.error(String.format("failed binding http listener %d on port %d", id,
                            httpServer.actualPort()), event.cause());
                }
            });
            //注册路由变更消息监听器
            consumer = vertx.eventBus().consumer(RouteMessage.TOPIC, (Handler<Message<RouteMessage>>) event -> {
                RouteMessage message = event.body();
                switch (message.getType()) {
                    case ADD:
                        addRoute(message.getConfig());
                        break;
                    case REMOVE:
                        removeRoute(message.getConfig());
                        break;
                }
            });
            logger.info(String.format("success starting routing verticle %d at %s", id, deploymentID()));
        } catch (Exception e) {
            logger.error(String.format("failed starting routing verticle %d at %s", id, deploymentID()), e);
            throw e;
        }
    }


    /**
     * 初始化
     *
     * @param vertx
     * @param environment 环境上下文
     * @param config
     * @throws Exception
     */
    public void register(final Vertx vertx, final Environment environment, final VertxConfig config) throws Exception {
        //防止多次注册
        if (Registrar.counter.incrementAndGet() == 1) {
            for (Registrar registrar : REGISTRAR.extensions()) {
                //注册
                registrar.register(vertx, environment, config);
            }
            //通知等待
            Registrar.latch.countDown();
        } else {
            //等到初始化完成
            Registrar.latch.await();
        }
    }

    /**
     * 注销
     *
     * @param vertx vertx对象
     */
    public void deregister(final Vertx vertx) {
        if (Registrar.counter.decrementAndGet() == 0) {
            //反序进行注销
            REGISTRAR.reverse().forEach(o -> o.deregister(vertx));
        }
    }

    /**
     * 创建路由管理器
     *
     * @param environment 环境
     * @return
     */
    protected Router createRouter(final Environment environment) {
        return new MyRouter(vertx, environment);
    }

    @Override
    public void stop() {
        if (httpServer != null) {
            httpServer.close(event -> {
                if (event.succeeded()) {
                    logger.info(String.format("success removing http listener %d on port %d", id, httpServer.actualPort()));
                } else {
                    logger.error(String.format("failed removing http listener %d on port %d", id,
                            httpServer.actualPort()), event.cause());
                }
            });
        }
        //注销本实例的消费者
        if (consumer != null) {
            consumer.unregister(new Handler<AsyncResult<Void>>() {
                @Override
                public void handle(AsyncResult<Void> event) {
                    if (event.succeeded()) {
                        logger.info(String.format("success unregistering consumer %d of %s", id, RouteMessage.TOPIC));
                    } else {
                        logger.error(String.format("failed unregistering consumer %d of %s", id, RouteMessage.TOPIC));
                    }
                }
            });
        }
        //注销本实例的消费者
        deregister(vertx);
        logger.info(String.format("success stop routing verticle %d at %s ", id, deploymentID()));
    }

    /**
     * 构造处理链
     *
     * @param router      路由
     * @param environment 环境
     */
    protected void addRoutes(final Router router, final List<RouteConfig> routes, final Environment environment) {
        if (routes != null) {
            for (RouteConfig info : routes) {
                addRoute(info, router, environment);
            }
        }
    }

    /**
     * 添加路由
     *
     * @param config
     * @param router
     * @param environment
     */
    protected void addRoute(final RouteConfig config, final Router router, final Environment environment) {
        // 过滤掉模板
        if (config.isRoute()) {
            return;
        }
        Route route = getRoute(config, router);
        if (route == null) {
            return;
        }
        if (config.getOrder() != null) {
            route.order(config.getOrder());
        }
        //设置路由配置
        if (route instanceof MyRoute) {
            ((MyRoute) route).setConfig(config);
        }
        // 设置能产生的内容
        buildProduces(route, config);
        // 设置能消费的内容
        buildConsumes(route, config);
        //设置异常处理链
        buildErrors(route, config);
        //设置业务处理链
        buildHandlers(route, config, environment);

    }

    /**
     * 获取路由
     *
     * @param config 路由配置
     * @param router 路由器
     * @return
     */
    protected Route getRoute(final RouteConfig config, final Router router) {
        if (config == null || router == null) {
            return null;
        }
        Route route;
        RouteType type = config.getType();
        String path = config.getPath();
        path = path != null ? path.trim() : path;
        //如果没有路径，则默认路由，如果没有请求方法，则默认匹配所有请求方法
        if (path == null || path.isEmpty()) {
            route = router.route();
        } else if (!config.isRegex()) {
            route = type == null ? router.route(path) : router.route(type.getMethod(), path);
        } else {
            route = type == null ? router.routeWithRegex(path) : router.routeWithRegex(type.getMethod(), path);
        }
        return route;
    }

    /**
     * 添加路由
     *
     * @param config
     */
    protected void addRoute(final RouteConfig config) {
        addRoute(config, router, env);
    }

    /**
     * 删除路由
     *
     * @param config 路由配置
     */
    protected void removeRoute(final RouteConfig config) {
        Route route = getRoute(config, router);
        if (route != null) {
            route.remove();
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
                handler = ERROR.get(error);
                if (handler != null) {
                    route.failureHandler(handler);
                } else {
                    logger.warn(String.format("error handler %s is not found. ignore.", error));
                }
            }
        }
    }

    /**
     * 构建路由处理器
     *
     * @param route       路由对象
     * @param config      路由配置
     * @param environment 环境
     */
    protected void buildHandlers(final Route route, final RouteConfig config, final Environment environment) {
        RoutingHandler handler;
        Command command;
        //上下文处理
        for (String name : config.getHandlers()) {
            handler = ROUTING.get(name);
            if (handler != null) {
                if (handler instanceof RouteAware) {
                    //感知路由配置，复制一份对象，确保环境初始化的设置
                    handler = ((RouteAware) handler).clone();
                    ((RouteAware) handler).setup(config);
                }
                route.handler(handler);
            } else {
                //命令
                command = COMMAND.get(name);
                if (command != null) {
                    Pool<Command> pool = null;
                    //判断命令是否需要池化
                    if (command instanceof Poolable) {
                        //对象池
                        int capacity = environment.getInteger(COMMAND_POOL_CAPACITY, 500);
                        int initializeSize = environment.getInteger(COMMAND_POOL_INITIALIZE_SIZE, 50);
                        if (capacity > 0) {
                            //构造对象池
                            pool = POOL.get().create(capacity);
                            if (initializeSize > 0) {
                                int min = Math.min(initializeSize, capacity);
                                //初始化对象池大小
                                for (int i = 0; i < min; i++) {
                                    try {
                                        pool.release(command.getClass().newInstance());
                                    } catch (InstantiationException e) {
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                            }
                        }
                    } else {
                        pool = null;
                    }
                    route.handler(new CommandHandler(command, pool));
                } else {
                    logger.warn(String.format("handler %s is not found. ignore.", name));
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

    /**
     * 命令处理器
     */
    protected static class CommandHandler implements Handler<RoutingContext> {
        //命令
        protected Command command;
        //对象池
        protected Pool<Command> pool;

        public CommandHandler(Command command, Pool<Command> pool) {
            this.command = command;
            this.pool = pool;
        }

        @Override
        public void handle(final RoutingContext context) {
            Command clone = null;
            try {
                //克隆一份
                clone = pool == null ? null : pool.get();
                if (clone == null) {
                    //构造新对象
                    clone = command.getClass().newInstance();
                }
                //使用环境和当前上下文进行绑定
                //理论上只有新创建的对象才需要使用环境和上下文同时绑定，对象池中的对象已经使用环境绑定过了
                //但是，有可能开发人员在清理的代码里面不小心清理了环境绑定的对象
                //避免出错，所以都重新绑定一次
                Binding.bind(context, clone);
                //验证
                Validates.validate(clone);
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
                        context.put(Environment.TEMPLATE, result.getTemplate());
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
                            render(context, true);
                            break;
                        case HOLD:
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
            } finally {
                if (pool != null && clone != null) {
                    pool.release(clone);
                }
            }
        }
    }

}
