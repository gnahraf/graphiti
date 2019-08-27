/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


/**
 * A positive {@linkplain Interval interval}.
 */
public class Pinterval extends Interval {

    public Pinterval(int lo, int hi) {
        super(lo, hi);
        if (lo < 0)
            throw new IllegalArgumentException("negative lo: " + this);
    }


    public Pinterval(Pinterval copy) { super(copy); }



    public final int span() {
        return hi() - lo();
    }
}
