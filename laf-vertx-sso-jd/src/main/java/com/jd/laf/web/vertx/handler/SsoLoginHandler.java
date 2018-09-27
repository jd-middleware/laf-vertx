package com.jd.laf.web.vertx.handler;

import com.jd.laf.binding.annotation.Value;
import com.jd.laf.web.vertx.security.UserDetail;
import com.jd.laf.web.vertx.security.UserDetailProvider;
import com.jd.laf.web.vertx.security.UserDetailService;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.exception.SsoException;
import com.jd.ssa.service.SsoService;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.impl.HttpStatusException;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.jd.laf.web.vertx.Environment.REMOTE_IP;
import static com.jd.laf.web.vertx.Environment.USER_KEY;
import static com.jd.laf.web.vertx.response.Response.HTTP_INTERNAL_ERROR;

/**
 * 单点登录
 */
public class SsoLoginHandler extends RemoteIpHandler {

    //单点登录的cookie名称
    @Value(value = "sso.cookie.name", defaultValue = "sso.jd.com")
    protected String ssoCookieName;
    @Value(value = "sso.login.url", defaultValue = "http://ssa.jd.com/sso/login")
    protected String ssoLoginUrl;
    //应用域名
    @Value("app.domain.name")
    protected String appDomainName;
    @Value(value = "user.session.key", defaultValue = "userDetail")
    @NotEmpty
    protected String userSessionKey;
    @Value(value = "sso.redirect.status", defaultValue = "401")
    protected int ssoRedirectStatus;
    @Value(value = "sso.redirect.returnUrl", defaultValue = "false")
    protected boolean ssoRedirectReturnUrl;
    @Value
    @NotNull
    protected SsoService ssoService;
    @Value
    @NotNull
    protected UserDetailProvider userDetailProvider;

    @Override
    public String type() {
        return "ssoLogin";
    }

    @Override
    public void handle(final RoutingContext context) {
        HttpServerRequest request = context.request();
        Session session = context.session();
        if (session == null) {
            context.fail(new HttpStatusException(HTTP_INTERNAL_ERROR, "No session - did you forget to include a SessionHandler?"));
            return;
        }
        String remoteIP = getRemoteIP(request);
        context.put(REMOTE_IP, remoteIP);
        String domain = appDomainName == null || appDomainName.isEmpty() ? request.host() : appDomainName;
        try {
            UserDetail userDetail = session.get(userSessionKey);
            if (userDetail == null) {
                //获取单点登录的cookie
                Cookie cookie = context.getCookie(ssoCookieName);
                String ticket = cookie == null ? null : cookie.getValue();
                if (ticket != null && !ticket.isEmpty()) {
                    //session已经做了缓存，直接从服务获取
                    UserInfo userInfo = ssoService.verifyTicket(ticket, domain, remoteIP);
                    userDetail = userDetailProvider.create();
                    userDetail.setCode(userInfo.getUsername());
                    userDetail.setName(userInfo.getFullname());
                    userDetail.setEmail(userInfo.getEmail());
                    userDetail.setMobile(userInfo.getMobile());
                    userDetail.setOrgId(userInfo.getOrgId());
                    userDetail.setOrgName(userInfo.getOrgName());
                    //系统中查找用户
                    UserDetailService detailService = userDetailProvider.service();
                    UserDetail exists = detailService.findByCode(userDetail.getCode());
                    if (exists == null) {
                        //新用户，添加到系统中
                        userDetail = detailService.addUser(userDetail);
                    } else {
                        //老用户，获取用户的ID以及角色
                        userDetail.setId(exists.getId());
                        userDetail.setRole(exists.getRole());
                    }
                    //添加到cookie中
                    session.put(userSessionKey, userDetail);
                } else {
                    //没有单点登录信息，重新定向到登录页面
                    redirect2Login(context);
                    return;
                }
            }
            //存放用户上下文信息
            context.put(USER_KEY, userDetail);
        } catch (SsoException e) {
            //单独登录认证错误，重新定向到登录页面
            redirect2Login(context);
        } catch (Exception e) {
            context.fail(e);
        }
        context.next();
    }

    /**
     * 重定向登录页面
     *
     * @param context
     */
    protected void redirect2Login(final RoutingContext context) {
        String url = ssoLoginUrl;
        //带上ReturnUrl
        if (ssoRedirectReturnUrl) {
            url = url + "?ReturnUrl=" + context.request().absoluteURI();
        }
        context.response().putHeader(HttpHeaders.LOCATION, url).
                setStatusCode(ssoRedirectStatus).end("Redirecting to " + url + ".");
    }

}