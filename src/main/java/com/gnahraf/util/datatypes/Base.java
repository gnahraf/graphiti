/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * Base type. This takes care of some boiler plate code for
 * correctly implementing equality across mixed types. The aim is not
 * speed efficiency.
 */
public class Base {


    /**
     * @return {@literal o != null && isEqualsCompat(o) && equalsImpl(o)}
     *
     * @see #isEqualsCompat(Object)
     * @see #equalsImpl(Object)
     */
    @Override
    public final boolean equals(Object o) {
        return o == this || o != null && isEqualsCompat(o) && equalsImpl(o);
    }

    /**
     * Equality implementation. Invoked only if {@linkplain #isEqualsCompat(Object)
     * isEqualsCompat(o)} returns <tt>true</tt>.
     *
     * @param o another instance (neither <tt>this</tt> nor <tt>null</tt>)
     *
     * @return the base implementation returns <tt>false</tt>
     */
    protected boolean equalsImpl(Object o) {
        return false;
    }


    /**
     * Determines whether the class of the given object is compatible for
     * equality with this one. By default, this returns the most discriminating
     * test, namely {@literal o.getClass() == getClass()}. It may be overriden
     * to return {@literal o instanceof C}: in that event, the overridden method
     * must be marked <tt>final</tt>.
     */
    protected boolean isEqualsCompat(Object o) {
        return o.getClass() == getClass();
    }

}
