package com.jd.laf.web.vertx;

import com.jd.laf.web.vertx.config.RouteConfig;

import java.util.List;

/**
 * 路由提供者，便于从数据库等地方获取路由配置信息
 */
public interface RouteProvider {

    /**
     * 获取路由信息
     *
     * @return
     */
    List<RouteConfig> getRoutes();
}
