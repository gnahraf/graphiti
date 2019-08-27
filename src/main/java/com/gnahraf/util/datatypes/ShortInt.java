/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * Created by babak on 6/15/15.
 */
public final class ShortInt extends ShortIntId implements Comparable<ShortInt> {

    public ShortInt(int type, int id) {
        super(type, id);
    }

    public ShortInt(short type, int id) {
        super(type, id);
    }

    public ShortInt(ShortIntId copy) {
        super(copy);
    }

    @Override
    public int compareTo(ShortInt another) {
        return super.compareTo(another);
    }
}
