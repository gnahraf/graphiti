/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util;

import com.gnahraf.util.datatypes.EquiType;

import java.util.Comparator;

/**
 * Created by babak on 5/9/15.
 */
public class Comparators {

    public interface Natural {

    }

    public static <T> Comparator<T> compound(
            final Comparator<? super T> primary, final Comparator<? super T> secondary) {

        return new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                int comp = primary.compare(lhs, rhs);
                if (comp == 0)
                    comp = secondary.compare(lhs, rhs);
                return comp;
            }
        };
    }


    public static <T extends Comparable<T>> Comparator<T> naturalComparator() {
        class NatComp<T extends Comparable<T>> extends EquiType implements Comparator<T>, Natural {
            @Override
            public int compare(T lhs, T rhs) {
                return lhs.compareTo(rhs);
            }
        }
        return new NatComp<T>();
    }


    public static <T> Comparator<T> supernaturalComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((Comparable) lhs).compareTo(rhs);
            }
        };
    }


    public static int compare(double a, double b) {
        if (a < b)
            return -1;
        else if (a == b)
            return 0;
        else
            return 1;
    }


    public static int compare(long a, long b) {
        if (a < b)
            return -1;
        else if (a == b)
            return 0;
        else
            return 1;
    }
}
