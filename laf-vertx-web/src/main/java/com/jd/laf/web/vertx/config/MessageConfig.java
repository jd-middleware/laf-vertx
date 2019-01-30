package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 配置的消费信息
 */
@XmlType(name = "message")
@XmlAccessorType(XmlAccessType.NONE)
public class MessageConfig extends HandlerConfig {
    //缓冲区大小
    @XmlAttribute
    protected Integer bufferSize;

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageConfig{");
        sb.append("path='").append(path).append('\'');
        sb.append(", bufferSize=").append(bufferSize);
        sb.append(", handlers=").append(handlers);
        sb.append('}');
        return sb.toString();
    }
}
