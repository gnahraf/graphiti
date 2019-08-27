/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import com.gnahraf.util.list.BaseList;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by babak on 6/7/15.
 */
public class OnDemandListBinarySearchTest {

    static abstract class TestList<T> extends BaseList<T> {

        private final int size;
        private int accessCount;

        TestList(int size) {
            this.size = size;
        }

//        @Override
//        protected T getImpl(int location) {
//            ++accessCount;
//            return super.get(location);
//        }

        @Override
         public T get(int location) {
            ++accessCount;
            return super.get(location);
        }

        public int getAccessCount() {
            return accessCount;
        }

        public List<T> resetAccessCount() {
            accessCount = 0;
            return this;
        }

        @Override
        public int size() {
            return size;
        }
    }

    static class OnDemandInt extends TestList<Integer> {

        OnDemandInt(int size) {
            super(size);
        }

        @Override
        protected Integer getImpl(int location) {
            return location * location;
        }
    }


    static class OnDemandLong extends TestList<Long> {

        OnDemandLong(int size) {
            super(size);
        }

        @Override
        protected Long getImpl(int location) { return ((long) location) * location; }

    }


    @Test
    public void testWith2Elements() {
        List<Integer> l2 = new OnDemandInt(5).subList(3, 5);
        assertEquals(9, l2.get(0).intValue());
        assertEquals(16, l2.get(1).intValue());
        assertEquals(-1, Collections.binarySearch(l2, 0));
        assertEquals(-1, Collections.binarySearch(l2, 8));
        assertEquals(0, Collections.binarySearch(l2, 9));
        assertEquals(-2, Collections.binarySearch(l2, 10));
        assertEquals(-2, Collections.binarySearch(l2, 15));
        assertEquals(1, Collections.binarySearch(l2, 16));
        assertEquals(-3, Collections.binarySearch(l2, 17));
    }

    @Test
    public void testWith1kElements() {
        String method = "[testWith1kElements] ";
        OnDemandInt k = new OnDemandInt(1013);
        assertNotSame(k.get(23), k.get(23));
        assertEquals(2, k.getAccessCount());
        assertEquals(499, Collections.binarySearch(k.resetAccessCount(), 499 * 499));
        System.out.println(method + "access count: " + k.getAccessCount());
        assertEquals(-500, Collections.binarySearch(k.resetAccessCount(), 499 * 498));
        System.out.println(method + "access count: " + k.getAccessCount());
        System.out.println(method + "access count: " + k.getAccessCount());
        assertEquals(-501, Collections.binarySearch(k.resetAccessCount(), 500 * 499));
        System.out.println(method + "access count: " + k.getAccessCount());
    }

    @Test
    public void testWith1MElements() {
        String method = "[testWith1MElements] ";
        OnDemandLong m = new OnDemandLong(1000 * 1013);
        assertEquals(499, Collections.binarySearch(m, (long) 499 * 499));
        System.out.println(method + "access count: " + m.getAccessCount());

        long p = 1000 * 1000;
        assertEquals(p, Collections.binarySearch(m.resetAccessCount(), p * p));
        System.out.println(method + "access count: " + m.getAccessCount());
    }
}
