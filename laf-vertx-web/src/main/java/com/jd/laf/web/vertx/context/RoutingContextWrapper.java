package com.jd.laf.web.vertx.context;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 路由上下文和环境混合属性提供者
 */
public class RoutingContextWrapper implements RoutingContext {
    protected RoutingContext wrapper;
    protected Environment environment;

    public RoutingContextWrapper(RoutingContext wrapper, Environment environment) {
        this.wrapper = wrapper;
        this.environment = environment;
    }

    @Override
    public HttpServerRequest request() {
        return wrapper.request();
    }

    @Override
    public HttpServerResponse response() {
        return wrapper.response();
    }

    @Override
    public void next() {
        wrapper.next();
    }

    @Override
    public void fail(final int statusCode) {
        wrapper.fail(statusCode);
    }

    @Override
    public void fail(final Throwable throwable) {
        wrapper.fail(throwable);
    }

    @Override
    public RoutingContext put(final String key, final Object obj) {
        return wrapper.put(key, obj);
    }

    @Override
    public <T> T get(final String key) {
        //允许从环境里面获取参数
        T result = wrapper.get(key);
        if (result == null) {
            result = environment.getObject(key);
        }
        return result;
    }

    @Override
    public <T> T remove(final String key) {
        return wrapper.remove(key);
    }

    @Override
    public Map<String, Object> data() {
        return wrapper.data();
    }

    @Override
    public Vertx vertx() {
        return wrapper.vertx();
    }

    @Override
    public String mountPoint() {
        return wrapper.mountPoint();
    }

    @Override
    public Route currentRoute() {
        return wrapper.currentRoute();
    }

    @Override
    public String normalisedPath() {
        return wrapper.normalisedPath();
    }

    @Override
    public Cookie getCookie(final String name) {
        return wrapper.getCookie(name);
    }

    @Override
    public RoutingContext addCookie(final Cookie cookie) {
        return wrapper.addCookie(cookie);
    }

    @Override
    public Cookie removeCookie(final String name) {
        return wrapper.removeCookie(name);
    }

    @Override
    public int cookieCount() {
        return wrapper.cookieCount();
    }

    @Override
    public Set<Cookie> cookies() {
        return wrapper.cookies();
    }

    @Override
    public String getBodyAsString() {
        return wrapper.getBodyAsString();
    }

    @Override
    public String getBodyAsString(final String encoding) {
        return wrapper.getBodyAsString(encoding);
    }

    @Override
    public JsonObject getBodyAsJson() {
        return wrapper.getBodyAsJson();
    }

    @Override
    public JsonArray getBodyAsJsonArray() {
        return wrapper.getBodyAsJsonArray();
    }

    @Override
    public Buffer getBody() {
        return wrapper.getBody();
    }

    @Override
    public Set<FileUpload> fileUploads() {
        return wrapper.fileUploads();
    }

    @Override
    public Session session() {
        return wrapper.session();
    }

    @Override
    public User user() {
        return wrapper.user();
    }

    @Override
    public Throwable failure() {
        return wrapper.failure();
    }

    @Override
    public int statusCode() {
        return wrapper.statusCode();
    }

    @Override
    public String getAcceptableContentType() {
        return wrapper.getAcceptableContentType();
    }

    @Override
    public ParsedHeaderValues parsedHeaders() {
        return wrapper.parsedHeaders();
    }

    @Override
    public int addHeadersEndHandler(final Handler<Void> handler) {
        return wrapper.addHeadersEndHandler(handler);
    }

    @Override
    public boolean removeHeadersEndHandler(final int handlerID) {
        return wrapper.removeHeadersEndHandler(handlerID);
    }

    @Override
    public int addBodyEndHandler(final Handler<Void> handler) {
        return wrapper.addBodyEndHandler(handler);
    }

    @Override
    public boolean removeBodyEndHandler(final int handlerID) {
        return wrapper.removeBodyEndHandler(handlerID);
    }

    @Override
    public boolean failed() {
        return wrapper.failed();
    }

    @Override
    public void setBody(final Buffer body) {
        wrapper.setBody(body);
    }

    @Override
    public void setSession(final Session session) {
        wrapper.setSession(session);
    }

    @Override
    public void setUser(User user) {
        wrapper.setUser(user);
    }

    @Override
    public void clearUser() {
        wrapper.clearUser();
    }

    @Override
    public void setAcceptableContentType(final String contentType) {
        wrapper.setAcceptableContentType(contentType);
    }

    @Override
    public void reroute(final HttpMethod method, final String path) {
        wrapper.reroute(method, path);
    }

    @Override
    public List<Locale> acceptableLocales() {
        return wrapper.acceptableLocales();
    }

    @Override
    public Map<String, String> pathParams() {
        return wrapper.pathParams();
    }

    @Override
    public String pathParam(final String name) {
        return wrapper.pathParam(name);
    }

    @Override
    public MultiMap queryParams() {
        return wrapper.queryParams();
    }

    @Override
    public List<String> queryParam(final String query) {
        return wrapper.queryParam(query);
    }
}