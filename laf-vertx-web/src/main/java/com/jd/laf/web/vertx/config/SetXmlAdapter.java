package com.jd.laf.web.vertx.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yangyang115 on 18-8-30.
 */
public class SetXmlAdapter extends XmlAdapter<String, Set<String>> {

    protected SetXmlAdapter() {
        super();
    }

    @Override
    public Set<String> unmarshal(final String value) {
        if (value == null) {
            return null;
        }
        Set<String> result = new HashSet<>();
        int start = 0;
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '\t':
                case ' ':
                case ',':
                case ';':
                    if (i > start) {
                        result.add(value.substring(start, i));
                    }
                    start = i + 1;
                    break;
            }
        }
        if (start < value.length()) {
            result.add(value.substring(start));
        }

        return result;
    }

    @Override
    public String marshal(final Set<String> value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String v : value) {
            if (count++ > 0) {
                sb.append(',');
            }
            sb.append(v);
        }
        return sb.toString();
    }
}
