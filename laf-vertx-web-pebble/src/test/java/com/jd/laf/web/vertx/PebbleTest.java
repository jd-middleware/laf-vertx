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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(VertxUnitRunner.class)
public class PebbleTest {

    protected static final Logger logger = Logger.getLogger(PebbleTest.class.getName());

    protected static Vertx vertx;
    protected static WebClient client;

    @BeforeClass
    public static void beforeClass(TestContext context) throws IOException, JAXBException {

        vertx = Vertx.vertx();
        client = WebClient.create(vertx);

        Map<String, Object> map = new HashMap<>();
        map.put(SystemContext.TEMPLATE_TYPE, "pebble");
        RoutingVerticle verticle = new RoutingVerticle();
        verticle.setParameters(map);

        vertx.deployVerticle(verticle, context.asyncAssertSuccess());
    }

    @AfterClass
    public static void afterClass(TestContext context) {
        if (vertx != null) {
            vertx.close(context.asyncAssertSuccess());
        }
    }

    @Test
    public void testPeeble() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.get(8080, "localhost", "/hello1?echo=1234567").
                putHeader("Accept", "text/html").send(a -> {
            if (a.succeeded()) {
                HttpResponse<Buffer> response = a.result();
                result.set(response.bodyAsString());
            } else {
                logger.log(Level.SEVERE, "failed", a.cause());
            }
            latch.countDown();
        });
        latch.await();
        System.out.println(result.get());
    }

}
