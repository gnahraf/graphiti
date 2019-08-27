/*
 * Copyright 2015 Babak Farhang
 */
package com.gnahraf.util.list;


import com.gnahraf.util.Comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by babak on 2/26/15.
 */
public class Lists {

    private Lists() { }






    public static <T> List<T> reversedView(List<T> list) {
        return list instanceof ReversedList ?
                ((ReversedList<T>) list).base :
                new ReversedList<>(list);

    }


    public static <T extends Comparable<T>> List<T> distinctUnion(List<T> a, List<T> b) {
        Comparator<T> naturalOrder = Comparators.naturalComparator();
        return distinctUnion(a, b, naturalOrder);
    }

    /**
     * Returns an ordered, distinct union of 2 ordered distinct, lists. An ordered, distinct list
     * here is simply an ordered list containing no duplicates. The construction of the
     * return value necessarily involves some
     * computation: we cannot simply return a lazy view of the union since the size of the union
     * is dependent on the number of collisions.
     * <p/>
     * For now, the implementation is not very memory efficient. Depending on the relative sizes
     * of the lists, we can do a good deal better. Postponing optimizations..
     *
     * @param a ordered, distinct list
     * @param b ordered, distinct list
     * @param order the total ordering of inputs and output
     *
     * @return an immutable ordered, distinct list
     */
    @SuppressWarnings("unchecked")
	public static <T> List<T> distinctUnion(List<T> a, List<T> b, Comparator<? super T> order) {

        if (a.isEmpty())
            return b;
        else if (b.isEmpty())
            return a;

        ArrayList<T> union = new ArrayList<>(a.size() + b.size());

        while (true) {

            if (a.isEmpty()) {
                union.addAll(b);
                break;
            } else if (b.isEmpty()) {
                union.addAll(a);
                break;
            }

            T aI = a.get(0);
            T bI = b.get(0);

            int comp = order.compare(aI, bI);
            if (comp == 0) {
                a = a.subList(1, a.size());
                continue;
            } else if (comp > 0) {
                Object swp = aI;
                aI = bI;
                bI = (T) swp;
                swp = a;
                a = b;
                b = (List<T>) swp;
            }

            // the first element in a < the first element in b (i.e. like comp < 0)

            // find the index of the first b in a..
            int indexInA = Collections.binarySearch(a, bI, order);

            if (indexInA < 0) {
                // the first b does not occur in a
                indexInA = -indexInA - 1;

                // indexInA is now at the insertion offset and is >= 1
                if (indexInA == 1) {
                    union.add(aI);
                    union.add(bI);
                    a = a.subList(1, a.size());
                    b = b.subList(1, b.size());
                } else {
                    union.addAll(a.subList(0, indexInA));
                    a = a.subList(indexInA, a.size());
                }
            } else {
                // found the index of the first b in a
                // .. so we'll throw out the first b
                b = b.subList(1, b.size());
                ++indexInA;
                union.addAll(a.subList(0, indexInA));
                a = a.subList(indexInA, a.size());
            }
        }
        return Collections.unmodifiableList(union);
    }


    public static <T> List<T> concatView(List<? extends T> first, List<? extends T> second) {
        return new ConcatList<>(first, second);
    }


    /**
     * Fixes the implementation of {@linkplain java.util.AbstractList#subList(int, int)},
     * or rather, the list returned by that method.
     *
     * @param list must not be mutated
     * @return a view of the <tt>list</tt> on which it is safe to chain invocations
     *         of {@linkplain java.util.List#subList(int, int)}
     */
    public static <T> List<T> subListWorkaround(List<T> list) {
        return list instanceof  SubList || list instanceof BaseList ? list : new SubList<T>(list);
    }



    public static <T> List<T> snapshot(List<T> list) {
        List<T> snapshot;
        if (list.isEmpty())
            snapshot =  Collections.emptyList();
        else
            snapshot = new ReadOnlyArrayList<>(list.toArray());
        return snapshot;
    }


    public static <T> List<T> asList(T[] array) {
        return new ReadOnlyArrayList<>(array);
    }




    private static class ReversedList<T> extends BaseList<T> {
        final List<T> base;
        ReversedList(List<T> base) {
            this.base = base;
            if (base == null)
                throw new IllegalArgumentException("null base list");
        }
        @Override
        protected T getImpl(int location) {
            return base.get(base.size() - location - 1);
        }
        @Override
        public int size() {
            return base.size();
        }
    }




    private static class ConcatList<T> extends BaseList<T> {

        private final List<? extends T> first;
        private final List<? extends T> second;

        ConcatList(List<? extends T> first, List<? extends T> second) {
            this.first = first;
            this.second = second;
        }
        @Override
        protected T getImpl(int location) {
            int fsize = first.size();
            return location >= fsize ? second.get(location - fsize) : first.get(location);
        }

        @Override
        public int size() {
            return first.size() + second.size();
        }
    }



    private static class ReadOnlyArrayList<T> extends BaseList<T> {
        private final Object[] array;
        ReadOnlyArrayList(Object[] array) {
            this.array = array;
        }
        @SuppressWarnings("unchecked")
		@Override
        protected T getImpl(int location) {
            return (T) array[location];
        }
        @Override
        public int size() {
            return array.length;
        }
    }



}
