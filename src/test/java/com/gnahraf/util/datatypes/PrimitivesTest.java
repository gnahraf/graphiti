/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;


import org.junit.Test;


import static com.gnahraf.util.datatypes.Primitives.*;
import static org.junit.Assert.*;

/**
 * Created by babak on 4/19/15.
 */
public class PrimitivesTest {


    @Test
    public void testSignedByte0() {
        int value = 0;
        byte expected = 0;
        byte actual = signedByte(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSignedByte127() {
        int value = 127;
        byte expected = 127;
        byte actual = signedByte(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSignedByte_127() {
        int value = -127;
        byte expected = -127;
        byte actual = signedByte(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSignedByte_1() {
        int value = -1;
        byte expected = -1;
        byte actual = signedByte(value);
        assertEquals(expected, actual);
    }


    @Test
    public void testSignedByte_128() {
        int value = -128;
        byte expected = -128;
        byte actual = signedByte(value);
        assertEquals(expected, actual);
    }


    @Test
    public void testSignedByte128() {
        assertSignedByteFail(128);
    }


    @Test
    public void testSignedByte_129() {
        assertSignedByteFail(-129);
    }


    @Test
    public void testUnsignedByte0() {
        assertUnsignedByteRoundtrip(0);
    }


    @Test
    public void testUnsignedByte1() {
        assertUnsignedByteRoundtrip(1);
    }


    @Test
    public void testUnsignedByte127() {
        assertUnsignedByteRoundtrip(127);
    }


    @Test
    public void testUnsignedByte128() {
        assertUnsignedByteRoundtrip(128);
    }


    @Test
    public void testUnsignedByte255() {
        assertUnsignedByteRoundtrip(255);
    }


    @Test
    public void testUnsignedByte256() {
        assertUnsignedByteFail(256);
    }


    @Test
    public void testUnsignedByte_1() {
        assertUnsignedByteFail(-1);
    }


    @Test
    public void testUnsignedByte_2() {
        assertUnsignedByteFail(-2);
    }


    @Test
    public void testUnsignedShortMax() {
        assertUnsignedShortRoundtrip(256 * 256 - 1);
    }


    @Test
    public void testSignedShort257() {
        int value = 257;
        assertEquals(value, signedShort(value));
    }

    @Test
    public void testSignedShort_257() {
        int value = -257;
        assertEquals(value, signedShort(value));
    }

    @Test
    public void testSignedShortMin() {
        int value = Short.MAX_VALUE;
        assertEquals(value, signedShort(value));
    }

    @Test
    public void testSignedShortMax() {
        int value = Short.MIN_VALUE;
        assertEquals(value, signedShort(value));
    }

    @Test
    public void testSignedShortOverflow() {
        assertSignedShortFail(Short.MIN_VALUE - 1);
        assertSignedShortFail(Short.MAX_VALUE + 1);
    }



    private void assertUnsignedByteRoundtrip(int value) {
        byte unsigned = unsignedByte(value);
        assertEquals(value, unsign(unsigned));
    }


    private void assertUnsignedShortRoundtrip(int value) {
        short unsigned = unsignedShort(value);
        assertEquals(value, unsign(unsigned));
    }

    private void assertSignedShortFail(int value) {
        try {
            signedShort(value);
            fail();
        } catch (ArithmeticException ax) {
            System.out.println("expected failure " + ax);
        }
    }

    private void assertSignedByteFail(int value) {
        try {
            signedByte(value);
            fail();
        } catch (ArithmeticException ax) {
            System.out.println("expected failure " + ax);
        }
    }


    private void assertUnsignedByteFail(int value) {
        try {
            unsignedByte(value);
            fail();
        } catch (ArithmeticException ax) {
            System.out.println("expected failure " + ax);
        }
    }
}
