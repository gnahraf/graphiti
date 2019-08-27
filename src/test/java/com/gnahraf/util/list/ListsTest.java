/*
 * Copyright 2015 Babak Farhang
 */
package com.gnahraf.util.list;



import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by babak on 2/28/15.
 */
public class ListsTest {



    @Test
    public void testReversedView() {
        testReversedViewImpl(10);
    }

    @Test
    public void testTrivialReversedView() {
        testReversedViewImpl(1);
    }

    @Test
    public void testMinimalReversedView() {
        testReversedViewImpl(2);
    }

    @Test
    public void testReversedView3() {
        testReversedViewImpl(3);
    }




    private void testReversedViewImpl(final int size) {
        List<Integer> original = new ArrayList<>();
        for (int i = 0; i < size; ++i)
            original.add(i);

        List<Integer> reversedView = Lists.reversedView(original);

        assertEquals(size, reversedView.size());
        for (int i = 0, v = size - 1; i < size; ++i, --v)
            assertEquals(v, reversedView.get(i).intValue());
    }


    @Test
    public void testDistinctUnion_Empty() {
        List<Integer> a = Collections.emptyList();
        testDistinctUnionImpl(a, a);
    }

    @Test
    public void testDistinctUnion_WithEmpty() {
        List<Integer> a = Collections.emptyList();
        List<Integer> b = Collections.singletonList(-1);
        testDistinctUnionImpl(a, b);
    }

    @Test
    public void testDistinctUnion_WithEmpty2() {
        List<Integer> a = Collections.emptyList();
        List<Integer> b = Collections.singletonList(1);
        testDistinctUnionImpl(b, a);
    }


    @Test
    public void testDistinctUnion_Trivial() {
        List<Integer> a = Collections.singletonList(1);
        List<Integer> b = Collections.singletonList(-1);
        testDistinctUnionImpl(a, b);
    }

    @Test
    public void testDistinctUnion_Trivial2() {
        List<Integer> a = Collections.singletonList(1);
        List<Integer> b = Collections.singletonList(-1);
        testDistinctUnionImpl(b, a);
    }

    @Test
    public void testDistinctUnion() {
        Integer[] a = {
              1, 5, 13, 27, 31, 32, 33, 48, 55, 56, 60, 66, 81,
        };
        Integer[] b = {
            3, 7, 11, 12, 29, 34,
        };
        testDistinctUnionImpl(a, b);
    }



    private void testDistinctUnionImpl(Integer[] a, Integer[] b) {
        testDistinctUnionImpl(Lists.asList(a), Lists.asList(b));
    }


    private void testDistinctUnionImpl(List<Integer> a, List<Integer> b) {
        ArrayList<Integer> expected = new ArrayList<>(a);
        expected.removeAll(b);
        expected.addAll(b);
        Collections.sort(expected);

        List<Integer> union = Lists.distinctUnion(a, b);
        assertEquals(expected, union);
    }
}
