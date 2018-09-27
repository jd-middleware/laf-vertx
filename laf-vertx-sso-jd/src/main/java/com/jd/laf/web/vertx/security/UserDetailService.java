package com.jd.laf.web.vertx.security;

/**
 * 用户服务
 */
public interface UserDetailService<M extends UserDetail> {

    /**
     * 添加用户
     *
     * @param detail
     */
    M addUser(M detail);

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    M findById(long id);

    /**
     * 根据代码查找
     *
     * @param code
     * @return
     */
    M findByCode(String code);
}
