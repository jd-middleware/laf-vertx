package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 配置的路由信息
 */
@XmlType(name = "route")
@XmlAccessorType(XmlAccessType.NONE)
public class RouteConfig extends HandlerConfig {
    public static final String PLACE_HOLDER = "{}";

    //名称，可用于继承
    @XmlAttribute
    private String name;
    //是否是路由
    @XmlAttribute
    private boolean route;
    //是否是正则表达式
    @XmlAttribute
    private boolean regex;
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
    //支持的消费内容
    private Set<String> consumes;
    //支持的生产内容
    private Set<String> produces;
    //异常处理器
    private List<String> errors = new ArrayList<>(3);

    public RouteConfig() {

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
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(final String error) {
        if (error != null && !error.isEmpty()) {
            errors.add(error);
        }
    }

    public void inherit(final RouteConfig parent) {
        if (parent == null) {
            return;
        }
        if (type == null && parent.type != null) {
            type = parent.type;
        }
        if ((consumes == null || consumes.isEmpty())
                && parent.consumes != null && !parent.consumes.isEmpty()) {
            consumes = parent.consumes;
        }
        if ((produces == null || produces.isEmpty())
                && parent.produces != null && !parent.produces.isEmpty()) {
            produces = parent.produces;
        }
        //处理业务处理器
        handlers = merge(parent.handlers, handlers);
        //处理异常处理器，直接覆盖
        errors = merge(parent.errors, errors);
    }

    /**
     * 合并处理链
     *
     * @param parent
     * @param child
     * @return
     */
    protected List<String> merge(final List<String> parent, final List<String> child) {
        List<String> result = child;
        if (parent != null && !parent.isEmpty()) {
            result = new LinkedList<>();
            boolean flag = false;
            for (String handler : parent) {
                if (!PLACE_HOLDER.equals(handler)) {
                    result.add(handler);
                } else if (!flag) {
                    flag = true;
                    if (child != null) {
                        result.addAll(child);
                    }
                }
            }
            if (!flag && child != null) {
                result.addAll(child);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteConfig{");
        sb.append("path='").append(path).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", route=").append(route);
        sb.append(", regex=").append(regex);
        sb.append(", inherit='").append(inherit).append('\'');
        sb.append(", template='").append(template).append('\'');
        sb.append(", type=").append(type);
        sb.append(", timeout=").append(timeout);
        sb.append(", order=").append(order);
        sb.append(", consumes=").append(consumes);
        sb.append(", produces=").append(produces);
        sb.append(", errors=").append(errors);
        sb.append(", handlers=").append(handlers);
        sb.append('}');
        return sb.toString();
    }
}
