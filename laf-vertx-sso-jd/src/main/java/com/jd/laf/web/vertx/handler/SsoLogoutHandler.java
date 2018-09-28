package com.jd.laf.web.vertx.handler;

import com.jd.laf.binding.annotation.Value;
import com.jd.laf.web.vertx.RoutingHandler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import javax.validation.constraints.NotEmpty;

import static com.jd.laf.web.vertx.Environment.USER_KEY;

/**
 * 单点注销
 */
public class SsoLogoutHandler implements RoutingHandler {

    //单点登录的cookie名称
    @Value("sso.cookie.name")
    protected String ssoCookieName = "sso.jd.com";
    @Value("sso.login.url")
    protected String ssoLoginUrl = "http://ssa.jd.com/sso/login";
    @Value("app.logout.url")
    protected String appLogoutUrl;
    @Value("app.index.url")
    protected String appIndexUrl;
    @Value(value = "sso.redirect.status", defaultValue = "401")
    protected int ssoRedirectStatus;
    @Value(value = "user.session.key", defaultValue = "userDetail")
    protected String userSessionKey;

    @Override
    public String type() {
        return "ssoLogout";
    }

    @Override
    public void handle(final RoutingContext context) {
        //删除用户
        Session session = context.session();
        if (session != null) {
            session.remove(USER_KEY);
            session.remove(userSessionKey);
        }
        //清理单点登录cookie
        context.removeCookie(ssoCookieName);
        //重定向到注销页面
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
                url = url + "?ReturnUrl=" + appIndexUrl;
            }
        }
        context.response().putHeader(HttpHeaders.LOCATION, url).
                setStatusCode(ssoRedirectStatus).end("Redirecting to " + url + ".");
    }
}