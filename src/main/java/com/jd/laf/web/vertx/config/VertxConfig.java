package com.jd.laf.web.vertx.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 配置
 */
@XmlRootElement(name = "vertx")
@XmlAccessorType(XmlAccessType.NONE)
public class VertxConfig {
    //路由处理器
    @XmlElementWrapper
    @XmlElement(name = "route")
    List<RouteConfig> routes = new ArrayList<>(50);
    //异常处理器
    @XmlElementWrapper
    @XmlElement(name = "error")
    List<RouteConfig> errors = new ArrayList<>(1);
    //消息处理器
    @XmlElementWrapper
    @XmlElement(name = "messages")
    List<RouteConfig> messages = new ArrayList<>(10);

    public VertxConfig() {
    }

    public List<RouteConfig> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteConfig> routes) {
        this.routes = routes;
    }

    public List<RouteConfig> getErrors() {
        return errors;
    }

    public void setErrors(List<RouteConfig> errors) {
        this.errors = errors;
    }

    public List<RouteConfig> getMessages() {
        return messages;
    }

    public void setMessages(List<RouteConfig> messages) {
        this.messages = messages;
    }

    /**
     * 添加路由处理器配制
     *
     * @param route 路由处理器配制
     */
    public void add(final RouteConfig route) {
        if (route != null) {
            if (RouteType.MSG == route.getType()) {
                messages.add(route);
            } else if (RouteType.ERROR == route.getType()) {
                errors.add(route);
            } else {
                routes.add(route);
            }
        }
    }

    /**
     * 构造器
     */
    public static class Builder {

        /**
         * 构造
         *
         * @param reader
         * @return
         * @throws IOException
         */
        public static VertxConfig build(final Reader reader) throws JAXBException {
            JAXBContext context = JAXBContext.newInstance(RouteConfig.class, RouteType.class, VertxConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (VertxConfig) unmarshaller.unmarshal(reader);
        }

    }

}
