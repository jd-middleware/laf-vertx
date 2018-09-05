package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yangyang115 on 18-8-30.
 */
public class ListXmlAdapter extends XmlAdapter<String, List<String>> {

    protected ListXmlAdapter() {
        super();
    }

    @Override
    public List<String> unmarshal(String vs) throws Exception {
        List<String> list = new ArrayList<>();
        if (vs != null && vs.length() > 0) {
            String[] vals = vs.split(",");
            for (String val : vals) {
                list.add(val.trim());
            }
        }
        return list;
    }

    @Override
    public String marshal(List<String> listVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String v : listVal) {
            sb.append(",").append(v);
        }
        if (sb.length() > 1) {
            return sb.substring(1);
        } else {
            return sb.toString();
        }
    }
}
