/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by babak on 8/12/15.
 */
public class SortedListUnionTest {

    @Test
    public void testEmpty() {
        List<Integer> a = Collections.emptyList();
        assertSame(a, SortedListUnion.asUnion(a, a));
    }

    @Test
    public void testTrivial() {
        List<Integer> a = Collections.singletonList(0);
        List<Integer> b = Collections.singletonList(1);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(2, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(1, union.get(1).intValue());
    }

    @Test
    public void testTrivial2() {
        List<Integer> a = Collections.singletonList(0);
        List<Integer> b = Collections.singletonList(1);
        List<Integer> union = SortedListUnion.asUnion(b, a);
        assertEquals(2, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(1, union.get(1).intValue());
    }

    @Test
    public void testTrivial3() {
        List<Integer> a = Collections.singletonList(1);
        List<Integer> union = SortedListUnion.asUnion(a, a);
        assertEquals(2, union.size());
        assertEquals(1, union.get(1).intValue());
        assertEquals(1, union.get(0).intValue());
    }

    @Test
    public void testTrivial4() {
        List<Integer> a = Collections.singletonList(0);
        List<Integer> b = Collections.singletonList(1);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(2, union.size());
        assertEquals(1, union.get(1).intValue());
        assertEquals(0, union.get(0).intValue());
    }

    @Test
    public void testWith3Elements() {
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(2);
        List<Integer> b = Collections.singletonList(1);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(1, union.get(1).intValue());
        assertEquals(2, union.get(2).intValue());
    }

    @Test
    public void testWith3Elements2() {
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(2);
        List<Integer> b = Collections.singletonList(-1);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(-1, union.get(0).intValue());
        assertEquals(0, union.get(1).intValue());
        assertEquals(2, union.get(2).intValue());
    }

    @Test
    public void testWith3Elements3() {
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(2);
        List<Integer> b = Collections.singletonList(3);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(2, union.get(1).intValue());
        assertEquals(3, union.get(2).intValue());
    }

    @Test
    public void testWith3Elements4() {
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(2);
        List<Integer> b = Collections.singletonList(0);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(0, union.get(1).intValue());
        assertEquals(2, union.get(2).intValue());
    }

    @Test
    public void testWith3Elements5() {
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(2);
        List<Integer> b = Collections.singletonList(2);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(0, union.get(0).intValue());
        assertEquals(2, union.get(1).intValue());
        assertEquals(2, union.get(2).intValue());
    }

    @Test
    public void testWith3Elements6() {
        List<Integer> a = new ArrayList<>();
        a.add(2);
        a.add(2);
        List<Integer> b = Collections.singletonList(2);
        List<Integer> union = SortedListUnion.asUnion(a, b);
        assertEquals(3, union.size());
        assertEquals(2, union.get(0).intValue());
        assertEquals(2, union.get(1).intValue());
        assertEquals(2, union.get(2).intValue());
    }

    @Test
    public void testWith4Elements() {
        Integer[] a = new Integer[] {
                -3, 4,
        };
        Integer[] b = new Integer[] {
                -2, 5,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements2() {
        Integer[] a = new Integer[] {
                -13, 4,
        };
        Integer[] b = new Integer[] {
                -2, 3,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements3() {
        Integer[] a = new Integer[] {
                -3, 4,
        };
        Integer[] b = new Integer[] {
                5, 7,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements4() {
        Integer[] a = new Integer[] {
                5, 7,
        };
        Integer[] b = new Integer[] {
                -3, -2,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements5() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                -3,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements6() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                5,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements7() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                6,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements8() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                7,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements9() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                8,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements10() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                59,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith4Elements11() {
        Integer[] a = new Integer[] {
                5, 7, 59
        };
        Integer[] b = new Integer[] {
                60,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                -3, -2,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements2() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                -3, 5,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements3() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                -3, 6,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements4() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                5, 6,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements5() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                5, 7,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements6() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                5, 8,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements7() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                5, 16,
        };
        testImpl(a, b);
    }

    @Test
    public void testWith5Elements8() {
        Integer[] a = new Integer[] {
                5, 7, 16
        };
        Integer[] b = new Integer[] {
                5, 17,
        };
        testImpl(a, b);
    }

    @Test
    public void testSome() {
        Integer[] a = new Integer[] {
                5, 7, 16, 17, 23, 26, 26, 26, 30, 52,
        };
        Integer[] b = new Integer[] {
                6, 17, 26, 29
        };
        testImpl(a, b);
    }

    @Test
    public void testSome2() {
        Integer[] a = new Integer[] {
                5, 7, 16, 17, 23, 26, 26, 26, 34, 52,
        };
        Integer[] b = new Integer[] {
                6, 17, 26, 31
        };
        testImpl(a, b);
    }

    @Test
    public void testSome3() {
        Integer[] a = new Integer[] {
                5, 7, 16, 17, 23, 26, 26, 26, 26, 26, 26, 34, 52,
        };
        Integer[] b = new Integer[] {
                6, 17, 26, 26, 31
        };
        testImpl(a, b);
    }


    private void testImpl(Integer[] aa, Integer[] bb) {
        List<Integer> a = Arrays.asList(aa);
        List<Integer> b = Arrays.asList(bb);

        ArrayList<Integer> expected = new ArrayList<>();
        expected.addAll(a);
        expected.addAll(b);
        Collections.sort(expected);

        List<Integer> union = SortedListUnion.asUnion(a, b);

        final int size = expected.size();
        assertEquals(size, union.size());
        int pseudoIndex = Math.abs(expected.hashCode());
        if (pseudoIndex < size)
            pseudoIndex += size;

//        System.out.println("random index: " + pseudoIndex % size);
        for (int count = size; count-- > 0; --pseudoIndex) {
            int index = pseudoIndex % size;
            assertEquals(expected.get(index), union.get(index));
        }
    }
}
