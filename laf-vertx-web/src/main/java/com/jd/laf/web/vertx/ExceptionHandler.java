package com.jd.laf.web.vertx;

import com.jd.laf.extension.Type;
import io.vertx.core.Handler;

/**
 * HTTP连接异常处理器
 */
public interface ExceptionHandler extends Handler<Throwable>, Type {

}
