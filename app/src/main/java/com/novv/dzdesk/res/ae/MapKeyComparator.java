package com.novv.dzdesk.res.ae;

import java.util.Comparator;

public class MapKeyComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
