package com.jd.laf.ignite.spring;

import com.jd.laf.extension.ExtensionPoint;
import com.jd.laf.extension.ExtensionPointLazy;

public interface Plugin {

    //序列化
    ExtensionPoint<BinaryMarshaller, String> MARSHALLER = new ExtensionPointLazy<>(BinaryMarshaller.class);
}
