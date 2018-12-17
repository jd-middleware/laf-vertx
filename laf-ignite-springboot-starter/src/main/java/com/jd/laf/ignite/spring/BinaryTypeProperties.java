package com.jd.laf.ignite.spring;

import org.apache.ignite.binary.BinaryIdMapper;
import org.apache.ignite.binary.BinaryNameMapper;
import org.apache.ignite.binary.BinarySerializer;

public class BinaryTypeProperties {

    /**
     * Class name.
     */
    private String typeName;

    /**
     * ID mapper.
     */
    private Class<BinaryIdMapper> idMapperClass;

    /**
     * Name mapper.
     */
    private Class<BinaryNameMapper> nameMapperClass;

    /**
     * Serializer.
     */
    private Class<BinarySerializer> serializerClass;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Class<BinaryIdMapper> getIdMapperClass() {
        return idMapperClass;
    }

    public void setIdMapperClass(Class<BinaryIdMapper> idMapperClass) {
        this.idMapperClass = idMapperClass;
    }

    public Class<BinaryNameMapper> getNameMapperClass() {
        return nameMapperClass;
    }

    public void setNameMapperClass(Class<BinaryNameMapper> nameMapperClass) {
        this.nameMapperClass = nameMapperClass;
    }

    public Class<BinarySerializer> getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(Class<BinarySerializer> serializerClass) {
        this.serializerClass = serializerClass;
    }
}
