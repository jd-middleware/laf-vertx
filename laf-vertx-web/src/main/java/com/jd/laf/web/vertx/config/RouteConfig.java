package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
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
    //是否是路由
    @XmlAttribute
    private boolean route;
    //是否是正则表达式
    @XmlAttribute
    private boolean regex;
    //是否阻塞链
    @XmlAttribute
    private boolean blocking;
    //继承自
    @XmlAttribute
    private String inherit;
    //模板名称
    @XmlAttribute
    private String template;
    //方法
    @XmlAttribute
    private RouteType type;
    //超时时间
    @XmlAttribute
    private Long timeout;
    //顺序
    @XmlAttribute
    private Integer order;
    //缓冲区大小
    @XmlAttribute
    private Integer bufferSize;
    //支持的消费内容
    private Set<String> consumes;
    //支持的生产内容
    private Set<String> produces;
    //处理器
    private List<String> handlers = new ArrayList<>(5);
    //异常处理器
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

    public boolean isRoute() {
        return route;
    }

    public void setRoute(boolean route) {
        this.route = route;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(SetXmlAdapter.class)
    public Set<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<String> consumes) {
        this.consumes = consumes;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(SetXmlAdapter.class)
    public Set<String> getProduces() {
        return produces;
    }

    public void setProduces(Set<String> produces) {
        this.produces = produces;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(ListXmlAdapter.class)
    public List<String> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<String> handlers) {
        this.handlers = handlers;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(ListXmlAdapter.class)
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
