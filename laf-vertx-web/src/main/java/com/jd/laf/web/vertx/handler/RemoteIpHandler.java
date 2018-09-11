package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.SystemContext.REMOTE_IP;

/**
 * 获取远程IP
 */
public class RemoteIpHandler implements RoutingHandler {
    @Override
    public String type() {
        return "ip";
    }

    @Override
    public void handle(final RoutingContext context) {
        //获取远程IP
        context.put(REMOTE_IP, getRemoteIP(context.request()));
        context.next();
    }

    /**
     * 获取远程ID
     *
     * @param request 远程IP
     * @return
     */
    public static String getRemoteIP(final HttpServerRequest request) {
        String ip = request.getHeader("J-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        } else {
            ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.remoteAddress().host();
            }
            if (ip != null) {
                int pos = ip.indexOf(',');
                if (pos > 0) {
                    ip = ip.substring(0, pos);
                }
            }
            return ip;
        }
    }

}
