/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;

import com.gnahraf.util.Comparators;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for an operation on group of mutually comparable sorted lists.
 */
public class MultiList<T> {

    /**
     * List of lists. Invariant: always sorted in listOrder.
     *
     * @see #listOrder
     */
    protected final List<T>[] lists;

    protected final Comparator<T> itemOrder;

    /**
     * Lists are mutually sorted by their first element, and then by their
     * size. For the size tie breaker, longer is ordered before shorter, and
     * empty goes last.
     */
    protected final Comparator<List<T>> listOrder;



    protected MultiList(List<T>[] lists, Comparator<T> comparator) {
        this.lists = lists;
        this.itemOrder = comparator;
        this.listOrder = newListOrder();
        sortLists();

        if (lists.length < 2)
            throw new IllegalArgumentException("list.length " + lists.length);
    }

    @SuppressWarnings("unchecked")
	protected MultiList(MultiList<T> copy) {
        {
            @SuppressWarnings("rawtypes")
			List[] array = new List[copy.lists.length];
            this.lists = (List<T>[]) array;
            for (int i = lists.length; i-- > 0; )
                this.lists[i] = copy.lists[i];
        }
        this.itemOrder = copy.itemOrder;
        this.listOrder = copy.listOrder;
    }


    private Comparator<List<T>> newListOrder() {
        return new Comparator<List<T>>() {
            @Override
            public int compare(List<T> lhs, List<T> rhs) {
                if (lhs.isEmpty())
                    return rhs.isEmpty() ? 0 : 1;
                else if (rhs.isEmpty())
                    return -1;
                int comp = itemOrder.compare(lhs.get(0), rhs.get(0));
                return comp == 0 ? Comparators.compare(rhs.size(), lhs.size()) : comp;
            }
        };
    }


    protected final void sortLists() {
        Arrays.sort(lists, listOrder);
    }


    protected final int indexOf(List<T> list, T item) {
        return Collections.binarySearch(list, item, itemOrder);
    }


    protected final List<T> advanceTo(List<T> list, T item) {
        int index = indexOf(list, item);
        if (index < 0)
            index = -1 - index;
        return list.subList(index, list.size());
    }


    protected final List<T> lastList() {
        return lists[lists.length - 1];
    }


    protected final int commonHeads() {
        if (lists[0].isEmpty())
            return 0;
        T headItem = lists[0].get(0);
        int i = 1;
        do {
            if (lists[i].isEmpty() || itemOrder.compare(headItem, lists[i].get(0)) != 0)
                break;
            ++i;
        } while (i < lists.length);
        return i;
    }

}
