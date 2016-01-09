package com.shaubert.liftago.util;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionsUtils {

    public static <E> List<E> filter(Collection<E> src, Predicate<E> p) {
        final ArrayList<E> res = new ArrayList<>();
        for (E elem : src) {
            if (p.apply(elem)) {
                res.add(elem);
            }
        }
        return res;
    }
}
