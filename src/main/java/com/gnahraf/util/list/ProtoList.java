/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Base implementation class. As far as I'm concerned, every list should be
 * random access.
 */
public abstract class ProtoList<T> extends AbstractList<T> implements RandomAccess {


    /**
     * Fix for IR9057625
     */
    @Override
    public List<T> subList(int start, int end) {
        return new SubList<T>(this, start, end - start);
    }
}
