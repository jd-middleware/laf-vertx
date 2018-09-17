package com.jd.laf.web.vertx.security;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.shareddata.impl.ClusterSerializable;

import java.nio.charset.StandardCharsets;

/**
 * 用户详情接口
 */
public interface UserDetail extends ClusterSerializable {

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

    @Override
    default void writeToBuffer(final Buffer buffer) {
        byte[] nameBytes = getName().getBytes(StandardCharsets.UTF_8);
        buffer.appendLong(getId()).appendInt(getRole()).appendInt(nameBytes.length).appendBytes(nameBytes);
    }

    @Override
    default int readFromBuffer(final int pos, final Buffer buffer) {
        setId(buffer.getLong(pos));
        setRole(buffer.getInt(pos + 8));
        int length = buffer.getInt(pos + 12);
        byte[] bytes = buffer.getBytes(pos + 16, pos + 16 + length);
        setName(new String(bytes, StandardCharsets.UTF_8));
        return pos + 16 + length;
    }
}
