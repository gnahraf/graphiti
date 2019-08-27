/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import org.junit.Test;


import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by babak on 4/29/15.
 */
public class SubListTest {

    @Test
    public void testEmpty() {
        List<Integer> list = Collections.emptyList();
        list = new SubList<Integer>(list);
        assertEquals(0, list.size());
        assertEquals(list, list.subList(0, 0));
    }


    @Test
    public void testOne() {
        final Integer v = 100000;
        List<Integer> list = Collections.singletonList(v);
        list = new SubList<Integer>(list);
        assertEquals(1, list.size());
        assertEquals(list, list.subList(0, 1));
        assertEquals(0, list.subList(0, 0).size());
        assertEquals(v, list.get(0));
    }

    @Test
    public void testTwo() {
        Integer[] v = { 1, 2, };
        List<Integer> base = Arrays.asList(v);

        // Test the test
        assertSubList(base, false);

        // Now the actual test
        assertSubList(new SubList<Integer>(base));
    }


    @Test
    public void testWith3() {
        performRecursiveTest(3);
    }


    @Test
    public void testWith1024() {
        long tick = System.currentTimeMillis();
        performRecursiveTest(1024);
        printLap("testWith1024", tick);
    }

    private void printLap(String label, long tick) {
        long lap = System.currentTimeMillis() - tick;
        System.out.println("[" + label + "] " + new DecimalFormat("#,###").format(lap) + " ms");
    }

    @Test
    public void testWith1024StockImpl() {
        long tick = System.currentTimeMillis();
        List<Integer> stock = Arrays.asList(makeIntegers(1024));
        assertSubList(stock, false);
        printLap("testWith1024StockImpl", tick);
    }

    @Test
    public void testWith512x1024() {
        long tick = System.currentTimeMillis();
        performRecursiveTest(1024 * 512);
        printLap("testWith512x1024", tick);
    }

    @Test
    public void testSubListTraversalWith1024() {
        long tick = System.currentTimeMillis();
        List<Integer> list = new SubList<Integer>(Arrays.asList(makeIntegers(1024)));
        assertSubListTraversal(list, false);
        printLap("testSubListTraversalWith1024", tick);
    }

    @Test
    public void testSubListTraversalWith1024StockImplementation() {
        long tick = System.currentTimeMillis();
        List<Integer> list = Arrays.asList(makeIntegers(1024));
        assertSubListTraversal(list, false);
        printLap("testSubListTraversalWith1024StockImplementation", tick);
    }

    @Test
    public void compareSubListTraversal() {
        List<Integer> stock = Arrays.asList(makeIntegers(1024 * 50));
        traverse(stock, true);
        traverse(new SubList<Integer>(stock));
    }

    @Test
    public void testIndexOutOfBoundsGet() {
        int count = 3;
        SubList<Integer> list = new SubList<Integer>(Arrays.asList(makeIntegers(count)));
        assertIndexOutOfBounds(list, count);
        assertIndexOutOfBounds(list, -1);
    }

    @Test
    public void testIndexOutOfBoundsSubList() {
        int count = 3;
        SubList<Integer> list = new SubList<Integer>(Arrays.asList(makeIntegers(count)));
        list.subList(count, count); // ok
        assertIndexOutOfBoundsOnSubList(list, count, count + 1);
        assertIndexOutOfBoundsOnSubList(list, 0, count + 1);
        assertIndexOutOfBoundsOnSubList(list, count + 1, count + 1);
        assertIndexOutOfBoundsOnSubList(list, count, count - 1);
    }

    @Test
    public void testSameSubList() {
        int count = 3;
        SubList<Integer> list = new SubList<Integer>(Arrays.asList(makeIntegers(count)));
        assertSame(list, list.subList(0, count));
    }

    @Test
    public void testSameEmpty() {
        int count = 3;
        SubList<Integer> list = new SubList<Integer>(Arrays.asList(makeIntegers(count)));
        assertSame(SubList.empty(), list.subList(count, count));
        assertSame(SubList.empty(), list.subList(0, 0));
        assertSame(SubList.empty(), SubList.empty().subList(0, 0));
    }

    private void assertIndexOutOfBounds(List<?> list, int index) {
        try {
            list.get(index);
            fail("expected to fail at index " + index + "; list.size() is " + list.size());
        } catch (IndexOutOfBoundsException expected) {  }
    }

    private void assertIndexOutOfBoundsOnSubList(List<?> list, int start, int end) {
        try {
            list.subList(start, end);
            fail(
                "expected to fail on list.subList(" + start + ", " + end +
                "; list.size() is " + list.size());
        } catch (IndexOutOfBoundsException expected) {  }
    }

    private void traverse(List<?> list) {
        traverse(list, false);
    }

    private void traverse(List<?> list, boolean overflowOk) {
        String label = list.getClass().getSimpleName() + ".subList Traversal";
        long tick = System.currentTimeMillis();
        assertSubListTraversal(list, overflowOk);
        printLap(label, tick);
    }

    private void performRecursiveTest(int size) {
        performRecursiveTest(makeIntegers(size));
    }

    private Integer[] makeIntegers(int size) {
        Integer[] v = new Integer[size];
        for (int i = size; i > 0; ) {
            Integer ii = i;
            v[--i] = ii;
        }
        return v;
    }

    private void performRecursiveTest(Integer[] v) {
        assertSubList(new SubList<Integer>(Arrays.asList(v)), true);
    }

    private void assertSubList(List<?> list) {
        assertSubList(list, true);
    }

    private void assertSubList(List<?> list, boolean checkEfficient) {
        // Note: java.util.AbstractList.subList fails this
        if (checkEfficient)
            assertTrue(list == list.subList(0, list.size()));
        assertEquals(0, list.subList(0, 0).size());
        if (list.size() >= 2) {
            int pivot = list.size() / 2;
            List<?> left =  list.subList(0, pivot);
            List<?> right = list.subList(pivot, list.size());
            assertEquals(list.size(), left.size() + right.size());
            assertEquals(list.get(pivot - 1), left.get(pivot - 1));
            assertEquals(list.get(pivot), right.get(0));

            // recurse (so we test subList of subList)
            assertSubList(left, checkEfficient);
            assertSubList(right, checkEfficient);
        }
    }


    private void assertSubListTraversal(List<?> list, boolean overflowOk) {
        int i = 1;
        try {
            List<?> sub = list;
            for (; i < list.size(); ) {
                sub = sub.subList(1, sub.size());
                assertEquals(list.get(i++), sub.get(0));
            }
            assertEquals(1, sub.size());
        } catch (StackOverflowError soe) {
            PrintStream out = overflowOk ? System.out : System.err;
            out.println("[" + list.getClass().getSimpleName() + "]: error at (" +
                    new DecimalFormat("#,###").format(i) + ") " + soe);
            if (!overflowOk)
                throw soe;
        }
    }


}
