package com.jd.laf.web.vertx.response;

import org.junit.Test;

import java.util.*;

public class ClassComparatorTest {

    @Test
    public void test() {
        List<Class> result = new ArrayList<>();
        result.add(List.class);
        result.add(Collection.class);
        result.add(ArrayList.class);
        result.add(Set.class);
        result.add(Object.class);
        result.add(HashSet.class);

        Collections.sort(result, (o1, o2) -> {
            if (o1 == o2) {
                return 0;
            } else if (o1.isAssignableFrom(o2)) {
                return 1;
            } else if (o2.isAssignableFrom(o1)) {
                return -1;
            }
            return 0;
        });

        result.forEach(System.out::println);
    }
}
