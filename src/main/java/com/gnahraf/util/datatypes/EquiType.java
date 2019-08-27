/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


/**
 * Base class for a type that expresses an equivalence class.
 */
public class EquiType {
    private final static int BASE_HASH = EquiType.class.hashCode();
    protected EquiType() { }
    /**
     * All instances of the same class are equal.
     */
    @Override
    public final boolean equals(Object other) {
        return this == other || other != null && other.getClass() == getClass();
    }
    @Override
    public final int hashCode() {
        return BASE_HASH ^ getClass().hashCode();
    }
}
