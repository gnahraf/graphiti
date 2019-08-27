/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


/**
 * Base class for a <tt>(int,int)</tt> tuple.
 */
public class IntInt extends Base {

    private final int first;
    private final int second;


    public IntInt(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public IntInt(IntInt copy) {
        this.first = copy.first;
        this.second = copy.second;
    }

    public final int first() {
        return first;
    }
    public final int second() {
        return second;
    }


    public final int compareTo(IntInt another) {
        if (first < another.first)
            return -1;
        else if (first > another.first)
            return 1;
        else if (second < another.second)
            return -1;
        else if (second > another.second)
            return 1;
        else
            return 0;
    }

    protected final boolean equalsImpl(Object o) {
        return equalsIntInt((IntInt) o);
    }


    public final boolean equalsIntInt(IntInt other) {
        return other.second == second && other.first == first;
    }

    @Override
    public final int hashCode() {
        return first ^ second;
    }


    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}
