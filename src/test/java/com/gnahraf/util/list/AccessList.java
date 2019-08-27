/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;

/**
 * Created by babak on 7/10/15.
 */
public abstract class AccessList<T> extends BaseList<T> {

    private int accessCount;

    public void resetAccessCount() {
        accessCount = 0;
    }

    public int accessCount() {
        return accessCount;
    }


    @Override
    protected T getImpl(int location) {
        ++accessCount;
        return accessCountedGetImpl(location);
    }

    protected abstract T accessCountedGetImpl(int location);
}
