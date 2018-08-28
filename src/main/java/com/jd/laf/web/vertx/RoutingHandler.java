package com.jd.laf.web.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 */
public interface RoutingHandler extends Handler<RoutingContext> {

    /**
     * WEB根路径
     */
    String WEB_ROOT = "web.root";

    /**
     * 默认页
     */
    String INDEX_PAGE = "web.indexPage";

    /**
     * Body大小限制
     */
    String BODY_LIMIT = "web.bodyLimit";

    /**
     * 上传目录
     */
    String UPLOAD_DIR = "web.uploadDir";

    /**
     * 验证器
     */
    String VALIDATOR = "validator";

    /**
     * 类型
     *
     * @return
     */
    String type();

}
