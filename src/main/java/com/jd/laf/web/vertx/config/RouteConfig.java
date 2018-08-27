package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 配置的路由信息
 */
@XmlType(name = "route")
@XmlAccessorType(XmlAccessType.NONE)
public class RouteConfig {
    //路径
    @XmlAttribute
    private String path;
    //方法
    @XmlAttribute
    private RouteType type;
    //支持的消费内容
    @XmlAttribute
    private Set<String> consumes = new HashSet<>();
    //支持的生产内容
    @XmlAttribute
    private Set<String> produces = new HashSet<>();
    //处理器
    @XmlAttribute
    @XmlList
    private List<String> handlers = new ArrayList<>(5);

    public RouteConfig() {

    }

    public RouteConfig(RouteType type) {
        this.type = type;
    }

    public RouteConfig(RouteType type, String path) {
        this.type = type;
        this.path = path;
    }

    public RouteConfig(RouteType type, String path, List<String> handlers) {
        this.type = type;
        this.path = path;
        this.handlers = handlers;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Set<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<String> consumes) {
        this.consumes = consumes;
    }

    public Set<String> getProduces() {
        return produces;
    }

    public void setProduces(Set<String> produces) {
        this.produces = produces;
    }

    public List<String> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<String> handlers) {
        this.handlers = handlers;
    }

    public boolean isEmpty() {
        return handlers == null || handlers.isEmpty();
    }

    public void add(final String handler) {
        if (handler != null && !handler.isEmpty()) {
            handlers.add(handler);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteConfig{");
        sb.append("type=").append(type);
        if (path != null && !path.isEmpty()) {
            sb.append(", path='").append(path).append('\'');
        }
        sb.append(", handlers=").append(handlers);
        sb.append('}');
        return sb.toString();
    }
}
