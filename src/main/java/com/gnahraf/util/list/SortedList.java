/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import com.gnahraf.util.Comparators;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Created by babak on 7/16/15.
 */
public class SortedList<T> extends BaseList<T> {

    private final Comparator<Object> order;

    private Object[] array;

    private int size;
    private int modCount;
    private int sortModCount;


    public SortedList(Comparator<? extends T> order) {
        this(order, 32);
    }


    @SuppressWarnings("unchecked")
	public SortedList(Comparator<? extends T> order, int initCapacity) {
        this.order = (Comparator<Object>) order;
        this.array = new Object[initCapacity];

        if (order == null)
            throw new IllegalArgumentException("null order");
    }

    @Override
    public boolean add(T object) {
        if (object == null)
            throw new IllegalArgumentException("null");
        ensureCapacity(size);
        array[size++] = object;
        ++modCount;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        ensureCapacity(size + collection.size());
        int index = size;
        for (T element : collection)
            array[index++] = element;
        size = index;
        ++modCount;
        return true;
    }


    // Commented out because this only complies with the List interface if the
    // comparator is consistent with equals--which in most cases, it isn't.
//    @Override
//    public int indexOf(Object object) {
//        try {
//            int index = binarySearch((T) object);
//            return index >= 0 && array[index].equals(object) ? index : -1;
//        } catch (ClassCastException ccx) {
//            return -1;
//        }
//    }

//    @Override
//    public boolean contains(Object object) {
//        return indexOf(object) != -1;
//    }


    public int binarySearch(T object) {
        if (modCount != sortModCount)
            sort();
        return Arrays.binarySearch(array, 0, size, object, order);
    }

    @Override
    public void clear() {
        size = 0;
        ++modCount;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected T getImpl(int location) {
        if (modCount != sortModCount)
            sort();
        return (T) array[location];
    }

    @Override
    public int size() {
        return size;
    }


    private void ensureCapacity(int capacity) {
        if (capacity <= array.length)
            return;
        int newCapacity = Math.max((int) (array.length * expansionFactor()), capacity);
        Object[] copy = new Object[newCapacity];
        for (int i = size; i-- > 0; )
            copy[i] = array[i];
        array = copy;
    }

    private void sort() {
        Arrays.sort(array, 0, size, order);
        sortModCount = modCount;
    }


    protected float expansionFactor() {
        return 1.5f;
    }



    public static <N extends Comparable<N>> SortedList<N> newInstance() {
        Comparator<N> naturalOrder = Comparators.naturalComparator();
        return new SortedList<>(naturalOrder);
    }
}
