package com.jd.laf.web.vertx.service;

import com.jd.laf.web.vertx.Environment;

/**
 * 服务程序
 */
public interface Daemon {
    /**
     * 启动
     *
     * @param context
     * @throws Exception
     */
    void start(Environment context) throws Exception;

    /**
     * 停止
     */
    void stop();
}
