/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


/**
 * Base <tt>List</tt> implementation class.
 */
public abstract class BaseList<T> extends ProtoList<T> {

    @Override
    public T get(int location) {
        if (location < 0 || location >= size())
            throw new IndexOutOfBoundsException("location " + location + "; size " + size());
        return getImpl(location);
    }


    protected abstract T getImpl(int location);
}
