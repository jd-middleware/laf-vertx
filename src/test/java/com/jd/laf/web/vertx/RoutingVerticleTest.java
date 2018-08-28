package com.jd.laf.web.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class RoutingVerticleTest {

    protected static Vertx vertx;

    @BeforeClass
    public static void beforeClass(TestContext context) throws IOException, JAXBException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(RoutingVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @AfterClass
    public static void afterClass(TestContext context) {
        if (vertx != null) {
            vertx.close(context.asyncAssertSuccess());
        }
    }

    @Test
    public void testConfig() {
//        RouteConfig cfg = config.getRoutes().get(5);
//        Assert.assertEquals(cfg.getHandlers().size(), 3);
//        Assert.assertArrayEquals(cfg.getHandlers().toArray(), new String[]{"body", "helloWorld", "render"});
    }

}
