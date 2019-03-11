package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.binding.Binding.BinderAnnotation;
import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.converter.Conversion;
import com.jd.laf.binding.converter.ConversionType;
import com.jd.laf.binding.converter.Converter;
import com.jd.laf.binding.converter.Scope;
import com.jd.laf.binding.reflect.Generics;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.extension.ExtensionMeta;
import com.jd.laf.web.vertx.annotation.CPath;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.jd.laf.binding.Plugin.BINDER;
import static com.jd.laf.binding.Plugin.CONVERTER;
import static com.jd.laf.binding.util.Primitive.inbox;
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
    // 缓存方法的绑定关系
    protected static Map<Method, List<ParameterBinding>> PARAMETERS = new ConcurrentHashMap<>();

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
            startHttpServer();
            //注册路由变更消息监听器
            dynamicRoute();
            logger.info(String.format("success starting routing verticle %d at %s", id, deploymentID()));
        } catch (Exception e) {
            logger.error(String.format("failed starting routing verticle %d at %s", id, deploymentID()), e);
            throw e;
        }
    }

    /**
     * 注册动态路由信息
     */
    protected void dynamicRoute() {
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
    }

    /**
     * 启动HTTP服务
     */
    protected void startHttpServer() {
        httpServer = vertx.createHttpServer(httpOptions);
        //配置HTTP服务处理器
        ExceptionHandler exceptionHandler = EXCEPTION.get(config.getException());
        ConnectionHandler connectionHandler = CONNECTION.get(config.getConnection());
        if (exceptionHandler != null) {
            httpServer.exceptionHandler(exceptionHandler);
        }
        if (connectionHandler != null) {
            httpServer.connectionHandler(connectionHandler);
        }
        httpServer.requestHandler(router);
        httpServer.listen(event -> {
            if (event.succeeded()) {
                logger.info(String.format("success binding http listener %d on port %d", id, httpServer.actualPort()));
            } else {
                logger.error(String.format("failed binding http listener %d on port %d", id,
                        httpServer.actualPort()), event.cause());
            }
        });

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
        Handler<RoutingContext> handler;
        ExtensionMeta<Command, String> meta;
        HandlerName handlerName;
        //上下文处理
        for (String name : config.getHandlers()) {
            handlerName = new HandlerName(name);
            handler = ROUTING.get(handlerName.getHandler());
            if (handler == null) {
                //命令插件元数据
                handler = buildCommand(handlerName, environment);
            }
            if (handler != null) {
                if (handler instanceof RouteAware) {
                    //感知路由配置，复制一份对象，确保环境初始化的设置
                    handler = ((RouteAware) handler).clone();
                    ((RouteAware) handler).setup(config);
                }
                if (handler instanceof Blocking) {
                    route.blockingHandler(handler);
                } else {
                    route.handler(handler);
                }
            } else {
                logger.warn(String.format("handler %s is not found. ignore.", name));
            }
        }
    }

    /**
     * 创建命令
     *
     * @param name
     * @param environment
     * @return
     */
    protected Handler<RoutingContext> buildCommand(final HandlerName name, final Environment environment) {
        ExtensionMeta<Command, String> meta = COMMAND.meta(name.getHandler());
        if (meta == null) {
            return null;
        }
        Command command;
        Pool<Command> pool = null;
        //判断命令是否需要池化
        if (Poolable.class.isAssignableFrom(meta.getExtension().getClazz())) {
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
                        command = meta.getTarget();
                        if (command != null) {
                            pool.release(command);
                        }
                    }
                }
            }
        } else {
            pool = null;
        }
        //方法级处理器需要传递方法信息
        if (name.getPath() != null && !name.getPath().isEmpty()) {
            LinkedList<Method> candidates = new LinkedList<>();
            CPath path;
            for (Method method : meta.getExtension().getClazz().getMethods()) {
                path = method.getAnnotation(CPath.class);
                if (path != null && path.value().equals(name.getPath())) {
                    candidates.addFirst(method);
                    break;
                } else if (method.getName().equals(name.getPath())) {
                    candidates.add(method);
                }
            }
            if (candidates.isEmpty()) {
                logger.warn(String.format("未能在类 %s 下，找到@Path为 %s 的方法.", meta.getExtension().getClazz(), name.getPath()));
                return null;
            } else {
                name.setMethod(candidates.getFirst());
                return new CommandHandler(meta, pool, name);
            }
        } else {
            return new CommandHandler(meta, pool, name);
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
        protected ExtensionMeta<Command, String> meta;
        //对象池
        protected Pool<Command> pool;
        //执行方法
        protected HandlerName handlerName;

        public CommandHandler(ExtensionMeta<Command, String> meta, Pool<Command> pool, HandlerName handlerName) {
            this.meta = meta;
            this.pool = pool;
            this.handlerName = handlerName;
        }

        @Override
        public void handle(final RoutingContext context) {
            Command clone = null;
            try {
                //克隆一份
                clone = pool == null ? null : pool.get();
                if (clone == null) {
                    //构造新对象
                    clone = meta.getTarget();
                }
                //使用环境和当前上下文进行绑定
                //理论上只有新创建的对象才需要使用环境和上下文同时绑定，对象池中的对象已经使用环境绑定过了
                //但是，有可能开发人员在清理的代码里面不小心清理了环境绑定的对象
                //避免出错，所以都重新绑定一次
                Binding.bind(context, clone);
                //验证
                Validates.validate(clone);
                //执行
                Object obj;
                if (handlerName.method != null) {
                    //执行方法
                    obj = handlerName.method.invoke(clone, getArgs(clone, handlerName.method, context));
                } else {
                    obj = clone.execute();
                }
                //处理结果
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

        protected Object[] getArgs(final Command target, final Method method, final RoutingContext context) throws ReflectionException {
            Object[] args = new Object[method.getParameterCount()];
            if (args.length > 0) {
                List<ParameterBinding> bindings = PARAMETERS.computeIfAbsent(method, o -> {
                    Parameter[] parameters = o.getParameters();
                    List<ParameterBinding> result = new ArrayList<>(parameters.length);
                    for (int i = 0; i < parameters.length; i++) {
                        result.add(new ParameterBinding(parameters[i], new ParameterConsumer(args, i)));
                    }

                    return result;
                });
                for (ParameterBinding parameter : bindings) {
                    parameter.bind(context, target);
                }
            }
            return args;
        }

    }

    protected static class HandlerName {
        //命令名称
        protected String handler;
        //方法名称
        protected String path;
        //执行方法
        public Method method;

        public HandlerName(String handler) {
            int pos = handler.indexOf('#');
            if (pos > 0) {
                this.handler = handler;
            } else {
                this.handler = handler.substring(0, pos);
                this.path = handler.substring(pos + 1);
            }
        }

        public String getHandler() {
            return handler;
        }

        public String getPath() {
            return path;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }


    /**
     * 参数值消费者
     */
    protected static class ParameterConsumer implements Consumer {

        protected Object[] args;
        protected int index;

        public ParameterConsumer(final Object[] args, final int index) {
            this.args = args;
            this.index = index;
        }

        @Override
        public void accept(final Object o) {
            args[index] = o;
        }
    }

    /**
     * 绑定字段
     */
    protected static class ParameterBinding {
        //方法参数
        final protected Parameter parameter;
        //值消费者
        final protected Consumer consumer;
        //绑定实现
        final protected List<BinderAnnotation> binders = new ArrayList<>(2);

        public ParameterBinding(final Parameter parameter, final Consumer consumer) {
            this.parameter = parameter;
            this.consumer = consumer;
            Annotation[] annotations = parameter.getDeclaredAnnotations();
            Binder binder;
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    binder = BINDER.get(annotation.annotationType());
                    if (binder != null) {
                        binders.add(new BinderAnnotation(annotation, binder));
                    }
                }
            }
            if (binders.isEmpty()) {
                //TODO 直接根据参数名称绑定请求参数值
            }
        }

        /**
         * 绑定
         *
         * @param source
         * @param target
         * @throws ReflectionException
         */
        public void bind(final Object source, final Object target) throws ReflectionException {
            Binder.Context context;
            for (BinderAnnotation annotation : binders) {
                context = new ParameterContext(source, target, annotation.getAnnotation(), parameter, consumer);
                if (annotation.getBinder().bind(context)) {
                    return;
                }
            }

        }
    }

    /**
     * 方法参数上下文
     */
    protected static class ParameterContext extends Binder.Context {
        //方法参数
        protected Parameter parameter;
        //值消费者
        protected Consumer consumer;

        public ParameterContext(final Object source, final Object target, final Annotation annotation, final Parameter parameter, final Consumer consumer) {
            super(source, target, annotation);
            this.parameter = parameter;
            this.consumer = consumer;
        }

        @Override
        public String getName() {
            return parameter.getName();
        }

        @Override
        public Class<?> getType() {
            return parameter.getType();
        }

        @Override
        public Object evaluate(final String name) throws ReflectionException {
            return ((RoutingContext) source).get(name);
        }

        @Override
        public boolean bind(final Object value) throws ReflectionException {
            return bind(value, null);
        }

        @Override
        public boolean bind(final Object value, final String format) throws ReflectionException {
            try {
                Class<?> type = parameter.getType();
                if (value == null) {
                    //值为空
                    if (type.isPrimitive()) {
                        //基本类型不能为空
                        return false;
                    }
                    consumer.accept(null);
                    return true;
                }
                Class<?> inboxTargetType = inbox(type);
                Class<?> inboxSourceType = inbox(value.getClass());
                MyParameterScope scope = new MyParameterScope(parameter);
                //获取转换器
                Converter operation = CONVERTER.select(new ConversionType(inboxSourceType, inboxTargetType, scope));
                if (operation != null) {
                    //拿到转换器
                    Object obj = operation.execute(new Conversion(inboxSourceType, inboxTargetType, value, format, scope));
                    if (obj != null) {
                        //转换成功
                        consumer.accept(obj);
                        return true;
                    }
                }
                return false;
            } catch (ReflectionException e) {
                throw e;
            } catch (Exception e) {
                throw new ReflectionException(e.getMessage(), e);
            }
        }
    }


    /**
     * 参数作用域
     */
    protected static class MyParameterScope implements Scope {
        //方法
        protected final Parameter parameter;
        //注解
        protected final Annotation[] annotations;
        //泛型
        protected final Class genericType;

        public MyParameterScope(Parameter parameter) {
            this.parameter = parameter;
            this.annotations = parameter.getAnnotations();
            this.genericType = Generics.getGenericType(parameter.getParameterizedType());
        }

        @Override
        public Annotation[] getAnnotations() {
            return annotations;
        }

        @Override
        public Class getGenericType() {
            return genericType;
        }

        @Override
        public Object target() {
            return parameter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MyParameterScope that = (MyParameterScope) o;

            return parameter.equals(that.parameter);
        }

        @Override
        public int hashCode() {
            return parameter.hashCode();
        }
    }
}
