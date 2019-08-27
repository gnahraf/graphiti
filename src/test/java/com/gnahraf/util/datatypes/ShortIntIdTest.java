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
public class ShortIntIdTest {

    static class Sid extends ShortIntId implements Comparable<Sid> {
        Sid(ShortIntId copy) { super(copy); }
        Sid(int type, int id) { super(type, id); }

        @Override
        public int compareTo(Sid another) {
            return super.compareTo(another);
        }
    }


    @Test
    public void testAttributes() {
        int type = -5;
        int id = 53;
        ShortIntId a = new ShortIntId(type, id);
        assertEquals(type, a.getType());
        assertEquals(id, a.getId());
    }


    @Test
    public void testCopyConstructor() {
        int type = -5;
        int id = 53;
        ShortIntId a = new ShortIntId(type, id);
        ShortIntId aa = new ShortIntId(a);
        assertEquals(type, aa.getType());
        assertEquals(id, aa.getId());
    }


    @Test
    public void testEquality() {
        ShortIntId a = new ShortIntId(28671, 83);
        ShortIntId aa = new ShortIntId(a);
        assertEquals(a, a);
        assertEqualsWithHashCheck(a, aa);
        ShortIntId ab = new ShortIntId(a.getType(), a.getId() + 1);
        assertNotEqual(a, ab);
        ShortIntId ba = new ShortIntId(a.getType() + 1, a.getId());
        assertNotEqual(ba, a);
        assertNotEqual(a, new Sid(a));
        assertEquals(new Sid(a), new Sid(a));
    }


    @Test
    public void testCompareTo() {
        ShortIntId min = new Sid(Short.MIN_VALUE, Integer.MIN_VALUE);
        ShortIntId a = new Sid(28671, 83);
        ShortIntId max = new Sid(Short.MAX_VALUE, Integer.MAX_VALUE);
        assertOrdered(min, a, max);
    }

}
