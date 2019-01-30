package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 路由信息
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class HandlerConfig {
    //路径
    @XmlAttribute
    protected String path;
    //处理器
    protected List<String> handlers = new ArrayList<>(5);

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(ListXmlAdapter.class)
    public List<String> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<String> handlers) {
        this.handlers = handlers;
    }

    public void addHandler(final String handler) {
        if (handler != null && !handler.isEmpty()) {
            handlers.add(handler);
        }
    }

}
