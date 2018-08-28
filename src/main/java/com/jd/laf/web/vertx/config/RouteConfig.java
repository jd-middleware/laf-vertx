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
    public static final String PLACE_HOLDER = "{}";
    //路径
    @XmlAttribute
    private String path;
    //名称，可用于继承
    @XmlAttribute
    private String name;
    //是否是模板，模板不构建路由
    @XmlAttribute
    private boolean template;
    //是否是正则表达式
    @XmlAttribute
    private boolean regex;
    //继承自
    @XmlAttribute
    private String inherit;
    //方法
    @XmlAttribute
    private RouteType type;
    //超时时间
    @XmlAttribute
    private Long timeout;
    //顺序
    @XmlAttribute
    private Integer order;
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
    //异常处理器
    @XmlAttribute
    @XmlList
    private List<String> errors = new ArrayList<>(3);

    public RouteConfig() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addHandler(final String handler) {
        if (handler != null && !handler.isEmpty()) {
            handlers.add(handler);
        }
    }

    public void addError(final String error) {
        if (error != null && !error.isEmpty()) {
            errors.add(error);
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
