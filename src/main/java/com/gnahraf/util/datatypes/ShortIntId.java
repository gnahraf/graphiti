/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


/**
 * Base class for a <tt>(short,int)</tt> tuple.
 */
public class ShortIntId extends Base {

    private final short type;
    private final int id;


    public ShortIntId(int type, int id) {
        this(Primitives.signedShort(type), id);
    }

    public ShortIntId(short type, int id) {
        this.type = type;
        this.id = id;
    }

    public ShortIntId(ShortIntId copy) {
        this.type = copy.type;
        this.id = copy.id;
    }

    public final short getType() {
        return type;
    }
    public final int getId() {
        return id;
    }


    public final int compareTo(ShortIntId another) {
        int diff = ((int) type) - another.type;
        if (diff != 0)
            return diff;
        if (id < another.id)
            return -1;
        else if (id == another.id)
            return 0;
        else
            return 1;
    }

    protected final boolean equalsImpl(Object o) {
        return equalsShortInt((ShortIntId) o);
    }


    public final boolean equalsShortInt(ShortIntId other) {
        return other.id == id && other.type == type;
    }

    @Override
    public final int hashCode() {
        int hash = type;
        hash <<= 16;
        return hash ^ id;
    }


    @Override
    public String toString() {
        return "(" + type + "," + id + ")";
    }
}
