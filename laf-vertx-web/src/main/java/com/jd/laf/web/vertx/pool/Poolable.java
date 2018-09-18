package com.jd.laf.web.vertx.pool;

/**
 * 对象可池化接口
 */
public interface Poolable {

    /**
     * 清理数据
     */
    void clean();
}
