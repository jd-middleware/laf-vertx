package com.jd.laf.web.vertx.security;

/**
 * 用户详情接口
 */
public interface UserDetail {

    /**
     * 获取用户ID
     *
     * @return
     */
    long getId();

    /**
     * 设置用户ID
     *
     * @param id
     */
    void setId(long id);

    /**
     * 获取用户ERP
     *
     * @return
     */
    String getCode();

    /**
     * 设置用户ERP
     *
     * @param code
     */
    void setCode(String code);

    /**
     * 获取用户名称
     *
     * @return
     */
    String getName();

    /**
     * 设置用户名称
     *
     * @param name
     */
    void setName(String name);

    /**
     * 获取组织ID
     *
     * @return
     */
    String getOrgId();

    /**
     * 设置组织ID
     *
     * @param orgId
     */
    void setOrgId(String orgId);

    /**
     * 获取组织名称
     *
     * @return
     */
    String getOrgName();

    /**
     * 设置组织名称
     *
     * @param orgName
     */
    void setOrgName(String orgName);

    /**
     * 获取邮件
     *
     * @return
     */
    String getEmail();

    /**
     * 设置邮件
     *
     * @param email
     */
    void setEmail(String email);

    /**
     * 获取移动电话
     *
     * @return
     */
    String getMobile();

    /**
     * 设置移动电话
     *
     * @param mobile
     */
    void setMobile(String mobile);

    /**
     * 获取角色
     *
     * @return
     */
    int getRole();

    /**
     * 设置角色
     *
     * @param role
     */
    void setRole(int role);
}
