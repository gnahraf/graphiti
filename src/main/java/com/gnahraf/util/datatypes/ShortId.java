/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


/**
 * Created by babak on 6/10/15.
 */
public class ShortId extends Base {

    private final short id;

    public ShortId(int id) {
        this(Primitives.signedShort(id));
    }

    public ShortId(short id) {
        this.id = id;
    }

    public ShortId(ShortId copy) {
        this.id = copy.id;
    }



    public final short getId() {
        return id;
    }




    public final int compareTo(ShortId another) {
        return ((int) id) - another.id;
    }


    @Override
    protected final boolean equalsImpl(Object o) {
        return equalsShortId((ShortId) o);
    }


    public final boolean equalsShortId(ShortId another) {
        return another != null && another.id == id;
    }

    @Override
    public final int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Short.toString(id);
    }

}
