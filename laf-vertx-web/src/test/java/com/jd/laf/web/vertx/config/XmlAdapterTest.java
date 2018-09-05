package com.jd.laf.web.vertx.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class XmlAdapterTest {

    @Test
    public void test(){
        String value=" ;,abcd efg;dgg";
        ListXmlAdapter listXmlAdapter=new ListXmlAdapter();
        List<String> list = listXmlAdapter.unmarshal(value);
        Assert.assertEquals(list.size(),3);
        Assert.assertEquals(list.get(0),"abcd");
        Assert.assertEquals(list.get(1),"efg");
        Assert.assertEquals(list.get(2),"dgg");
        value=listXmlAdapter.marshal(list);
        Assert.assertEquals(value,"abcd,efg,dgg");
        SetXmlAdapter setXmlAdapter=new SetXmlAdapter();
        Set<String> set = setXmlAdapter.unmarshal(value);
        Assert.assertEquals(set.size(),3);
        value=listXmlAdapter.marshal(list);
        Assert.assertTrue(set.contains("abcd"));
        Assert.assertTrue(set.contains("efg"));
        Assert.assertTrue(set.contains("dgg"));
    }

}
