package com.jd.laf.web.vertx;

import com.jd.laf.web.vertx.config.RouteConfig;
import com.jd.laf.web.vertx.config.VertxConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class RoutingVerticleTest {

    protected static VertxConfig config;

    @BeforeClass
    public static void beforeClass() throws IOException, JAXBException {
        config = RoutingVerticle.buildConfig("routing.xml");
    }

    @Test
    public void testConfig() {
        RouteConfig cfg = config.getRoutes().get(5);
        Assert.assertEquals(cfg.getHandlers().size(), 3);
        Assert.assertArrayEquals(cfg.getHandlers().toArray(), new String[]{"body", "helloWorld", "render"});
    }

}
