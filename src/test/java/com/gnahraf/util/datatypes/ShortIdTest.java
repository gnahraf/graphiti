/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


import org.junit.Test;


import static org.junit.Assert.*;
import static com.gnahraf.util.datatypes.OrderedTypeTest.*;

/**
 * Created by babak on 6/11/15.
 */
public class ShortIdTest {


    static class Sid extends ShortId implements Comparable<Sid> {

        Sid(int id) { super(id); }

        @Override
        public int compareTo(Sid another) {
            return super.compareTo(another);
        }
    }


    @Test
    public void testGetId() {
        short id = 3;
        ShortId si = new ShortId(id);
        assertEquals(id, si.getId());
    }

    @Test
    public void testSelfEquality() {
        short id = 5;
        ShortId si = new Sid(id);
        assertEquals(si, si);
    }

    @Test
    public void testEquality() {
        ShortId one = new ShortId(1);
        ShortId four = new ShortId(4);

        assertEqualsWithHashCheck(one, new ShortId(1));
        assertFalse(one.equals(four));
    }


    @Test
    public void testCompareTo() {
        ShortId one = new Sid(1);
        ShortId anotherOne = new Sid(1);
        ShortId four = new Sid(4);

        assertOrderedEquals(one, anotherOne);
        assertEquals(0, one.compareTo(one));
        assertOrdered(one, four);
    }


    @Test
    public void testCompareAtLimits() {
        ShortId one = new Sid(1);
        ShortId min = new Sid(Short.MIN_VALUE);
        ShortId max = new Sid(Short.MAX_VALUE);

        assertOrdered(min, one, max);
    }



    @Test
    public void testApplesAndOranges() {
        ShortId one = new ShortId(1);
        ShortId anotherOne = new Sid(1);
        assertFalse(one.equals(anotherOne));
        assertFalse(anotherOne.equals(one));
    }


    static class Fruit extends ShortId {
        Fruit(int id) { super(id); }

        @Override
        protected final boolean isEqualsCompat(Object o) {
            return o instanceof Fruit;
        }
    }

    static class Apple extends Fruit {
        Apple(int id) { super(id); }
    }


    @Test
    public void testFruitsAndApples() {
        int id = 1;
        ShortId sid = new ShortId(id);
        ShortId fruit = new Fruit(id);
        ShortId apple = new Apple(id);
        assertNotEqual(sid, fruit);
        assertNotEqual(sid, apple);
        assertEqualsWithHashCheck(fruit, apple);
    }



}
