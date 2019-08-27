/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import com.gnahraf.util.Comparators;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 *
 */
public class ListIntersectorTest {


    @Test
    public void testEmpty() {
        List<Integer>[] lists = new List[2];
        lists[0] = lists[1] = Collections.emptyList();
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertAtEnd(intersector);
    }

    @Test
    public void testTrivial() {
        List<Integer>[] lists = new List[2];
        lists[0] = Collections.emptyList();
        lists[1] = Collections.singletonList(1);
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertAtEnd(intersector);
    }

    @Test
    public void testTrivial2() {
        List<Integer>[] lists = new List[2];
        lists[1] = Collections.emptyList();
        lists[0] = Collections.singletonList(1);
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertAtEnd(intersector);
    }

    @Test
    public void testTrivial3() {
        List<Integer>[] lists = new List[3];
        lists[0] = Collections.singletonList(0);
        lists[1] = Collections.singletonList(1);
        lists[2] = Collections.singletonList(2);
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertAtEnd(intersector);
    }

    @Test
    public void testTrivial4() {
        List<Integer>[] lists = new List[3];
        lists[0] = Collections.singletonList(0);
        lists[1] = Collections.singletonList(1);
        lists[2] = Collections.singletonList(0);
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertAtEnd(intersector);
    }

    @Test
    public void testMinimal() {
        List<Integer>[] lists = new List[2];
        lists[0] = Collections.singletonList(1);
        lists[1] = Collections.singletonList(1);
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertTrue(intersector.hasNext());
        assertEquals(1, intersector.next().intValue());
        assertAtEnd(intersector);
    }

    @Test
    public void testMinimal2() {
        List<Integer>[] lists = new List[2];
        lists[0] = new ArrayList<>();
        lists[1] = new ArrayList<>();
        lists[0].add(-1);
        lists[0].add(3);
        lists[1].add(0);
        lists[1].add(3);
        lists[1].add(4);
        Iterator<Integer> expected = Collections.singleton(3).iterator();
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertIterator(expected, intersector);
    }

    @Test
    public void testMinimal3() {
        List<Integer>[] lists = new List[3];
        lists[0] = new ArrayList<>();
        lists[1] = new ArrayList<>();
        lists[2] = new ArrayList<>();

        lists[0].add(-1);
        lists[0].add(3);

        lists[1].add(0);
        lists[1].add(3);
        lists[1].add(4);

        lists[2].add(-1);
        lists[2].add(0);
        lists[2].add(3);
        lists[2].add(4);
        lists[2].add(5);

        Iterator<Integer> expected = Collections.singleton(3).iterator();
        Iterator<Integer> intersector = ListIntersector.newInstance(lists);
        assertIterator(expected, intersector);
    }


    @Test
    public void testAfew() {
        List<Integer>[] lists = new List[3];
        AccessList<Integer> twos = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 2;
            }
            @Override
            public int size() {
                return 120;
            }
        };
        AccessList<Integer> threes = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 3;
            }
            @Override
            public int size() {
                return 345;
            }
        };
        AccessList<Integer> fives = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 5;
            }
            @Override
            public int size() {
                return 50;
            }
        };
        lists[0] = fives;
        lists[1] = threes;
        lists[2] = twos;

        ArrayList<Integer> expected = new ArrayList<>();
        final int lcm = 2 * 3 * 5;
        for (int i = lcm; i <= 240; i += lcm)
            expected.add(i);

        assertIterator(expected.iterator(), ListIntersector.newInstance(lists));

        System.out.println("testAfew");
        System.out.println("========");
        printAccessList(twos);
        printAccessList(threes);
        printAccessList(fives);
    }


    @Test
    public void testAfew2() {
        List<Integer>[] lists = new List[3];
        AccessList<Integer> twos = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 2;
            }
            @Override
            public int size() {
                return 120;
            }
        };
        AccessList<Integer> threes = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 3;
            }
            @Override
            public int size() {
                return 345;
            }
        };
        AccessList<Integer> fives = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 5;
            }
            @Override
            public int size() {
                return 50;
            }
        };
        lists[0] = threes;
        lists[1] = twos;
        lists[2] = fives;

        ArrayList<Integer> expected = new ArrayList<>();
        final int lcm = 2 * 3 * 5;
        for (int i = lcm; i <= 240; i += lcm)
            expected.add(i);

        assertIterator(expected.iterator(), ListIntersector.newInstance(lists));

        System.out.println("testAfew2");
        System.out.println("=========");
        printAccessList(twos);
        printAccessList(threes);
        printAccessList(fives);
    }




    @Test
    public void testAfew3() {
        List<Integer>[] lists = new List[3];
        AccessList<Integer> twos = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 2;
            }
            @Override
            public int size() {
                return 120;
            }
        };
        AccessList<Integer> threes = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 3;
            }
            @Override
            public int size() {
                return 345;
            }
        };
        AccessList<Integer> fives = new AccessList<Integer>() {
            @Override
            protected Integer accessCountedGetImpl(int location) {
                return (1 + location) * 5;
            }
            @Override
            public int size() {
                return 50;
            }
        };
        lists[0] = threes;
        lists[1] = fives;
        lists[2] = twos;

        ArrayList<Integer> expected = new ArrayList<>();
        final int lcm = 2 * 3 * 5;
        for (int i = lcm; i <= 240; i += lcm)
            expected.add(i);

        assertIterator(expected.iterator(), ListIntersector.newInstance(lists));

        System.out.println("testAfew3");
        System.out.println("=========");
        printAccessList(twos);
        printAccessList(threes);
        printAccessList(fives);
    }


    private void printAccessList(AccessList<Integer> list) {
        System.out.println(
                "list(" + list.get(0) + ") access count " +
                        list.accessCount() + "; size " + list.size());
    }



    private <T extends Comparable<T>> void assertIterator(
            Iterator<T> expected,
            Iterator<T> actual) {

        Comparator<T> comparator = Comparators.naturalComparator();
        assertIterator(expected, actual, comparator);
    }

    private <T> void assertIterator(
            Iterator<T> expected, Iterator<T> actual, Comparator<T> comparator) {

        while (expected.hasNext()) {
            assertTrue(actual.hasNext());
            assertEquals(0, comparator.compare(expected.next(), actual.next()));
        }
        assertAtEnd(actual);
    }


    private void assertAtEnd(Iterator<?> iter) {
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail();
        } catch (NoSuchElementException expected) {  }
    }

}
