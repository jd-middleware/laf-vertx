package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpConnection;

/**
 * 连接处理器
 */
public interface ConnectionHandler extends Handler<HttpConnection>, Type {

}
