/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;



import static org.junit.Assert.*;

/**
 * Created by babak on 6/11/15.
 */
public class OrderedTypeTest {


    public static void assertEqualsWithHashCheck(Object a, Object b) {
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
    }


    @SuppressWarnings("unchecked")
	public static void assertOrderedEquals(Object a, Object b) {
        assertEquals(0, ((Comparable<Object>) a).compareTo(b));
        assertEquals(0, ((Comparable<Object>) b).compareTo(a));
        assertEqualsWithHashCheck(a, b);
    }


    @SuppressWarnings("unchecked")
	public static void assertOrdered(Object lesser, Object greater) {
        int lcg = ((Comparable<Object>) lesser).compareTo(greater);
        int gcl = ((Comparable<Object>) greater).compareTo(lesser);
        assertTrue(lcg < 0);
        assertTrue(gcl > 0);
        assertFalse(lesser.equals(greater));
        assertFalse(greater.equals(lesser));
    }


    public static void assertOrdered(Object lo, Object mid, Object hi) {
        assertOrdered(lo, mid);
        assertOrdered(mid, hi);
        assertOrdered(lo, hi);
    }


    public static void assertNotEqual(Object a, Object b) {
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

}
