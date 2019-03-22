package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import com.jd.laf.binding.marshaller.Marshaller;
import com.jd.laf.web.vertx.annotation.Body;
import com.jd.laf.web.vertx.marshaller.JacksonProvider;
import com.jd.laf.web.vertx.query.QPageQuery;
import com.jd.laf.web.vertx.query.QUser;
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
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BindingTest {

    @Test
    public void testMethodBinding() throws Exception {


        Marshaller marshaller = new JacksonProvider().getMarshaller();

        QPageQuery<QUser> qPageQuery = new QPageQuery<QUser>(1, 10, new QUser("he"));
        final String value = marshaller.marshall(qPageQuery);
        Method method = BindingTest.class.getDeclaredMethod("annotation", QPageQuery.class);
        Object[] args = Binding.bind(new MyRoutingContext(value), this, method);
        Assert.assertTrue(args.length == 1);


        UserModel userModel = new UserModel(1, "he");
        final String value1 = marshaller.marshall(qPageQuery);
        UserService userService = new UserService();
        Method[] methods = UserService.class.getMethods();
        for (Method m : methods) {
            if (m.getName().equals("add")) {
                method = m;
                break;
            }
        }
        args = Binding.bind(new MyRoutingContext(value1), userService, method);

        Assert.assertTrue(args.length == 1);
    }


    protected void annotation(@Body QPageQuery<QUser> query) {

    }


    public static class BaseModel {

        protected long id;

        public BaseModel() {
        }

        public BaseModel(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class UserModel extends BaseModel {

        protected String name;

        public UserModel() {
        }

        public UserModel(long id, String name) {
            super(id);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class BaseService<M extends BaseModel> {

        public void add(@Body M model) throws Exception {

        }
    }

    public static class UserService extends BaseService<UserModel> {

    }

    public static class MyRoutingContext implements RoutingContext {
        private final String value;

        public MyRoutingContext(String value) {
            this.value = value;
        }

        @Override
        public HttpServerRequest request() {
            return null;
        }

        @Override
        public HttpServerResponse response() {
            return null;
        }

        @Override
        public void next() {

        }

        @Override
        public void fail(int statusCode) {

        }

        @Override
        public void fail(Throwable throwable) {

        }

        @Override
        public void fail(int statusCode, Throwable throwable) {

        }

        @Override
        public RoutingContext put(String key, Object obj) {
            return null;
        }

        @Override
        public <T> T get(String key) {
            return null;
        }

        @Override
        public <T> T remove(String key) {
            return null;
        }

        @Override
        public Map<String, Object> data() {
            return null;
        }

        @Override
        public Vertx vertx() {
            return null;
        }

        @Override
        public String mountPoint() {
            return null;
        }

        @Override
        public Route currentRoute() {
            return null;
        }

        @Override
        public String normalisedPath() {
            return null;
        }

        @Override
        public Cookie getCookie(String name) {
            return null;
        }

        @Override
        public RoutingContext addCookie(Cookie cookie) {
            return null;
        }

        @Override
        public Cookie removeCookie(String name, boolean invalidate) {
            return null;
        }

        @Override
        public int cookieCount() {
            return 0;
        }

        @Override
        public Set<Cookie> cookies() {
            return null;
        }

        @Override
        public String getBodyAsString() {
            return value;
        }

        @Override
        public String getBodyAsString(String encoding) {
            return value;
        }

        @Override
        public JsonObject getBodyAsJson() {
            return null;
        }

        @Override
        public JsonArray getBodyAsJsonArray() {
            return null;
        }

        @Override
        public Buffer getBody() {
            return null;
        }

        @Override
        public Set<FileUpload> fileUploads() {
            return null;
        }

        @Override
        public Session session() {
            return null;
        }

        @Override
        public User user() {
            return null;
        }

        @Override
        public Throwable failure() {
            return null;
        }

        @Override
        public int statusCode() {
            return 0;
        }

        @Override
        public String getAcceptableContentType() {
            return null;
        }

        @Override
        public ParsedHeaderValues parsedHeaders() {
            return null;
        }

        @Override
        public int addHeadersEndHandler(Handler<Void> handler) {
            return 0;
        }

        @Override
        public boolean removeHeadersEndHandler(int handlerID) {
            return false;
        }

        @Override
        public int addBodyEndHandler(Handler<Void> handler) {
            return 0;
        }

        @Override
        public boolean removeBodyEndHandler(int handlerID) {
            return false;
        }

        @Override
        public boolean failed() {
            return false;
        }

        @Override
        public void setBody(Buffer body) {

        }

        @Override
        public void setSession(Session session) {

        }

        @Override
        public void setUser(User user) {

        }

        @Override
        public void clearUser() {

        }

        @Override
        public void setAcceptableContentType(String contentType) {

        }

        @Override
        public void reroute(HttpMethod method, String path) {

        }

        @Override
        public List<Locale> acceptableLocales() {
            return null;
        }

        @Override
        public Map<String, String> pathParams() {
            return null;
        }

        @Override
        public String pathParam(String name) {
            return null;
        }

        @Override
        public MultiMap queryParams() {
            return null;
        }

        @Override
        public List<String> queryParam(String query) {
            return null;
        }
    }
}
