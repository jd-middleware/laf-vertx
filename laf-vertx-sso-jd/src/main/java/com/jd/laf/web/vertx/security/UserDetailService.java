package com.jd.laf.web.vertx.security;

/**
 * 用户服务
 */
public interface UserDetailService {

    /**
     * 添加应用
     *
     * @param detail
     */
    UserDetail addUser(UserDetail detail);

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    UserDetail findById(long id);

    /**
     * 根据代码查找
     *
     * @param code
     * @return
     */
    UserDetail findByCode(String code);
}
