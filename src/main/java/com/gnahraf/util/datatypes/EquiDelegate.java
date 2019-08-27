/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * Base class for a type that delegates equality to one of its fields.
 * This is meant to eliminate the tedium of implementing it correctly, at a
 * small cost to efficiency.
 *
 */
public abstract class EquiDelegate extends Base {

    protected abstract Object equalityDelegate();

    @Override
    protected final boolean equalsImpl(Object o) {
        return ((EquiDelegate) o).equalityDelegate().equals(equalityDelegate());
    }
    @Override
    public int hashCode() {
        return equalityDelegate().hashCode();
    }
}
