package com.jd.laf.web.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(VertxUnitRunner.class)
public class RoutingVerticleTest {

    protected static final Logger logger = Logger.getLogger(RoutingVerticleTest.class.getName());

    protected static Vertx vertx;
    protected static WebClient client;

    @BeforeClass
    public static void beforeClass(TestContext context) throws IOException, JAXBException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(RoutingVerticle.class.getName(),
                context.asyncAssertSuccess());
        client = WebClient.create(vertx);
    }

    @AfterClass
    public static void afterClass(TestContext context) {
        if (vertx != null) {
            vertx.close(context.asyncAssertSuccess());
        }
    }

    @Test
    public void testConfig() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        client.get(8080, "localhost", "/hello").send(a -> {
            if (a.succeeded()) {
                HttpResponse<Buffer> response = a.result();
                System.out.println(response.bodyAsString());
            } else {
                logger.log(Level.SEVERE, "failed", a.cause());
            }
            latch.countDown();
        });
        latch.await();
    }

}
