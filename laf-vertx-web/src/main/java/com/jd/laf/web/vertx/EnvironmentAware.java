package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import io.vertx.core.Vertx;

/**
 * 系统上下文感知
 */
public interface EnvironmentAware {

    /**
     * 通过上下文来构建，单线程调用
     *
     * @param vertx
     * @param environment 环境
     * @throws Exception
     */
    void setup(Vertx vertx, Environment environment) throws Exception;

    /**
     * 绑定并且验证
     *
     * @param vertx
     * @param environment 环境
     * @param target      对象
     * @throws Exception
     */
    static <T> T setup(final Vertx vertx, final Environment environment, final T target) throws Exception {
        if (target != null) {
            //绑定对象
            Binding.bind(environment, target);
            //验证对象绑定
            Validates.validate(target);
            //调用初始化方法
            if (target instanceof EnvironmentAware) {
                ((EnvironmentAware) target).setup(vertx, environment);
            }
        }
        return target;
    }

    /**
     * 绑定并且验证
     *
     * @param vertx
     * @param environment 环境
     * @param targets     对象
     * @throws Exception
     */
    static <T> void setup(final Vertx vertx, final Environment environment, final Iterable<T> targets) throws Exception {
        if (targets != null) {
            for (T target : targets) {
                setup(vertx, environment, target);
            }
        }
    }
}