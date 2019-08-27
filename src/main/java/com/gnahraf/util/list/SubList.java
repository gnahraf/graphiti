/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * A workaround for a bug in the default <tt>List.subList()</tt> implementation. Reported,
 * but not sure whether it's been fixed (cf IR9057625).
 */
public class SubList<T> extends AbstractList<T> implements RandomAccess {

    @SuppressWarnings("unchecked")
	private final static SubList<?> EMPTY = new SubList<Object>(Collections.EMPTY_LIST);

    @SuppressWarnings("unchecked")
	public static <T> SubList<T> empty() {
        return (SubList<T>) EMPTY;
    }

    private final List<T> base;
    private final int offset;
    private final int size;

    public SubList(List<T> base) {
        this(base, 0, base.size(), false);
    }

    public SubList(List<T> base, int offset, int size) {
        this(base, offset, size, false);
        if (base == null)
            throw new IllegalArgumentException("null base list");
        if (offset < 0)
            throw new IllegalArgumentException("offset: " + offset);
        if (size < 0)
            throw new IllegalArgumentException("size: " + offset);
        if (offset + size > base.size())
            throw new IllegalArgumentException(
                    "offset: " + offset + ", size: " + size + "; base.size(): " + base.size());
    }


    private SubList(List<T> base, int offset, int size, boolean ignore) {
        this.base = base;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public T get(int location) {
        if (location >= size || location < 0)
            throw new IndexOutOfBoundsException(String.valueOf(location));
        return base.get(location + offset);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> subList(int start, int end) {
        if (end == size && start == 0)
            return this;

        if (start < 0 || end < start || end > size)
            throw new IndexOutOfBoundsException(
                    "start: " + start + ", end: " + end + ", size: " + size);

        int subSize = end - start;
        if (subSize == 0)
            return empty();
        else
            return newSubList(base, offset + start, subSize);
    }


    /**
     * Creates a new instance. Arguments have been checked. This controls the
     * exact type returned by Override, for example,
     * in a {@linkplain java.util.RandomAccess} subclass. Arguments are already checked.
     */
    protected List<T> newSubList(List<T> base, int offset, int size) {
        return new SubList<T>(base, offset, size, false);
    }
}
