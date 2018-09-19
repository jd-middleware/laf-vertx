package com.jd.laf.web.vertx.handler;

import com.jd.laf.binding.annotation.Value;
import com.jd.laf.codec.Base64;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.response.Response.HTTP_MOVED_TEMP;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by yangyang36 on 2018/9/18.
 */
public class SsoLogoutAfterHandler implements RoutingHandler {

    @Value("sso.login.url")
    protected String ssoLoginUrl = "http://ssa.jd.com/sso/login";
    @Value("app.logout.url")
    protected String appLogoutUrl;
    @Value("app.index.url")
    protected String appIndexUrl;

    @Override
    public String type() {
        return "ssoLogoutAfter";
    }

    @Override
    public void handle(RoutingContext context) {
        redirect2Login(context);
    }

    /**
     * 重定向登录页面
     *
     * @param context
     */
    protected void redirect2Login(final RoutingContext context) {
        String url = appLogoutUrl;
        if (url == null || url.isEmpty()) {
            url = ssoLoginUrl;
            if (appIndexUrl != null && !appIndexUrl.isEmpty()) {
                url = url + "?ReturnUrl=" + Base64.encode(appIndexUrl.getBytes(UTF_8));
            }
        }
        context.response().putHeader(HttpHeaders.LOCATION, url).
                setStatusCode(HTTP_MOVED_TEMP).end("Redirecting to " + url + ".");
    }
}
