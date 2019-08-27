/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * A non-decreasing sequence of 2 integers. Can be used to represent a range, for example.
 *
 * <h3Invariant:</h3>
 * {@linkplain #first()} &le; {@linkplain #second()}
 */
public class Interval extends IntInt {

    /**
     * @throws IllegalArgumentException
     *         if {@literal first > second}
     */
    public Interval(int first, int second) {
        super(first, second);
        checkArgs();
    }

    public Interval(Interval copy) {
        super(copy);
    }

    /** Synonym for {@linkplain #first()} */
    public final int lo() {
        return first();
    }


    /** Synonym for {@linkplain #second()} */
    public final int hi() {
        return second();
    }


    private void checkArgs() {
        if (first() > second())
            throw new IllegalArgumentException(
                "first (" + first() + ") > second (" + second() + ")");
    }






    @Override
    protected final boolean isEqualsCompat(Object o) {
        return o instanceof Interval;
    }
}
