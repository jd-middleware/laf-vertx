package com.jd.laf.web.vertx.handler;

import com.jd.laf.binding.annotation.Value;
import com.jd.laf.codec.Base64;
import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import com.jd.laf.web.vertx.security.UserDetail;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.Environment.USER_KEY;
import static com.jd.laf.web.vertx.response.Response.HTTP_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by yangyang36 on 2018/9/18.
 */
public class SsoLoginAfterHandler implements RoutingHandler, EnvironmentAware {

    @Value(value = "sso.login.url", defaultValue = "http://ssa.jd.com/sso/login")
    protected String ssoLoginUrl;

    @Override
    public void setup(Environment environment) {

    }

    @Override
    public String type() {
        return "ssoLoginAfter";
    }

    @Override
    public void handle(RoutingContext context) {
        UserDetail userDetail = context.get(USER_KEY);
        if (userDetail == null) {
            redirect2Login(context);
        }
        context.next();
    }

    /**
     * 重定向登录页面
     *
     * @param context
     */
    protected void redirect2Login(final RoutingContext context) {
        String url = ssoLoginUrl + "?ReturnUrl=" + Base64.encode(context.request().uri().getBytes(UTF_8));
        context.response().putHeader(HttpHeaders.LOCATION, url).
                setStatusCode(HTTP_UNAUTHORIZED).end("Redirecting to " + url + ".");
    }
}
