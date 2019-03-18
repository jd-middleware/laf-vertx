package com.jd.laf.web.vertx.marshaller;

import com.jd.laf.binding.marshaller.Marshaller;
import com.jd.laf.binding.marshaller.TypeReference;
import com.jd.laf.binding.marshaller.Unmarshaller;
import com.jd.laf.web.vertx.query.QPageQuery;
import com.jd.laf.web.vertx.query.QUser;
import org.junit.Assert;
import org.junit.Test;

public class JacksonTest {

    @Test
    public void testTypeReference() throws Exception {

        Marshaller marshaller = new JacksonProvider.JacksonMarshaller();
        Unmarshaller unmarshaller = new JacksonProvider.JacksonUnmarshaller();

        QPageQuery<QUser> qPageQuery = new QPageQuery<QUser>(1, 10, new QUser("he"));
        String value = marshaller.marshall(qPageQuery);
        qPageQuery = unmarshaller.unmarshall(value, new TypeReference<QPageQuery<QUser>>(QUser.class) {

        });
        Assert.assertNotNull(qPageQuery);

    }
}
