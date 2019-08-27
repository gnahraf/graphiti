/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by babak on 6/11/15.
 */
public class BaseTest {

    @Test
    public void testEquality() {
        Object a = new Base();
        assertEquals(a, a);
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Base()));
    }

}
