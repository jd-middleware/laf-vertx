package com.jd.laf.web.vertx;

import io.vertx.core.http.impl.HttpUtils;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    public static final int count = 10000000;
    public static final String URL = "/v1/namespace/hexiaofeng/config/test/profiles/test";

    @Test
    public void testNormalizePath() {
        String[] urls = new String[]{
                "www.jd.com/abcd/../acd%41",
                "www.jd.com/abcd/..",
                "www.jd.com/abcd//",
                "www.jd.com/abcd/../..",
                "www.jd.com/abcd/../..a",
                "www.jd.com/abcd/%2E./acd%41",
                "a/..",
                "a/.",
                ".",
                "..",
                "%2F/a",
                "/a/c/..",
                "/.%8E"
        };

        for (String url : urls) {
            Assert.assertEquals(HttpUtils.normalizePath(url), HttpUtils.normalize(url));
        }

        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            HttpUtils.normalizePath(URL);
        }
        System.out.println("tps:" + count * 1000 / (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            HttpUtils.normalize(URL);
        }
        System.out.println("tps:" + count * 1000 / (System.currentTimeMillis() - time));

    }


}
