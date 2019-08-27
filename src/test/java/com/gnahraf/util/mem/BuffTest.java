/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.mem;


import com.gnahraf.util.datatypes.Primitives;
import com.gnahraf.util.mem.Buff;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by babak on 6/12/15.
 */
public class BuffTest {

    @Test
    public void testShort() {
        Buff buff = new Buff(1021);
        short value = 30197;
        int index = 34;
        assertRoundtripShort(value, buff, index);
        assertRoundtripShort((short) -value, buff, index);

        buff = buff.sub(3, 9);
        index = buff.size() - 2;
        assertRoundtripShort(value, buff, index);

        assertBoundsCheckShort(buff, -1);
        assertBoundsCheckShort(buff, buff.size());
        assertBoundsCheckShort(buff, buff.size() - 1);

        assertRoundtripShort(Short.MIN_VALUE, buff, 0);
        assertRoundtripShort(Short.MAX_VALUE, buff, 0);

    }


    @Test
    public void testInt() {
        Buff buff = new Buff(1021).sub(57, 234);

        int index = 5;
        assertRoundtripInt(7, buff, index);
        assertRoundtripInt(-7, buff, index);

        assertBoundsCheckInt(buff, -1);
        assertBoundsCheckInt(buff, buff.size());
        assertBoundsCheckInt(buff, buff.size() - 3);

        assertRoundtripInt(Integer.MIN_VALUE, buff, 55);
        assertRoundtripInt(Integer.MAX_VALUE, buff, buff.size() - 4);
    }


    @Test
    public void testTryte() {
        Buff buff = new Buff(1081).sub(59, 233);
        int index = 76;

        assertRoundtripTryte(0, buff, index);
        assertRoundtripTryte(7, buff, index);
        assertRoundtripTryte(2 * (int) Short.MAX_VALUE, buff, 1);
        assertRoundtripTryte(Primitives.MAX_TRYTE, buff, buff.size() - 3);

        assertBoundsCheckTryte(buff, buff.size() - 2);
        assertBoundsCheckTryte(buff, -1);

        assertTryteOverflow(buff, Primitives.MAX_TRYTE + 1);
        assertTryteOverflow(buff, -1);
        assertTryteOverflow(buff, Short.MIN_VALUE);

    }


    void assertRoundtripShort(short value, Buff buff, int index) {
        buff.putShort(value, index);
        assertEquals(value, buff.getShort(index));
    }

    void assertRoundtripInt(int value, Buff buff, int index) {
        buff.putInt(value, index);
        assertEquals(value, buff.getInt(index));
    }


    void assertRoundtripTryte(int value, Buff buff, int index) {
        buff.putTryte(value, index);
        assertEquals(value, buff.getTryte(index));
    }

    void assertBoundsCheckShort(Buff buff, int index) {
        try {
            buff.getShort(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }

        try {
            short anyval = 0;
            buff.putShort(anyval, index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }
    }


    void assertBoundsCheckInt(Buff buff, int index) {
        try {
            buff.getInt(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }

        try {
            buff.putInt(0, index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }
    }


    void assertBoundsCheckTryte(Buff buff, int index) {
        try {
            buff.getTryte(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }

        try {
            buff.putTryte(0, index);
            fail();
        } catch (IndexOutOfBoundsException expected) {   }
    }


    void assertTryteOverflow(Buff buff, int value) {
        try {
            buff.putTryte(value, 0);
            fail();
        } catch (ArithmeticException expected) {   }
    }
}
