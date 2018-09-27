package com.jd.laf.web.vertx.security;

/**
 * 用户提供者
 */
public interface UserDetailProvider<M extends UserDetail> {

    /**
     * 创建用户
     *
     * @return
     */
    M create();

    /**
     * 获取用户服务
     *
     * @return
     */
    UserDetailService<M> service();
}
