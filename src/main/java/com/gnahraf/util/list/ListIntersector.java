/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import com.gnahraf.util.Comparators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator over the intersecting elements of 2 or more sorted lists. In order for
 * an element to be returned by this iterator, it must occur in all lists.
 */
public class ListIntersector<T> extends MultiList<T> implements Iterator<T>, Iterable<T> {


    /**
     * Pseudo-constructor for lists of <tt>Comparable</tt> instances.
     */
    public static  <Y extends Comparable<Y>> ListIntersector<Y> newInstance(List<Y>[] lists) {
        Comparator<Y> comparator = Comparators.naturalComparator();
        return new ListIntersector<>(lists, comparator);
    }


    /**
     * Constructs a new instance using the given array of <tt>lists</tt>.
     * The individual lists are assumed to be each sorted using the specified
     * <tt>comparator</tt>. Results are undefined, o.w. (no defensive fail fast
     * mechanisms.)
     *
     * @param lists
     *        array of sorted lists. Owned by the instance. Must not be modified externally.
     * @param comparator
     *        the comparator with which the elements of the lists have been sorted
     *
     * @throws NullPointerException
     *         if any of the arguments is <tt>null</tt>
     */
    public ListIntersector(List<T>[] lists, Comparator<T> comparator) {
        super(lists, comparator);
        advanceToIntersection();
    }

    /**
     * Copy constructor. Creates a new instance, in the same state as the given
     * <tt>copy</tt>.
     */
    public ListIntersector(ListIntersector<T> copy) {
        super(copy);
    }

    @Override
    public boolean hasNext() {
        return atIntersection();
    }

    @Override
    public T next() {
        if (!atIntersection())
            throw new NoSuchElementException();
        T item = lists[0].get(0);
        for (int i = lists.length; i-- > 0; )
            lists[i] = lists[i].subList(1, lists[i].size());
        sortLists();
        advanceToIntersection();
        return item;
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }


    private boolean atIntersection() {
        return commonHeads() == lists.length;
    }


    private void advanceToIntersection() {
        while (!atIntersection()) {
            List<T> lastList = lastList();
            if (lastList.isEmpty())
                break;
            T nexusCandidate = lastList.get(0);
            int i = 0;
            do {
                lists[i] = advanceTo(lists[i], nexusCandidate);
                ++i;
            } while (lists[i] != lastList);
            sortLists();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
