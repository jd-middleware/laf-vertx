package com.jd.laf.ignite.spring;

import org.apache.ignite.binary.BinarySerializer;

/**
 * 二进制序列化器
 */
public interface BinaryMarshaller extends BinarySerializer {

    String name();
}
