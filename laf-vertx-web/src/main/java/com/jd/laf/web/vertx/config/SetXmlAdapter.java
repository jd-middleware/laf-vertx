package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangyang115 on 18-8-30.
 */
public class SetXmlAdapter extends XmlAdapter<String, Set<String>> {

    protected SetXmlAdapter() {
        super();
    }

    @Override
    public Set<String> unmarshal(String v) throws Exception {
        Set<String> set = new HashSet<>();
        if (v != null && v.length() > 0) {
            String[] vals = v.split(",");
            for (String val : vals) {
                set.add(val.trim());
            }
        }
        return set;
    }

    @Override
    public String marshal(Set<String> setVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String v : setVal) {
            sb.append(",").append(v);
        }
        if (sb.length() > 1) {
            return sb.substring(1);
        } else {
            return sb.toString();
        }
    }
}
