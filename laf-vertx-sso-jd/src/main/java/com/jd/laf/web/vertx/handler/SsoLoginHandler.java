package com.jd.laf.web.vertx.handler;

import com.jd.laf.binding.annotation.Value;
import com.jd.laf.codec.Base64;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.security.UserDetail;
import com.jd.laf.web.vertx.security.UserDetailProvider;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.exception.SsoException;
import com.jd.ssa.service.SsoService;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import io.vertx.ext.web.impl.ConcurrentLRUCache;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.concurrent.ConcurrentMap;

import static com.jd.laf.web.vertx.Environment.REMOTE_IP;
import static com.jd.laf.web.vertx.Environment.USER_KEY;
import static com.jd.laf.web.vertx.response.Response.HTTP_INTERNAL_ERROR;
import static com.jd.laf.web.vertx.response.Response.HTTP_MOVED_TEMP;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 单点登录
 */
public class SsoLoginHandler extends RemoteIpHandler implements EnvironmentAware {

    @Value(value = "sso.cache.size", defaultValue = "10000")
    @Positive
    protected int ssoCacheSize;
    //单点登录的cookie名称

    @Value(value = "sso.cookie.name", defaultValue = "sso.jd.com")
    protected String ssoCookieName;

    @Value(value = "sso.login.url", defaultValue = "http://ssa.jd.com/sso/login")
    protected String ssoLoginUrl;

    //应用域名
    @Value("app.domain.name")
    protected String appDomainName;

    @Value
    @NotNull
    protected SsoService ssoService;

    @Value
    @NotNull
    protected UserDetailProvider userDetailProvider;

    @Value(value = "user.session.key", defaultValue = "userDetail")
    @NotEmpty
    protected String userSessionKey;

    private ConcurrentMap<String, UserInfo> cache;

    @Override
    public String type() {
        return "ssoLogin";
    }

    @Override
    public void setup(final Environment environment) {
        cache = new ConcurrentLRUCache(ssoCacheSize);
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
                    //从缓存获取
                    UserInfo userInfo = getFromCache(ticket);
                    if (userInfo == null) {
                        //从服务获取
                        userInfo = ssoService.verifyTicket(ticket, domain, remoteIP);
                    }
                    userDetail = userDetailProvider.create();
                    userDetail.setCode(userInfo.getUsername());
                    userDetail.setName(userInfo.getFullname());
                    userDetail.setEmail(userInfo.getEmail());
                    userDetail.setMobile(userInfo.getMobile());
                    userDetail.setOrgId(userInfo.getOrgId());
                    userDetail.setOrgName(userInfo.getOrgName());
                    userDetail = userDetailProvider.service().addUser(userDetail);
                    //添加到cookie中
                    CookieUser cookieUser = new CookieUser(userDetail.getId(), userDetail.getName(), userDetail.getRole());
                    session.put(userSessionKey, cookieUser);
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
    }

    /**
     * 重定向登录页面
     *
     * @param context
     */
    protected void redirect2Login(final RoutingContext context) {
        String url = ssoLoginUrl + "?ReturnUrl=" + Base64.encode(context.request().uri().getBytes(UTF_8));
        context.response().putHeader(HttpHeaders.LOCATION, url).
                setStatusCode(HTTP_MOVED_TEMP).end("Redirecting to " + url + ".");
    }

    protected UserInfo getFromCache(final String ticket) {
        if (cache == null) {
            return null;
        } else {
            UserInfo user = cache.get(ticket);
            if (user != null && System.currentTimeMillis() > user.getExpire()) {
                cache.remove(ticket);
                return null;
            } else {
                return user;
            }
        }
    }

    protected static class CookieUser implements UserDetail {

        protected long id;

        protected String name;

        protected int role;

        public CookieUser() {
        }

        public CookieUser(long id, String name, int role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public void setId(long id) {
            this.id = id;
        }

        @Override
        public String getCode() {
            return null;
        }

        @Override
        public void setCode(String code) {

        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getOrgId() {
            return null;
        }

        @Override
        public void setOrgId(String orgId) {

        }

        @Override
        public String getOrgName() {
            return null;
        }

        @Override
        public void setOrgName(String orgName) {

        }

        @Override
        public String getEmail() {
            return null;
        }

        @Override
        public void setEmail(String email) {

        }

        @Override
        public String getMobile() {
            return null;
        }

        @Override
        public void setMobile(String mobile) {

        }

        @Override
        public int getRole() {
            return role;
        }

        @Override
        public void setRole(int role) {
            this.role = role;
        }
    }

}
