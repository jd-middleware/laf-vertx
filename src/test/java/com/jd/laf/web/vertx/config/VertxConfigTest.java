package com.jd.laf.web.vertx.config;

import com.jd.laf.web.vertx.RenderHandler;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class VertxConfigTest {

    @Test
    public void testXml() throws JAXBException {
        VertxConfig config = new VertxConfig();
        config.getProduces().add("application/json");
        config.getProduces().add("application/xml");
        config.getConsumes().add("application/json");
        RouteConfig route = new RouteConfig();
        route.setType(RouteType.GET);
        route.setPath("/test");
        route.add("addApplication");
        route.add(RenderHandler.RENDER);
        route.getProduces().add("application/json");
        config.add(route);
        JAXBContext context = JAXBContext.newInstance(RouteConfig.class, VertxConfig.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(config, writer);
        String xml = writer.toString();
        System.out.println(xml);

        VertxConfig target = VertxConfig.Builder.build(new StringReader(xml));
        Assert.assertTrue(target.getProduces().contains("application/json"));
        Assert.assertTrue(target.getProduces().contains("application/xml"));
        Assert.assertEquals(target.getRoutes().get(0).getPath(), "/test");

    }
}
