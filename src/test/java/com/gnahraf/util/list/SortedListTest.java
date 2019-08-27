/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by babak on 7/18/15.
 */
public class SortedListTest {

    @Test
    public void testAFew() {
        List<Integer> list = SortedList.newInstance();
        assertTrue(list.isEmpty());
        assertTrue( list.add(1) );
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
        list.add(0);
        assertEquals(2, list.size());
        assertEquals(0, list.get(0).intValue());
        assertEquals(1, list.get(1).intValue());
        list.add(2);
        assertEquals(3, list.size());
        assertEquals(0, list.get(0).intValue());
        assertEquals(1, list.get(1).intValue());
        assertEquals(2, list.get(2).intValue());
    }

    @Test
    public void testAFew2() {
        List<Integer> list = SortedList.newInstance();
        assertTrue(list.isEmpty());
        list.add(1);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
        list.add(5);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        list.add(3);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(3, list.get(1).intValue());
        assertEquals(5, list.get(2).intValue());
    }
}
