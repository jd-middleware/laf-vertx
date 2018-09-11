package com.jd.laf.web.vertx.security;

/**
 * 用户提供者
 */
public interface UserDetailProvider {

    /**
     * 创建用户
     *
     * @return
     */
    UserDetail create();

    /**
     * 获取用户服务
     *
     * @return
     */
    UserDetailService service();
}
